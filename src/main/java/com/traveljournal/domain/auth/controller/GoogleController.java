package com.traveljournal.domain.auth.controller;

import com.traveljournal.domain.auth.dto.LoginCombinedResponse;
import com.traveljournal.domain.auth.dto.LoginResponse;
import com.traveljournal.domain.auth.service.AuthService;
import com.traveljournal.global.data.ApiResponse;
import com.traveljournal.global.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GoogleController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;


    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<LoginResponse> googleCallback(
            @Parameter(description = "구글에서 반환한 인증 코드", required = true)
            @RequestParam String code,
            @Parameter(description = "디바이스 ID")
            @RequestParam(required = false) String deviceId
    ) {
        log.info("구글 로그인 콜백 호출됨 : {}", code);
        LoginCombinedResponse loginCombinedResponse = authService.processGoogleLoginWithCode(code, deviceId);
        return ApiResponse.accessTokenResponse(loginCombinedResponse.LoginResponse(), loginCombinedResponse.accessToken());
    }
}
