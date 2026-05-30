package com.project.ongojisik.domain.board.repository;

import com.project.ongojisik.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
