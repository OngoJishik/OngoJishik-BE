package com.project.ongojisik.domain.bookmark.repository;

import com.project.ongojisik.domain.bookmark.entity.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByUserUserIdAndFoodFoodId(Long userId, String foodId);

    Optional<Bookmark> findByUserUserIdAndFoodFoodId(Long userId, String foodId);

    List<Bookmark> findByUserUserIdOrderByCreatedAtDesc(Long userId);
}
