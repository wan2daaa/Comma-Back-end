package com.team.comma.spotify.archive.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.archive.domain.Archive;
import com.team.comma.spotify.archive.dto.ArchiveRequest;
import com.team.comma.spotify.archive.repository.ArchiveRepository;
import com.team.comma.spotify.playlist.exception.PlaylistException;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final ArchiveRepository archiveRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageResponse addArchive(String token , ArchiveRequest archiveRequest) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccountException("사용자를 찾을 수 없습니다."));
        Playlist playlist = playlistRepository.findById(archiveRequest.getPlaylistId())
                .orElseThrow(() -> new PlaylistException("Playlist를 찾을 수 없습니다."));

        Archive archive = Archive.createArchive(user , archiveRequest.getContent() , playlist);
        archiveRepository.save(archive);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

}
