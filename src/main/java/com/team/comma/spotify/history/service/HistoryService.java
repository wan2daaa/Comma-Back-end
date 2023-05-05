package com.team.comma.spotify.history.service;

import com.team.comma.spotify.history.dto.HistoryRequest;
import com.team.comma.spotify.history.dto.HistoryResponse;
import com.team.comma.spotify.history.repository.HistoryRepository;
import com.team.comma.spotify.search.dto.RequestResponse;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.util.List;

import static com.team.comma.common.constant.ResponseCode.REQUEST_SUCCESS;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void addHistory(HistoryRequest history , String token) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userEmail);

        if(user == null) {
            throw new AccountException("사용자를 찾을 수 없습니다.");
        }

        user.addHistory(history.getSearchHistory());
    }

    public RequestResponse getHistoryList(String token) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userEmail);

        if(user == null) {
            throw new AccountException("사용자를 찾을 수 없습니다.");
        }

        List<HistoryResponse> historyList = historyRepository.getHistoryListByUserEmail(user.getEmail());
        return RequestResponse.of(REQUEST_SUCCESS , historyList);
    }

    @Transactional
    public void deleteHistory(long historyId) {
        historyRepository.deleteHistoryById(historyId);
    }

    @Transactional
    public void deleteAllHistory(String token) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userEmail);

        if(user == null) {
            throw new AccountException("사용자를 찾을 수 없습니다.");
        }

        historyRepository.deleteAllHistoryByUser(user);
    }
}
