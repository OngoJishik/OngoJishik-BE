package com.project.ongojisik.domain.board.entity;

import com.project.ongojisik.domain.user.entity.User;
import com.project.ongojisik.global.converter.StringListJsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import java.util.ArrayList;
import java.util.List;
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

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "image_urls", nullable = false, columnDefinition = "TEXT")
    private List<String> imageUrls;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private BoardCategory category;

    @Column(name = "recipe_id")
    private String recipeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private Board(User user, String title, String content, List<String> imageUrls, BoardCategory category, String recipeId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.title = title;
        this.content = content;
        if (imageUrls == null) {
            this.imageUrls = new ArrayList<>();
        } else {
            this.imageUrls = new ArrayList<>(imageUrls);
        }
        this.category = category;
        this.recipeId = recipeId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Board create(User user, String title, String content, List<String> imageUrls, BoardCategory category, String recipeId) {
        LocalDateTime now = LocalDateTime.now();
        return new Board(user, title, content, imageUrls, category, recipeId, now, now);
    }

    public void update(String title, String content, List<String> imageUrls, BoardCategory category, String recipeId) {
        this.title = title;
        this.content = content;
        if (imageUrls == null) {
            this.imageUrls = new ArrayList<>();
        } else {
            this.imageUrls = new ArrayList<>(imageUrls);
        }
        this.category = category;
        this.recipeId = recipeId;
        this.updatedAt = LocalDateTime.now();
    }

    public String getImageUrl() {
        return imageUrls == null || imageUrls.isEmpty() ? null : imageUrls.get(0);
    }

    public void assignBoardId(Long boardId) {
        this.boardId = boardId;
    }
}
