package com.team.comma.util.auth.support;

import com.team.comma.user.constant.UserRole;
import com.team.comma.util.security.domain.Token;
import com.team.comma.user.dto.UserSession;
import com.team.comma.util.jwt.support.CreationCookie;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    final private JwtTokenProvider jwtTokenProvider;
    final private HttpSession httpSession;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        UserSession user = (UserSession) httpSession.getAttribute("user");

        if (user == null) { // 이메일이 없을 때
            getRedirectStrategy().sendRedirect(request, response,
                createRedirectUrl("http://43.201.188.96:3000/oauth2/disallowance"));
            return;
        }

        Token token = jwtTokenProvider.createAccessToken(user.getEmail(), UserRole.USER);

        response.addCookie(CreationCookie.createAccessToken(token.getAccessToken()));
        response.addCookie(CreationCookie.createRefreshToken(token.getRefreshToken()));

        httpSession.removeAttribute("user"); // 세션 삭제

        getRedirectStrategy().sendRedirect(request, response,
            createRedirectUrl("http://43.201.188.96:3000"));
    }

    public String createRedirectUrl(String url) {
        return UriComponentsBuilder.fromUriString(url).build().toUriString();
    }


}

