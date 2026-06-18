package com.project.ongojisik.domain.analysis.dto;

import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.util.FoodTextUtils;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record FoodDetailResponse(
        String foodId,
        String foodName,
        String foodNameTranslated,
        String category,
        List<String> features,
        String imageUrl,
        boolean isBookmarked,
        List<String> ingredients,
        List<RecipeStep> recipeSteps,
        History history,
        Literature literature,
        String dataSource
) {

    private static final String DATA_SOURCE = "Korean traditional food data API";

    public static FoodDetailResponse from(Food food, boolean isBookmarked) {
        return new FoodDetailResponse(
                food.getFoodId(),
                food.getFoodName(),
                food.getFoodName(),
                food.getCategory(),
                createFeatures(food),
                food.getFoodPicture(),
                isBookmarked,
                FoodTextUtils.splitComma(food.getIngredients()),
                createRecipeSteps(food.getRecipe()),
                new History(defaultString(food.getHistory()), ""),
                new Literature(List.of(Source.from(food))),
                DATA_SOURCE
        );
    }

    private static List<String> createFeatures(Food food) {
        List<String> foodFeatures = FoodTextUtils.splitComma(food.getFoodFeatures());
        if (food.getCategory() == null || food.getCategory().isBlank()) {
            return foodFeatures;
        }

        return Stream.concat(Stream.of(food.getCategory()), foodFeatures.stream())
                .toList();
    }

    private static List<RecipeStep> createRecipeSteps(String recipe) {
        List<String> descriptions = FoodTextUtils.splitRecipeDescriptions(recipe);
        return IntStream.range(0, descriptions.size())
                .mapToObj(index -> new RecipeStep(
                        index + 1,
                        (index + 1) + "단계",
                        descriptions.get(index)
                ))
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

    public record RecipeStep(
            int stepNumber,
            String title,
            String description
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
