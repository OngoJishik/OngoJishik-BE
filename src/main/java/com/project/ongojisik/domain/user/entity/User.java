package com.project.ongojisik.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "provider_id", nullable = false, unique = true)
    private String providerId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    private User(String providerId, String email, String nickname) {
        this.providerId = providerId;
        this.email = email;
        this.nickname = nickname;
    }

    public static User create(String providerId, String email, String nickname) {
        return new User(providerId, email, nickname);
    }

    public void syncGoogleProfile(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
