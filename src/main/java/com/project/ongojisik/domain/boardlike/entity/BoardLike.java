package com.project.ongojisik.domain.boardlike.entity;

import com.project.ongojisik.domain.board.entity.Board;
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
        name = "board_likes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_board_likes_user_board",
                columnNames = {"user_id", "board_id"}
        )
)
public class BoardLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_like_id", nullable = false)
    private Long boardLikeId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private BoardLike(User user, Board board, LocalDateTime createdAt) {
        this.user = user;
        this.board = board;
        this.createdAt = createdAt;
    }

    public static BoardLike create(User user, Board board) {
        return new BoardLike(user, board, LocalDateTime.now());
    }
}
