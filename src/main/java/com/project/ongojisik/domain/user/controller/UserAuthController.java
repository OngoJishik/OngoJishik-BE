package com.project.ongojisik.domain.user.controller;

import com.project.ongojisik.domain.user.dto.GoogleLoginRequest;
import com.project.ongojisik.domain.user.dto.TokenRefreshRequest;
import com.project.ongojisik.domain.user.dto.UserLoginResponse;
import com.project.ongojisik.domain.user.service.UserAuthService;
import com.project.ongojisik.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 인증", description = "구글 OAuth 기반 사용자 인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/auth")
public class UserAuthController {

    private final UserAuthService userAuthService;

    @Operation(summary = "구글 로그인", description = "구글 ID 토큰을 검증하고 서비스 JWT를 발급합니다.")
    @PostMapping("/google")
    public ApiResponse<UserLoginResponse> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        return ApiResponse.success(userAuthService.loginWithGoogle(request));
    }

    @Operation(summary = "토큰 재발급", description = "JWT 액세스 토큰과 리프레시 토큰을 재발급합니다.")
    @PostMapping("/refresh")
    public ApiResponse<UserLoginResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ApiResponse.success(userAuthService.refreshToken(request));
    }
}
