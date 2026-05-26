package com.project.ongojisik.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.ongojisik.domain.user.dto.GoogleLoginRequest;
import com.project.ongojisik.domain.user.dto.TokenRefreshRequest;
import com.project.ongojisik.domain.user.dto.UserLoginResponse;
import com.project.ongojisik.domain.user.entity.User;
import com.project.ongojisik.domain.user.repository.UserRepository;
import com.project.ongojisik.global.auth.JwtTokenProvider;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoogleOAuthService googleOAuthService;

    private JwtTokenProvider jwtTokenProvider;
    private UserAuthService userAuthService;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "test-secret-key-for-jwt-provider-should-be-long-enough-123456",
                3600,
                1209600
        );
        userAuthService = new UserAuthService(userRepository, googleOAuthService, jwtTokenProvider);
    }

    @Test
    void loginWithGoogleCreatesUserWhenUserDoesNotExist() {
        GoogleOAuthService.GoogleUserInfo googleUserInfo =
                new GoogleOAuthService.GoogleUserInfo("google-123", "newuser@gmail.com", "새유저");
        User savedUser = User.create("google-123", "newuser@gmail.com", "새유저");
        ReflectionTestUtils.setField(savedUser, "userId", 1L);

        when(googleOAuthService.verify("valid-id-token")).thenReturn(googleUserInfo);
        when(userRepository.findByProviderId("google-123")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserLoginResponse response = userAuthService.loginWithGoogle(new GoogleLoginRequest("valid-id-token"));

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("newuser@gmail.com");
        assertThat(response.nickname()).isEqualTo("새유저");
        assertThat(response.newUser()).isTrue();
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void loginWithGoogleUpdatesUserWhenUserExists() {
        GoogleOAuthService.GoogleUserInfo googleUserInfo =
                new GoogleOAuthService.GoogleUserInfo("google-456", "updated@gmail.com", "수정닉네임");
        User existingUser = User.create("google-456", "old@gmail.com", "기존닉네임");
        ReflectionTestUtils.setField(existingUser, "userId", 2L);

        when(googleOAuthService.verify("valid-id-token")).thenReturn(googleUserInfo);
        when(userRepository.findByProviderId("google-456")).thenReturn(Optional.of(existingUser));

        UserLoginResponse response = userAuthService.loginWithGoogle(new GoogleLoginRequest("valid-id-token"));

        assertThat(response.userId()).isEqualTo(2L);
        assertThat(response.email()).isEqualTo("updated@gmail.com");
        assertThat(response.nickname()).isEqualTo("수정닉네임");
        assertThat(response.newUser()).isFalse();
        assertThat(existingUser.getEmail()).isEqualTo("updated@gmail.com");
        assertThat(existingUser.getNickname()).isEqualTo("수정닉네임");
    }

    @Test
    void refreshTokenThrowsExceptionWhenRefreshTokenIsInvalid() {
        assertThatThrownBy(() -> userAuthService.refreshToken(new TokenRefreshRequest("invalid-token")))
                .isInstanceOf(APIException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REFRESH_TOKEN_INVALID);
    }

    @Test
    void refreshTokenReissuesTokensWhenRefreshTokenIsValid() {
        User existingUser = User.create("google-789", "refresh@gmail.com", "리프레시유저");
        ReflectionTestUtils.setField(existingUser, "userId", 3L);
        String refreshToken = jwtTokenProvider.createRefreshToken(3L);

        when(userRepository.findById(3L)).thenReturn(Optional.of(existingUser));

        UserLoginResponse response = userAuthService.refreshToken(new TokenRefreshRequest(refreshToken));

        assertThat(response.userId()).isEqualTo(3L);
        assertThat(response.newUser()).isFalse();
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
    }
}
