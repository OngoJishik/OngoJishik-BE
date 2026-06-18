package com.project.ongojisik.domain.analysis.util;

import java.util.Arrays;
import java.util.List;

public final class FoodTextUtils {

    private FoodTextUtils() {
    }

    public static List<String> splitComma(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }

    public static List<String> splitRecipeDescriptions(String recipe) {
        if (recipe == null || recipe.isBlank()) {
            return List.of();
        }

        return Arrays.stream(recipe.split("\\R"))
                .map(String::trim)
                .filter(step -> !step.isBlank())
                .map(step -> step.replaceFirst("^\\d+\\)\\s*", ""))
                .toList();
    }
}
