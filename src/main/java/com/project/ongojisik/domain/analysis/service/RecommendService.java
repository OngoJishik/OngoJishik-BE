package com.project.ongojisik.domain.analysis.service;

import com.project.ongojisik.domain.analysis.dto.FoodDetailResponse;
import com.project.ongojisik.domain.analysis.dto.RecommendFoodResponse;
import com.project.ongojisik.domain.analysis.dto.RecommendResponse;
import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.repository.FoodRepository;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/*
1. 사용자의 query를 받음
2. 임시 feature 리스트를 만듦
3. DB에서 Food 전체 조회
4. 음식별 foodFeature와 사용자 feature 비교
5. 유사도 점수 계산
6. 상위 3개 음식 반환
* */
@Service
@RequiredArgsConstructor
@Transactional
public class RecommendService {
    private final FoodRepository foodRepository;

    public RecommendResponse recommend(String query) {
        //query는 null이 아니여야함
        if (query == null) {
            throw new APIException(ErrorCode.MISSING_REQUIRED_FIELD);
        }

        //LLM없는 하드코딩 버전
        List<String> extractedFeatures = extractFeaturesTemp(query);
        List<Food> foods = foodRepository.findAll();

        List<RecommendFoodResponse> recommendations = foods.stream()
                .map(food -> new FoodScore(
                        food, countMatchedFeatures(
                                extractedFeatures,
                        parseFoodFeatures(food.getFoodFeatures())
                )
                ))
                .filter(result -> result.matchedCount() >0)
                .sorted(Comparator.comparingInt(FoodScore::matchedCount).reversed())
                .limit(3)
                .map(result -> RecommendFoodResponse.from(result.food()))
                .toList();

        return new RecommendResponse(query, extractedFeatures, recommendations);
    }

    @Transactional(readOnly = true)
    public FoodDetailResponse getFoodDetail(String foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new APIException(ErrorCode.FOOD_NOT_FOUND));

        return FoodDetailResponse.from(food, false);
    }

    private List<String> extractFeaturesTemp(String query) {
        if (query.contains("손님") || query.contains("대접")) {
            return List.of("손님접대", "상차림", "격식");
        }

        if (query.contains("비") || query.contains("국물")) {
            return List.of("국물", "따뜻한", "든든한");
        }

        if (query.contains("매운") || query.contains("매콤")) {
            return List.of("매운맛", "빨강", "국물");
        }

        return List.of("전통", "든든한");
    }
    private List<String> parseFoodFeatures(String foodFeature) {
        if (foodFeature == null || foodFeature.isBlank()) {
            return List.of();
        }

        return Arrays.stream(foodFeature.split(","))
                .map(String::trim)
                .toList();
    }

    private int countMatchedFeatures(
            List<String> extractedFeatures,
            List<String> foodFeatures
    ) {
        return (int) extractedFeatures.stream()
                .filter(extractedFeature -> foodFeatures.stream()
                        .anyMatch(foodFeature ->
                                foodFeature.contains(extractedFeature)
                                        || extractedFeature.contains(foodFeature)
                        ))
                .count();
    }
    private record FoodScore(Food food, int matchedCount) {
    }
}
