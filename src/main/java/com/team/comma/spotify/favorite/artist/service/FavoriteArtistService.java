package com.team.comma.spotify.favorite.artist.service;

import com.team.comma.common.constant.ResponseCodeEnum;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.favorite.artist.exception.FavoriteArtistException;
import com.team.comma.spotify.favorite.artist.repository.FavoriteArtistRepository;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteArtistService {

    private final FavoriteArtistRepository favoriteArtistRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public MessageResponse isFavoriteArtist(String token , String artistName) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccountException("사용자를 찾을 수 없습니다."));
        boolean isAddedArtist = false;

        if(isAddedFavoriteArtist(user , artistName)) {
            isAddedArtist = true;
        }

        return MessageResponse.of(ResponseCodeEnum.REQUEST_SUCCESS , isAddedArtist);
    }

    public boolean isAddedFavoriteArtist(User user , String artistName) {
        if(favoriteArtistRepository.findFavoriteArtistByUser(user , artistName).isPresent()) {
            return true;
        };
        return false;
    }

    @Transactional
    public MessageResponse addFavoriteArtist(String token , String artistName) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccountException("사용자를 찾을 수 없습니다."));

        if(isAddedFavoriteArtist(user , artistName)) {
            throw new FavoriteArtistException("이미 추가된 관심 아티스트입니다.");
        }

        user.addFavoriteArtist(artistName);

        return MessageResponse.of(ResponseCodeEnum.REQUEST_SUCCESS);
    }

    @Transactional
    public MessageResponse deleteFavoriteArtist(String token , String artistName) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccountException("사용자를 찾을 수 없습니다."));

        favoriteArtistRepository.deleteByUser(user , artistName);

        return MessageResponse.of(ResponseCodeEnum.REQUEST_SUCCESS);
    }

    public List<String> getFavoriteArtistList(String token) throws AccountException {
        String userName = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new AccountException("사용자를 찾을 수 없습니다."));

        return favoriteArtistRepository.findFavoriteArtistListByUser(user);
    }

}
