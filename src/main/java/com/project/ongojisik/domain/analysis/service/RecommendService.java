package com.project.ongojisik.domain.analysis.service;

import com.project.ongojisik.domain.analysis.dto.FoodDetailResponse;
import com.project.ongojisik.domain.analysis.dto.FoodSummaryResponse;
import com.project.ongojisik.domain.analysis.dto.RecommendResponse;
import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.llm.FeatureExtractionResult;
import com.project.ongojisik.domain.analysis.llm.FeatureExtractor;
import com.project.ongojisik.domain.analysis.repository.FoodRepository;
import com.project.ongojisik.domain.analysis.util.FoodTextUtils;
import com.project.ongojisik.domain.bookmark.repository.BookmarkRepository;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
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
    private final ImageGenerationJobService imageGenerationJobService;

    public RecommendResponse recommend(String query) {
        if (query == null || query.isBlank()) {
            throw new APIException(ErrorCode.MISSING_REQUIRED_FIELD);
        }

        FeatureExtractionResult extractionResult = featureExtractor.extract(query);
        List<Food> foods = foodRepository.findAll();

        List<Food> recommendedFoods = selectMatchedRecommendations(query, extractionResult, foods);
        if (recommendedFoods.isEmpty()) {
            recommendedFoods = selectFallbackRecommendations(foods);
        }

        List<FoodSummaryResponse> recommendations = recommendedFoods.stream()
                .map(food -> FoodSummaryResponse.from(
                        food,
                        imageGenerationJobService.requestImageGenerationIfNeeded(food)
                ))
                .toList();

        return new RecommendResponse(query, extractionResult.searchTerms(), recommendations);
    }

    @Transactional(readOnly = true)
    public FoodDetailResponse getFoodDetail(Long userId, String foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new APIException(ErrorCode.FOOD_NOT_FOUND));

        boolean isBookmarked = bookmarkRepository.existsByUserUserIdAndFoodFoodId(userId, foodId);
        return FoodDetailResponse.from(food, isBookmarked);
    }

    private List<Food> selectMatchedRecommendations(
            String query,
            FeatureExtractionResult extractionResult,
            List<Food> foods
    ) {
        return foods.stream()
                .map(food -> new FoodScore(
                        food,
                        countMatchedFeatures(
                                extractionResult.features(),
                                FoodTextUtils.splitComma(food.getFoodFeatures())
                        ),
                        extractionResult.categories().contains(food.getCategory()),
                        isFoodNameIncludedInQuery(query, food)
                ))
                .filter(result -> result.foodNameMatched()
                        || result.featureMatchCount() > 0
                        || result.categoryMatched())
                .sorted(Comparator.comparing(FoodScore::foodNameMatched, Comparator.reverseOrder())
                        .thenComparing(Comparator.comparingInt(FoodScore::featureMatchCount).reversed())
                        .thenComparing(FoodScore::categoryMatched, Comparator.reverseOrder())
                        .thenComparing(result -> result.food().getFoodId()))
                .filter(distinctByFoodName())
                .limit(3)
                .map(FoodScore::food)
                .toList();
    }

    private List<Food> selectFallbackRecommendations(List<Food> foods) {
        List<Food> shuffledFoods = new ArrayList<>(foods);
        Collections.shuffle(shuffledFoods);
        return shuffledFoods.stream()
                .filter(distinctFoodByName())
                .limit(3)
                .toList();
    }

    private Predicate<FoodScore> distinctByFoodName() {
        Set<String> foodNames = new HashSet<>();
        return foodScore -> foodNames.add(foodScore.food().getFoodName());
    }

    private Predicate<Food> distinctFoodByName() {
        Set<String> foodNames = new HashSet<>();
        return food -> foodNames.add(food.getFoodName());
    }

    private int countMatchedFeatures(
            List<String> extractedFeatures,
            List<String> foodFeatures
    ) {
        return (int) extractedFeatures.stream()
                .filter(foodFeatures::contains)
                .count();
    }

    private boolean isFoodNameIncludedInQuery(String query, Food food) {
        String foodName = food.getFoodName();
        return foodName != null && !foodName.isBlank() && query.contains(foodName);
    }

    private record FoodScore(
            Food food,
            int featureMatchCount,
            boolean categoryMatched,
            boolean foodNameMatched
    ) {
    }
}
