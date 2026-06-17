package com.project.ongojisik.domain.search.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.repository.FoodRepository;
import com.project.ongojisik.domain.analysis.service.RecommendService;
import com.project.ongojisik.domain.search.dto.SearchResponse;
import com.project.ongojisik.domain.search.entity.SearchHistory;
import com.project.ongojisik.domain.search.repository.SearchHistoryRepository;
import com.project.ongojisik.domain.user.entity.User;
import com.project.ongojisik.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private RecommendService recommendService;

    private SearchService searchService;

    @BeforeEach
    void setUp() {
        searchService = new SearchService(
                searchHistoryRepository,
                userRepository,
                foodRepository,
                recommendService
        );
    }

    @Test
    void searchReturnsExistingHistoryWithoutSavingDuplicateQuery() {
        User user = User.create("google-123", "user@example.com", "user");
        ReflectionTestUtils.setField(user, "userId", 1L);
        SearchHistory existingSearch = SearchHistory.create(
                user,
                "spicy food",
                List.of("spicy"),
                List.of("10002")
        );
        ReflectionTestUtils.setField(existingSearch, "searchId", 10L);
        Food food = new Food();
        ReflectionTestUtils.setField(food, "foodId", "10002");
        ReflectionTestUtils.setField(food, "foodName", "food");
        ReflectionTestUtils.setField(food, "category", "category");
        ReflectionTestUtils.setField(food, "foodFeatures", "feature");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(searchHistoryRepository.findFirstByUserUserIdAndQuery(1L, "spicy food"))
                .thenReturn(Optional.of(existingSearch));
        when(foodRepository.findAllById(List.of("10002"))).thenReturn(List.of(food));

        SearchResponse response = searchService.search(1L, "  spicy food  ");

        assertThat(response.searchId()).isEqualTo(10L);
        assertThat(response.originalQuery()).isEqualTo("spicy food");
        assertThat(response.recommendations()).hasSize(1);
        assertThat(response.recommendations().get(0).foodId()).isEqualTo("10002");
        verify(recommendService, never()).recommend("spicy food");
        verify(searchHistoryRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
