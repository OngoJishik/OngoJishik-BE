package com.project.ongojisik.domain.boardlike.entity;

import com.project.ongojisik.domain.board.entity.Board;
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
@Table(name = "게시판 좋아요")
public class BoardLike {

    @Id
    @Column(name = "boardLikeId", nullable = false)
    private Long boardLikeId;

    @ManyToOne
    @JoinColumn(name = "boardId", nullable = false)
    private Board board;
}
