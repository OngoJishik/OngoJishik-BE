package com.project.ongojisik.domain.analysis.dto;

import com.project.ongojisik.domain.analysis.entity.Food;
import java.util.Arrays;
import java.util.List;

public record FoodDetailResponse(
        String foodId,
        String foodName,
        String foodNameTranslated,
        String category,
        List<String> features,
        String imageUrl,
        boolean isBookmarked,
        String ingredient,
        List<String> recipeSteps,
        History history,
        Literature literature,
        String dataSource
) {

    private static final String DATA_SOURCE = "출처: 특허청 한국전통지식포탈 전통식품정보 API";

    public static FoodDetailResponse from(Food food, boolean isBookmarked) {
        return new FoodDetailResponse(
                food.getFoodId(),
                food.getFoodName(),
                food.getFoodName(),
                food.getCategory(),
                createFeatures(food),
                food.getFoodPicture(),
                isBookmarked,
                food.getIngredients(),
                parseRecipeSteps(food.getRecipe()),
                new History(defaultString(food.getHistory()), ""),
                new Literature(List.of(Source.from(food))),
                DATA_SOURCE
        );
    }

    private static List<String> createFeatures(Food food) {
        List<String> foodFeatures = splitComma(food.getFoodFeatures());
        if (food.getCategory() == null || food.getCategory().isBlank()) {
            return foodFeatures;
        }

        return java.util.stream.Stream.concat(
                java.util.stream.Stream.of(food.getCategory()),
                foodFeatures.stream()
        ).toList();
    }

    private static List<String> splitComma(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(feature -> !feature.isBlank())
                .toList();
    }

    private static List<String> parseRecipeSteps(String recipe) {
        if (recipe == null || recipe.isBlank()) {
            return List.of();
        }

        return Arrays.stream(recipe.split("\\R"))
                .map(String::trim)
                .filter(step -> !step.isBlank())
                .map(step -> step.replaceFirst("^\\d+\\)\\s*", ""))
                .toList();
    }

    private static String defaultString(String value) {
        return value == null ? "" : value;
    }

    public record History(
            String origin,
            String ceremony
    ) {
    }

    public record Literature(
            List<Source> sources
    ) {
    }

    public record Source(
            String sourceId,
            String title,
            String author,
            String publishYear,
            String content,
            String originalUrl
    ) {

        private static Source from(Food food) {
            return new Source(
                    food.getFoodId(),
                    food.getDocName(),
                    food.getAuthor(),
                    food.getPublishedYear(),
                    defaultString(food.getTransTxt()),
                    defaultString(food.getOrgFoodUrl())
            );
        }
    }
}
