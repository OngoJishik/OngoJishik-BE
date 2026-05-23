package com.project.ongojisik.domain.board.entity;

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
@Table(name = "게시판")
public class Board {

    @Id
    @Column(name = "boardId", nullable = false)
    private Long boardId;

    @Column(name = "CHAR")
    private String charField;

    @Column(name = "Field2")
    private String field2;

    @Column(name = "Field3")
    private String field3;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}
