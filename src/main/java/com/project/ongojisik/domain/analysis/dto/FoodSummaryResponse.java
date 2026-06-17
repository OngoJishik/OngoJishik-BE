package com.project.ongojisik.domain.analysis.dto;

import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.util.FoodTextUtils;
import java.util.List;

public record FoodSummaryResponse(
        String foodId,
        String foodName,
        String category,
        List<String> features,
        String foodPicture
) {
    public static FoodSummaryResponse from(Food food) {
        return new FoodSummaryResponse(
                food.getFoodId(),
                food.getFoodName(),
                food.getCategory(),
                FoodTextUtils.splitComma(food.getFoodFeatures()),
                food.getFoodPicture()
        );
    }
}
