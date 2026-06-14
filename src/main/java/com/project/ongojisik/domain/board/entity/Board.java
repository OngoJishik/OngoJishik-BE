package com.project.ongojisik.domain.board.entity;

import com.project.ongojisik.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private BoardCategory category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private Board(User user, String title, String content, String imageUrl, BoardCategory category, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Board create(User user, String title, String content, String imageUrl, BoardCategory category) {
        LocalDateTime now = LocalDateTime.now();
        return new Board(user, title, content, imageUrl, category, now, now);
    }

    public void update(String title, String content, String imageUrl, BoardCategory category) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }
}
