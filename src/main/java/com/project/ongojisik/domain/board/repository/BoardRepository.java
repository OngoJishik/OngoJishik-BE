package com.project.ongojisik.domain.board.repository;

import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.entity.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findByCategory(BoardCategory category, Pageable pageable);

    Page<Board> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Board> findByTitleContainingIgnoreCaseAndCategory(String title, BoardCategory category, Pageable pageable);
}
