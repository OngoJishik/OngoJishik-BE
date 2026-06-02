package com.project.ongojisik.domain.bookmark.repository;

import com.project.ongojisik.domain.bookmark.entity.Bookmark;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserUserIdAndBoardBoardId(Long userId, Long boardId);

    Page<Bookmark> findByUserUserId(Long userId, Pageable pageable);

    boolean existsByUserUserIdAndBoardBoardId(Long userId, Long boardId);
}
