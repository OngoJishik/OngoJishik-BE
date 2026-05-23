package com.project.ongojisik.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "유저")
public class User {

    @Id
    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "providerId")
    private String providerId;

    @Column(name = "nickname")
    private String nickname;
}
