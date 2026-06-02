package com.project.ongojisik.domain.bookmark.repository;

import com.project.ongojisik.domain.bookmark.entity.Bookmark;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("""
            select b
            from Bookmark b
            where b.user.userId = :userId
              and b.board.boardId = :boardId
            """)
    Optional<Bookmark> findByUserUserIdAndBoardBoardId(
            @Param("userId") Long userId,
            @Param("boardId") Long boardId
    );

    @Query("""
            select b
            from Bookmark b
            where b.user.userId = :userId
            order by b.createdAt desc, b.bookmarkId desc
            """)
    Page<Bookmark> findByUserUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
            select case when count(b) > 0 then true else false end
            from Bookmark b
            where b.user.userId = :userId
              and b.board.boardId = :boardId
            """)
    boolean existsByUserUserIdAndBoardBoardId(
            @Param("userId") Long userId,
            @Param("boardId") Long boardId
    );
}
