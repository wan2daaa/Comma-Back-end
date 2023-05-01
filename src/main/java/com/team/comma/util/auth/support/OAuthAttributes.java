package com.team.comma.util.auth.support;

import lombok.*;

import java.util.Map;

@Data
public final class OAuthAttributes {

    final private Map<String, Object> attributes;
    final private String nameAttributeKey;
    final private String name;
    final private String email;

    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
        Map<String, Object> attributes) {
        if (registrationId.equals("kakao")) {
            return ofKakao(userNameAttributeName, attributes);
        } else if (registrationId.equals("naver")) {
            return ofNaver(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName,
        Map<String, Object> attributes) {
        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get(
            "kakao_account");  // 카카오로 받은 데이터에서 계정 정보가 담긴 kakao_account 값을 꺼낸다.
        Map<String, Object> profile = (Map<String, Object>) kakao_account.get(
            "profile");   // 마찬가지로 profile(nickname, image_url.. 등) 정보가 담긴 값을 꺼낸다.

        return new OAuthAttributes(attributes,
            userNameAttributeName,
            (String) profile.get("nickname"),
            (String) kakao_account.get("email"));
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName,
        Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get(
            "response");    // 네이버에서 받은 데이터에서 프로필 정보다 담긴 response 값을 꺼낸다.

        return new OAuthAttributes(attributes,
            userNameAttributeName,
            (String) response.get("name"),
            (String) response.get("email"));
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName,
        Map<String, Object> attributes) {

        return new OAuthAttributes(attributes,
            userNameAttributeName,
            (String) attributes.get("name"),
            (String) attributes.get("email"));
    }

}
