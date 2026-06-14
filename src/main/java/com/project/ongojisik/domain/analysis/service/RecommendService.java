package com.project.ongojisik.domain.analysis.service;

import com.project.ongojisik.domain.analysis.dto.FoodDetailResponse;
import com.project.ongojisik.domain.analysis.dto.RecommendFoodResponse;
import com.project.ongojisik.domain.analysis.dto.RecommendResponse;
import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.llm.FeatureExtractionResult;
import com.project.ongojisik.domain.analysis.llm.FeatureExtractor;
import com.project.ongojisik.domain.analysis.repository.FoodRepository;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecommendService {

    private final FoodRepository foodRepository;
    private final FeatureExtractor featureExtractor;

    public RecommendResponse recommend(String query) {
        if (query == null || query.isBlank()) {
            throw new APIException(ErrorCode.MISSING_REQUIRED_FIELD);
        }

        FeatureExtractionResult extractionResult = featureExtractor.extract(query);
        List<Food> foods = foodRepository.findAll();

        List<RecommendFoodResponse> recommendations = foods.stream()
                .map(food -> new FoodScore(
                        food,
                        calculateScore(extractionResult, food)
                ))
                .filter(result -> result.score() > 0)
                .sorted(Comparator.comparingInt(FoodScore::score).reversed()
                        .thenComparing(result -> result.food().getFoodId()))
                .limit(3)
                .map(result -> RecommendFoodResponse.from(result.food()))
                .toList();

        return new RecommendResponse(query, extractionResult.searchTerms(), recommendations);
    }

    @Transactional(readOnly = true)
    public FoodDetailResponse getFoodDetail(String foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new APIException(ErrorCode.FOOD_NOT_FOUND));

        return FoodDetailResponse.from(food, false);
    }

    private int calculateScore(FeatureExtractionResult result, Food food) {
        int categoryScore = result.categories().contains(food.getCategory()) ? 2 : 0;
        int featureScore = countMatchedFeatures(
                result.features(),
                parseFoodFeatures(food.getFoodFeatures())
        );
        return categoryScore + featureScore;
    }

    private List<String> parseFoodFeatures(String foodFeature) {
        if (foodFeature == null || foodFeature.isBlank()) {
            return List.of();
        }

        return Arrays.stream(foodFeature.split(","))
                .map(String::trim)
                .filter(feature -> !feature.isBlank())
                .toList();
    }

    private int countMatchedFeatures(
            List<String> extractedFeatures,
            List<String> foodFeatures
    ) {
        return (int) extractedFeatures.stream()
                .filter(foodFeatures::contains)
                .count();
    }

    private record FoodScore(Food food, int score) {
    }
}
