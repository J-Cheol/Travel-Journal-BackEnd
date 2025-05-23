package com.traveljournal.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oauth2.google")
public class GoogleOAuthConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope;
    private String tokenUri;
    private String testRedirectUri;
}
