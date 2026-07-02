package com.project.ongojisik.domain.boardlike.repository;

import com.project.ongojisik.domain.boardlike.entity.BoardLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    @Query("""
            select bl
            from BoardLike bl
            where bl.user.userId = :userId
              and bl.board.boardId = :boardId
            """)
    Optional<BoardLike> findByUserUserIdAndBoardBoardId(
            @Param("userId") Long userId,
            @Param("boardId") Long boardId
    );

    @Query("""
            select count(bl)
            from BoardLike bl
            where bl.board.boardId = :boardId
            """)
    Long countByBoardBoardId(@Param("boardId") Long boardId);

    @Modifying(clearAutomatically = true)
    @Query("""
            delete from BoardLike bl
            where bl.board.boardId = :boardId
            """)
    void deleteByBoardBoardId(@Param("boardId") Long boardId);
}
