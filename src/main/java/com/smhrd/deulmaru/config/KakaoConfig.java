package com.smhrd.deulmaru.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KakaoConfig {

    @Value("${kakao.api.client-id}")
    private String clientId;

    @Value("${kakao.api.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.api.token-url}")
    private String tokenUrl;

    @Value("${kakao.api.user-info-url}")
    private String userInfoUrl;
}
