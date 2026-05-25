package com.project.ongojisik.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank(message = "구글 ID 토큰은 필수입니다.")
        String idToken
) {
}
