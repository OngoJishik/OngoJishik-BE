package com.project.ongojisik.domain.analysis.dto;

import com.project.ongojisik.domain.analysis.entity.Food;

public record RecommendFoodResponse(
        String foodId,
        String foodName,
        String category,
        String foodFeatures,
        String foodPicture
) {
    public static RecommendFoodResponse from(Food food) {
        return new RecommendFoodResponse(
                food.getFoodId(),
                food.getFoodName(),
                food.getCategory(),
                food.getFoodFeatures(),
                food.getFoodPicture()
        );
    }
}
