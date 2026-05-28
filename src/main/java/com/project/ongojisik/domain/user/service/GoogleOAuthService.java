package com.project.ongojisik.domain.user.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleOAuthService {

    private final String googleClientId;

    public GoogleOAuthService(@Value("${google.oauth.client-id:}") String googleClientId) {
        this.googleClientId = googleClientId;
    }

    public GoogleUserInfo verify(String idToken) {
        if (googleClientId.isBlank()) {
            throw new APIException(ErrorCode.GOOGLE_CLIENT_ID_NOT_CONFIGURED);
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(List.of(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new APIException(ErrorCode.GOOGLE_INVALID_TOKEN);
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String providerId = payload.getSubject();
            String email = payload.getEmail();
            String nickname = (String) payload.get("name");
            boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());

            if (providerId == null || email == null || !emailVerified) {
                throw new APIException(ErrorCode.GOOGLE_ACCOUNT_INFO_INVALID);
            }

            String resolvedNickname;
            if (nickname == null || nickname.isBlank()) {
                resolvedNickname = email;
            } else {
                resolvedNickname = nickname;
            }
            return new GoogleUserInfo(providerId, email, resolvedNickname);
        } catch (GeneralSecurityException | IOException exception) {
            throw new APIException(ErrorCode.GOOGLE_TOKEN_VERIFICATION_FAILED);
        }
    }

    public record GoogleUserInfo(
            String providerId,
            String email,
            String nickname
    ) {
    }
}
