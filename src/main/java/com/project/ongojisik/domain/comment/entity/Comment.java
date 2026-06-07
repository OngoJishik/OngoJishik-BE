package com.project.ongojisik.domain.comment.entity;

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
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "comment_content", nullable = false)
    private String commentContent;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private Comment(Board board, User user, String commentContent, String authorName,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.board = board;
        this.user = user;
        this.commentContent = commentContent;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Comment create(Board board, User user, String commentContent) {
        LocalDateTime now = LocalDateTime.now();
        return new Comment(board, user, commentContent, user.getNickname(), now, now);
    }

    public void update(String commentContent) {
        this.commentContent = commentContent;
        this.updatedAt = LocalDateTime.now();
    }
}
