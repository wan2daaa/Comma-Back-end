package com.team.comma.service;

import com.team.comma.dto.PlaylistResponse;
import com.team.comma.repository.MainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {
    private final MainRepository mainRepository;

    public List<PlaylistResponse> getUserPlaylist(final String email) {
        return mainRepository.findAllByUserEntity_Email(email);
    }

}
