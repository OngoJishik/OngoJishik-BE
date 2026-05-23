package com.project.ongojisik.domain.bookmark.entity;

import com.project.ongojisik.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "즐겨찾기")
public class Bookmark {

    @Id
    @Column(name = "bookmarkId", nullable = false)
    private Long bookmarkId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}
