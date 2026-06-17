package com.project.ongojisik.domain.bookmark.dto;

import com.project.ongojisik.domain.analysis.entity.Food;

public record BookmarkedRecipeResponse(
        String recipeId,
        String foodName,
        String foodPicture,
        String recipe
) {

    public static BookmarkedRecipeResponse from(Food food) {
        return new BookmarkedRecipeResponse(
                food.getFoodId(),
                food.getFoodName(),
                food.getFoodPicture(),
                food.getRecipe()
        );
    }
}
