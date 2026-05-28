package com.project.ongojisik.domain.user.service;

import com.project.ongojisik.domain.user.dto.GoogleLoginRequest;
import com.project.ongojisik.domain.user.dto.TokenRefreshRequest;
import com.project.ongojisik.domain.user.dto.UserLoginResponse;
import com.project.ongojisik.domain.user.entity.User;
import com.project.ongojisik.domain.user.repository.UserRepository;
import com.project.ongojisik.global.auth.JwtTokenProvider;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final GoogleOAuthService googleOAuthService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserLoginResponse loginWithGoogle(GoogleLoginRequest request) {
        GoogleOAuthService.GoogleUserInfo googleUserInfo = googleOAuthService.verify(request.idToken());
        Optional<User> existingUser = userRepository.findByProviderId(googleUserInfo.providerId());
        boolean newUser = existingUser.isEmpty();

        User user = existingUser
                .map(savedUser -> {
                    savedUser.syncGoogleProfile(googleUserInfo.email(), googleUserInfo.nickname());
                    return savedUser;
                })
                .orElseGet(() -> userRepository.save(
                        User.create(
                                googleUserInfo.providerId(),
                                googleUserInfo.email(),
                                googleUserInfo.nickname()
                        )
                ));

        return createLoginResponse(user, newUser);
    }

    @Transactional(readOnly = true)
    public UserLoginResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new APIException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));

        return createLoginResponse(user, false);
    }

    private UserLoginResponse createLoginResponse(User user, boolean newUser) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        return new UserLoginResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                accessToken,
                refreshToken,
                newUser
        );
    }
}
