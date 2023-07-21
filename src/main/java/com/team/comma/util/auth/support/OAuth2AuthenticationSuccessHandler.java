package com.team.comma.util.auth.support;

import com.team.comma.user.constant.UserRole;
import com.team.comma.util.security.domain.Token;
import com.team.comma.user.dto.UserSession;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static com.team.comma.util.jwt.support.CreationCookie.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    final private JwtTokenProvider jwtTokenProvider;
    final private HttpSession httpSession;

    @Value("${client.url}")
    private String clientUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        UserSession user = (UserSession) httpSession.getAttribute("user");

        if (user == null) {
            getRedirectStrategy().sendRedirect(request, response,
                createRedirectUrl(clientUrl + "/oauth2/disallowance"));
            return;
        }

        Token token = jwtTokenProvider.createAccessToken(user.getEmail(), UserRole.USER);

        response.addHeader("Set-Cookie" , createResponseAccessToken(token.getAccessToken()).toString());
        response.addHeader("Set-Cookie" , createResponseRefreshToken(token.getRefreshToken()).toString());

        httpSession.removeAttribute("user");

        getRedirectStrategy().sendRedirect(request, response, createRedirectUrl(clientUrl));
    }

    public String createRedirectUrl(String url) {
        return UriComponentsBuilder.fromUriString(url).build().toUriString();
    }

}

