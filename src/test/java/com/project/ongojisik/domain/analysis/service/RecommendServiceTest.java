package com.project.ongojisik.domain.analysis.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.project.ongojisik.domain.analysis.dto.FoodDetailResponse;
import com.project.ongojisik.domain.analysis.dto.FoodSummaryResponse;
import com.project.ongojisik.domain.analysis.dto.RecommendResponse;
import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.llm.FeatureExtractionResult;
import com.project.ongojisik.domain.analysis.llm.FeatureExtractor;
import com.project.ongojisik.domain.analysis.repository.FoodRepository;
import com.project.ongojisik.domain.bookmark.repository.BookmarkRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class RecommendServiceTest {

    private final FoodRepository foodRepository = mock(FoodRepository.class);
    private final FeatureExtractor featureExtractor = mock(FeatureExtractor.class);
    private final BookmarkRepository bookmarkRepository = mock(BookmarkRepository.class);
    private final ImageGenerationJobService imageGenerationJobService = mock(ImageGenerationJobService.class);
    private final RecommendService recommendService =
            new RecommendService(foodRepository, featureExtractor, bookmarkRepository, imageGenerationJobService);

    @Test
    void ranksExactCategoryAndFeatureMatches() {
        Food categoryAndFeatureMatch = food("1", "매운탕", "국/탕류", "매운맛,따뜻한");
        Food featureOnlyMatch = food("2", "매운전", "전/부침류", "매운맛");
        Food noMatch = food("3", "약과", "한과/과자류", "단맛");

        when(featureExtractor.extract("매운 국물 음식"))
                .thenReturn(new FeatureExtractionResult(
                        List.of("매운맛"),
                        List.of("국/탕류")
                ));
        when(foodRepository.findAll())
                .thenReturn(List.of(featureOnlyMatch, noMatch, categoryAndFeatureMatch));

        RecommendResponse response = recommendService.recommend("매운 국물 음식");

        assertEquals(List.of("국/탕류", "매운맛"), response.extractedFeatures());
        assertEquals(List.of("1", "2"), response.recommendations().stream()
                .map(FoodSummaryResponse::foodId)
                .toList());
    }

    @Test
    void ranksByFeatureCountThenCategoryThenFoodId() {
        Food oneFeatureWithCategory = food("1", "food-1", "target-category", "feature-a");
        Food twoFeaturesHigherId = food("4", "food-4", "other-category", "feature-a,feature-b");
        Food twoFeaturesLowerId = food("3", "food-3", "other-category", "feature-a,feature-b");
        Food oneFeatureWithoutCategory = food("2", "food-2", "other-category", "feature-a");

        when(featureExtractor.extract("ranking query"))
                .thenReturn(new FeatureExtractionResult(
                        List.of("feature-a", "feature-b"),
                        List.of("target-category")
                ));
        when(foodRepository.findAll()).thenReturn(List.of(
                oneFeatureWithoutCategory,
                twoFeaturesHigherId,
                oneFeatureWithCategory,
                twoFeaturesLowerId
        ));

        RecommendResponse response = recommendService.recommend("ranking query");

        assertThat(response.recommendations())
                .extracting(FoodSummaryResponse::foodId)
                .containsExactly("3", "4", "1");
    }

    @Test
    void removesDuplicateFoodNamesFromMatchedRecommendations() {
        Food bestDuplicate = food("1", "same-food", "target-category", "feature-a,feature-b");
        Food lowerDuplicate = food("2", "same-food", "target-category", "feature-a");
        Food secondFood = food("3", "second-food", "target-category", "feature-a");
        Food thirdFood = food("4", "third-food", "target-category", "feature-a");

        when(featureExtractor.extract("duplicate query"))
                .thenReturn(new FeatureExtractionResult(
                        List.of("feature-a", "feature-b"),
                        List.of("target-category")
                ));
        when(foodRepository.findAll()).thenReturn(List.of(
                lowerDuplicate,
                thirdFood,
                bestDuplicate,
                secondFood
        ));

        RecommendResponse response = recommendService.recommend("duplicate query");

        assertThat(response.recommendations())
                .extracting(FoodSummaryResponse::foodId)
                .containsExactly("1", "3", "4");
        assertThat(response.recommendations())
                .extracting(FoodSummaryResponse::foodName)
                .doesNotHaveDuplicates();
    }

    @Test
    void recommendsCategoryMatchesWhenOnlyCategoryIsExtracted() {
        Food categoryMatch = food("1", "food-1", "target-category", "feature-a");
        Food noMatch = food("2", "food-2", "other-category", "feature-b");
        when(featureExtractor.extract("category query"))
                .thenReturn(new FeatureExtractionResult(
                        List.of(),
                        List.of("target-category")
                ));
        when(foodRepository.findAll()).thenReturn(List.of(noMatch, categoryMatch));

        RecommendResponse response = recommendService.recommend("category query");

        assertThat(response.recommendations())
                .extracting(FoodSummaryResponse::foodId)
                .containsExactly("1");
    }

    @Test
    void returnsFallbackRecommendationsWhenExtractionIsEmpty() {
        List<Food> foods = List.of(
                food("1", "food-1", "category-1", "feature-1"),
                food("2", "food-2", "category-2", "feature-2"),
                food("3", "food-3", "category-3", "feature-3"),
                food("4", "food-4", "category-4", "feature-4")
        );
        when(featureExtractor.extract("anything"))
                .thenReturn(new FeatureExtractionResult(List.of(), List.of()));
        when(foodRepository.findAll()).thenReturn(foods);

        RecommendResponse response = recommendService.recommend("anything");

        assertThat(response.extractedFeatures()).isEmpty();
        assertThat(response.recommendations())
                .hasSize(3)
                .extracting(FoodSummaryResponse::foodId)
                .doesNotHaveDuplicates()
                .isSubsetOf("1", "2", "3", "4");
    }

    @Test
    void removesDuplicateFoodNamesFromFallbackRecommendations() {
        List<Food> foods = List.of(
                food("1", "same-food", "category-1", "feature-1"),
                food("2", "same-food", "category-2", "feature-2"),
                food("3", "second-food", "category-3", "feature-3"),
                food("4", "third-food", "category-4", "feature-4"),
                food("5", "fourth-food", "category-5", "feature-5")
        );
        when(featureExtractor.extract("anything"))
                .thenReturn(new FeatureExtractionResult(List.of(), List.of()));
        when(foodRepository.findAll()).thenReturn(foods);

        RecommendResponse response = recommendService.recommend("anything");

        assertThat(response.recommendations())
                .hasSize(3)
                .extracting(FoodSummaryResponse::foodName)
                .doesNotHaveDuplicates();
    }

    @Test
    void returnsFallbackRecommendationsWhenNoFoodMatchesExtractedTerms() {
        List<Food> foods = List.of(
                food("1", "food-1", "category-1", "feature-1"),
                food("2", "food-2", "category-2", "feature-2")
        );
        when(featureExtractor.extract("unknown preference"))
                .thenReturn(new FeatureExtractionResult(
                        List.of("unmatched-feature"),
                        List.of()
                ));
        when(foodRepository.findAll()).thenReturn(foods);

        RecommendResponse response = recommendService.recommend("unknown preference");

        assertThat(response.recommendations())
                .hasSize(2)
                .extracting(FoodSummaryResponse::foodId)
                .containsExactlyInAnyOrder("1", "2");
    }

    @Test
    void foodDetailContainsCurrentUsersBookmarkStatus() {
        Food food = food("011763", "food", "category", "feature");
        when(foodRepository.findById("011763")).thenReturn(java.util.Optional.of(food));
        when(bookmarkRepository.existsByUserUserIdAndFoodFoodId(1L, "011763")).thenReturn(true);

        FoodDetailResponse response = recommendService.getFoodDetail(1L, "011763");

        assertThat(response.isBookmarked()).isTrue();
    }

    private Food food(String id, String name, String category, String features) {
        Food food = new Food();
        ReflectionTestUtils.setField(food, "foodId", id);
        ReflectionTestUtils.setField(food, "foodName", name);
        ReflectionTestUtils.setField(food, "category", category);
        ReflectionTestUtils.setField(food, "foodFeatures", features);
        return food;
    }
}
