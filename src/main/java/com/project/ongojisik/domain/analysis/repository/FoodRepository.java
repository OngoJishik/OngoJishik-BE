package com.project.ongojisik.domain.analysis.repository;

import com.project.ongojisik.domain.analysis.entity.Food;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, String> {
    List<Food> findByFoodPictureIsNotNullAndFoodPictureNot(String foodPicture);

    List<Food> findByFoodName(String foodName);

    Optional<Food> findFirstByFoodNameAndFoodPictureIsNotNullAndFoodPictureNotOrderByFoodIdAsc(
            String foodName,
            String foodPicture
    );
}
