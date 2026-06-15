package com.project.ongojisik.domain.bookmark.entity;

import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(
        name = "bookmark",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_bookmark_user_food",
                columnNames = {"user_id", "food_id"}
        )
)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id", nullable = false)
    private Long favoriteId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private Bookmark(User user, Food food, LocalDateTime createdAt) {
        this.user = user;
        this.food = food;
        this.createdAt = createdAt;
    }

    public static Bookmark create(User user, Food food) {
        return new Bookmark(user, food, LocalDateTime.now());
    }
}
