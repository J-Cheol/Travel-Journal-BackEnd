package com.traveljournal.domain.auth.util;

import com.traveljournal.domain.auth.dto.GoogleMemberInfo;
import com.traveljournal.domain.auth.dto.GoogleTokenResponse;
import com.traveljournal.global.config.GoogleOAuthConfig;
import com.traveljournal.global.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleClient {
    private final GoogleOAuthConfig googleOAuthConfig;
    private final RestTemplate restTemplate;

    /**
     * 구글 인증 코드로 토큰 정보 요청
     */
    public GoogleTokenResponse getGoogleToken(String code) {
        /*
        String url = "https://accounts.google.com/o/oauth2/v2/auth?";
        String body = "code=" + code +
                "&client_id=" + "${google.clientId}" +
                "&client_secret=" + "${google.clientSecret}" +
                "&redirect_uri=" + "${google.RedirectUri}" +
                "&grant_type=authorization_code";

        return restTemplate.postForObject(url, body, GoogleTokenResponse.class);

         */
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", googleOAuthConfig.getClientId());
            params.add("client_secret", googleOAuthConfig.getClientSecret());
            params.add("redirect_uri", googleOAuthConfig.getRedirectUri());
            params.add("grant_type", "authorization_code");
            log.info("🚀 Google OAuth 요청 - clientId: {}, clientSecret: {}, redirectUri: {}",
                    googleOAuthConfig.getClientId(),
                    googleOAuthConfig.getClientSecret(),
                    googleOAuthConfig.getRedirectUri());
            log.info("🚀 [Google OAuth] 토큰 요청 URL: {}", googleOAuthConfig.getTokenUri());
            log.info("🚀 [Google OAuth] 요청 파라미터: {}", params);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            GoogleTokenResponse response = restTemplate.postForObject(
                    "https://oauth2.googleapis.com/token",
                    request,
                    GoogleTokenResponse.class
            );

            log.info("✅ [Google OAuth] 토큰 발급 성공: {}", response);

            if (response == null) {
                throw new ExternalApiException("구글 토큰 응답이 null 입니다.");
            }

            log.info("구글 토큰 발급 성공");
            return response;
        } catch (Exception e) {
            log.error("구글 토큰 발급 실패 : {}", e.getMessage());
            throw new ExternalApiException("구글 토큰 발급에 실패했습니다." + e.getMessage());
        }
    }

    /**
     * 구글 액세스 토큰으로 사용자 정보 가져오기
     */

    public GoogleMemberInfo getGoogleMemberInfo(String accessToken) {
        String memberInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

        log.info("구글 사용자 정보 요청: {}", memberInfoUrl);

        return restTemplate.getForObject(memberInfoUrl, GoogleMemberInfo.class);

    }
}
