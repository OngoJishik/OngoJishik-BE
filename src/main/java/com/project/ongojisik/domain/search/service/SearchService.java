package com.project.ongojisik.domain.search.service;

import com.project.ongojisik.domain.analysis.dto.RecommendFoodResponse;
import com.project.ongojisik.domain.analysis.dto.RecommendResponse;
import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.repository.FoodRepository;
import com.project.ongojisik.domain.analysis.service.RecommendService;
import com.project.ongojisik.domain.search.dto.SearchListResponse;
import com.project.ongojisik.domain.search.dto.SearchRequest;
import com.project.ongojisik.domain.search.dto.SearchResponse;
import com.project.ongojisik.domain.search.dto.SearchSummaryResponse;
import com.project.ongojisik.domain.search.entity.SearchHistory;
import com.project.ongojisik.domain.search.repository.SearchHistoryRepository;
import com.project.ongojisik.domain.user.entity.User;
import com.project.ongojisik.domain.user.repository.UserRepository;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final RecommendService recommendService;

    @Transactional
    public SearchResponse search(Long userId, SearchRequest request) {
        User user = findCurrentUser(userId);
        String query = request.query().trim();

        SearchHistory existingSearchHistory = searchHistoryRepository
                .findFirstByUserUserIdAndQuery(userId, query)
                .orElse(null);
        if (existingSearchHistory != null) {
            List<RecommendFoodResponse> recommendations = findRecommendationsInSavedOrder(existingSearchHistory);
            return SearchResponse.from(existingSearchHistory, recommendations);
        }

        RecommendResponse recommendResponse = recommendService.recommend(query);
        List<String> recommendedFoodIds = recommendResponse.recommendations().stream()
                .map(RecommendFoodResponse::foodId)
                .toList();

        SearchHistory searchHistory = SearchHistory.create(
                user,
                recommendResponse.originalQuery(),
                recommendResponse.extractedFeatures(),
                recommendedFoodIds
        );

        SearchHistory savedSearchHistory = searchHistoryRepository.save(searchHistory);
        return SearchResponse.from(savedSearchHistory, recommendResponse);
    }

    @Transactional(readOnly = true)
    public SearchListResponse getRecentSearches(Long userId) {
        findCurrentUser(userId);
        List<SearchSummaryResponse> searches = searchHistoryRepository.findByUserUserId(userId).stream()
                .map(SearchSummaryResponse::from)
                .toList();
        return SearchListResponse.from(searches);
    }

    @Transactional(readOnly = true)
    public SearchResponse getRecentSearchResult(Long userId, Long searchId) {
        SearchHistory searchHistory = findSearchHistory(userId, searchId);
        List<RecommendFoodResponse> recommendations = findRecommendationsInSavedOrder(searchHistory);
        return SearchResponse.from(searchHistory, recommendations);
    }

    @Transactional
    public void deleteRecentSearch(Long userId, Long searchId) {
        SearchHistory searchHistory = findSearchHistory(userId, searchId);
        searchHistoryRepository.delete(searchHistory);
    }

    @Transactional
    public void deleteAllRecentSearches(Long userId) {
        findCurrentUser(userId);
        searchHistoryRepository.deleteByUserUserId(userId);
    }

    private List<RecommendFoodResponse> findRecommendationsInSavedOrder(SearchHistory searchHistory) {
        List<String> savedFoodIds = searchHistory.getRecommendedFoodIdList();
        Map<String, Food> foodById = foodRepository.findAllById(savedFoodIds).stream()
                .collect(Collectors.toMap(Food::getFoodId, Function.identity()));

        return savedFoodIds.stream()
                .filter(foodById::containsKey)
                .map(foodById::get)
                .map(RecommendFoodResponse::from)
                .sorted(Comparator.comparingInt(response -> savedFoodIds.indexOf(response.foodId())))
                .toList();
    }

    private SearchHistory findSearchHistory(Long userId, Long searchId) {
        return searchHistoryRepository.findBySearchIdAndUserUserId(searchId, userId)
                .orElseThrow(() -> new APIException(ErrorCode.SEARCH_HISTORY_NOT_FOUND));
    }

    private User findCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));
    }
}
