package com.team.comma.util.oauth;

import com.team.comma.constant.UserRole;
import com.team.comma.constant.UserType;
import com.team.comma.domain.User;
import com.team.comma.dto.SessionUser;
import com.team.comma.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    final private UserRepository userRepository;
    final private HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        // 현재 서비스중인 서버 ( 네이버 , 카카오 , 구글 )
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OAuth2 서버의 키값 ( 구글 = sub , 네이버 = response , 카카오 = id )
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        // 사용자 정보 값
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        if(user != null) { // email 정보가 없을 경우
            httpSession.setAttribute("user" , SessionUser.of(user)); // 세션 저장
        }

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), attributes.getAttributes() , attributes.getNameAttributeKey());
    }

    public User saveOrUpdate(OAuthAttributes attributes) {

        if(attributes.getEmail() == null) { // email 정보가 없으면 종료
            return null;
        }

        User user = userRepository.findByEmail(attributes.getEmail());

        if(user == null) { // 정보가 없을 때만
            User createUser = User.builder().email(attributes.getEmail()).name(attributes.getName())
                    .role(UserRole.USER).type(UserType.OAuthUser).build();
            return userRepository.save(createUser);
        }
        return user;
    }
}
