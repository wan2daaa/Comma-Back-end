package com.team.comma.spotify.history.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.history.dto.HistoryRequest;
import com.team.comma.spotify.history.dto.HistoryResponse;
import com.team.comma.spotify.history.repository.HistoryRepository;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.util.List;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MessageResponse addHistory(HistoryRequest history , String token) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccountException("사용자를 찾을 수 없습니다."));

        user.addHistory(history.getSearchHistory());

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse getHistoryList(String token) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccountException("사용자를 찾을 수 없습니다."));

        List<HistoryResponse> historyList = historyRepository.getHistoryListByUserEmail(user.getEmail());

        return MessageResponse.of(REQUEST_SUCCESS , historyList);
    }

    @Transactional
    public MessageResponse deleteHistory(long historyId) {
        historyRepository.deleteHistoryById(historyId);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    @Transactional
    public MessageResponse deleteAllHistory(String token) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccountException("사용자를 찾을 수 없습니다."));

        historyRepository.deleteAllHistoryByUser(user);

        return MessageResponse.of(REQUEST_SUCCESS);
    }
}
