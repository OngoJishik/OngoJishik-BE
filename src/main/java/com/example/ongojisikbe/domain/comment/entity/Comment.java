package com.example.ongojisikbe.domain.comment.entity;

import com.example.ongojisikbe.domain.board.entity.Board;
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
@Table(name = "댓글")
public class Comment {

    @Id
    @Column(name = "commentId", nullable = false)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "boardId", nullable = false)
    private Board board;

    @Column(name = "commentComtent")
    private String commentContent;

    @Column(name = "Field")
    private String field;
}
