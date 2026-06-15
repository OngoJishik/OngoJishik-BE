package com.project.ongojisik.domain.analysis.service;

import com.project.ongojisik.domain.analysis.dto.FoodDetailResponse;
import com.project.ongojisik.domain.analysis.dto.RecommendFoodResponse;
import com.project.ongojisik.domain.analysis.dto.RecommendResponse;
import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.llm.FeatureExtractionResult;
import com.project.ongojisik.domain.analysis.llm.FeatureExtractor;
import com.project.ongojisik.domain.analysis.repository.FoodRepository;
import com.project.ongojisik.domain.bookmark.repository.BookmarkRepository;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private final BookmarkRepository bookmarkRepository;

    public RecommendResponse recommend(String query) {
        if (query == null || query.isBlank()) {
            throw new APIException(ErrorCode.MISSING_REQUIRED_FIELD);
        }

        FeatureExtractionResult extractionResult = featureExtractor.extract(query);
        List<Food> foods = foodRepository.findAll();

        List<RecommendFoodResponse> recommendations;
        if (extractionResult.searchTerms().isEmpty()) {
            recommendations = selectFallbackRecommendations(foods);
        } else {
            recommendations = selectMatchedRecommendations(extractionResult, foods);
            if (recommendations.isEmpty()) {
                recommendations = selectFallbackRecommendations(foods);
            }
        }

        return new RecommendResponse(query, extractionResult.searchTerms(), recommendations);
    }

    @Transactional(readOnly = true)
    public FoodDetailResponse getFoodDetail(Long userId, String foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new APIException(ErrorCode.FOOD_NOT_FOUND));

        boolean isBookmarked = bookmarkRepository.existsByUserUserIdAndFoodFoodId(userId, foodId);
        return FoodDetailResponse.from(food, isBookmarked);
    }

    private List<RecommendFoodResponse> selectMatchedRecommendations(
            FeatureExtractionResult extractionResult,
            List<Food> foods
    ) {
        return foods.stream()
                .map(food -> new FoodScore(
                        food,
                        countMatchedFeatures(
                                extractionResult.features(),
                                parseFoodFeatures(food.getFoodFeatures())
                        ),
                        extractionResult.categories().contains(food.getCategory())
                ))
                .filter(result -> result.featureMatchCount() > 0 || result.categoryMatched())
                .sorted(Comparator.comparingInt(FoodScore::featureMatchCount).reversed()
                        .thenComparing(FoodScore::categoryMatched, Comparator.reverseOrder())
                        .thenComparing(result -> result.food().getFoodId()))
                .limit(3)
                .map(result -> RecommendFoodResponse.from(result.food()))
                .toList();
    }

    private List<RecommendFoodResponse> selectFallbackRecommendations(List<Food> foods) {
        List<Food> shuffledFoods = new ArrayList<>(foods);
        Collections.shuffle(shuffledFoods);
        return shuffledFoods.stream()
                .limit(3)
                .map(RecommendFoodResponse::from)
                .toList();
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

    private record FoodScore(
            Food food,
            int featureMatchCount,
            boolean categoryMatched
    ) {
    }
}
