package com.project.ongojisik.global.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "test-secret-key-for-jwt-provider-should-be-long-enough-123456",
                3600,
                1209600
        );
    }

    @Test
    void createAccessTokenCreatesValidAccessToken() {
        String accessToken = jwtTokenProvider.createAccessToken(1L);

        assertThat(jwtTokenProvider.validateToken(accessToken)).isTrue();
        assertThat(jwtTokenProvider.isAccessToken(accessToken)).isTrue();
        assertThat(jwtTokenProvider.isRefreshToken(accessToken)).isFalse();
        assertThat(jwtTokenProvider.getUserId(accessToken)).isEqualTo(1L);
    }

    @Test
    void createRefreshTokenCreatesValidRefreshToken() {
        String refreshToken = jwtTokenProvider.createRefreshToken(2L);

        assertThat(jwtTokenProvider.validateToken(refreshToken)).isTrue();
        assertThat(jwtTokenProvider.isRefreshToken(refreshToken)).isTrue();
        assertThat(jwtTokenProvider.isAccessToken(refreshToken)).isFalse();
        assertThat(jwtTokenProvider.getUserId(refreshToken)).isEqualTo(2L);
    }

    @Test
    void validateTokenReturnsFalseForInvalidToken() {
        assertThat(jwtTokenProvider.validateToken("invalid-token")).isFalse();
    }
}
