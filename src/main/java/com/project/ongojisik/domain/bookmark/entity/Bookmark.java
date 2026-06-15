package com.project.ongojisik.domain.bookmark.entity;

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
        name = "bookmarks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_bookmarks_user_board",
                columnNames = {"user_id", "board_id"}
        )
)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id", nullable = false)
    private Long bookmarkId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private Bookmark(User user, Board board, LocalDateTime createdAt) {
        this.user = user;
        this.board = board;
        this.createdAt = createdAt;
    }

    public static Bookmark create(User user, Board board) {
        return new Bookmark(user, board, LocalDateTime.now());
    }

    public void assignBookmarkId(Long bookmarkId) {
        this.bookmarkId = bookmarkId;
    }
}
