package com.project.ongojisik.domain.home.dto;

import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.util.FoodTextUtils;
import java.util.List;

public record HomeFoodResponse(
        int order,
        String foodId,
        String foodName,
        String category,
        List<String> features,
        String foodPicture
) {

    public static HomeFoodResponse from(int order, Food food) {
        return new HomeFoodResponse(
                order,
                food.getFoodId(),
                food.getFoodName(),
                food.getCategory(),
                FoodTextUtils.splitComma(food.getFoodFeatures()),
                food.getFoodPicture()
        );
    }
}
