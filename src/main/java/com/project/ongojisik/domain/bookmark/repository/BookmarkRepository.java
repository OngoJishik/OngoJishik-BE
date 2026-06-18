package com.project.ongojisik.domain.bookmark.repository;

import com.project.ongojisik.domain.bookmark.entity.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByUserUserIdAndFoodFoodId(Long userId, String foodId);

    Optional<Bookmark> findByUserUserIdAndFoodFoodId(Long userId, String foodId);

    List<Bookmark> findByUserUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
            select b
            from Bookmark b
            join fetch b.food
            where b.user.userId = :userId
              and b.food.recipe is not null
              and b.food.recipe <> ''
            order by b.createdAt desc
            """)
    List<Bookmark> findBookmarkedFoodsWithRecipe(@Param("userId") Long userId);
}
