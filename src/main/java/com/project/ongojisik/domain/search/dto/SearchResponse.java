package com.project.ongojisik.domain.search.dto;

import com.project.ongojisik.domain.analysis.dto.RecommendFoodResponse;
import com.project.ongojisik.domain.analysis.dto.RecommendResponse;
import com.project.ongojisik.domain.search.entity.SearchHistory;
import java.time.LocalDateTime;
import java.util.List;

public record SearchResponse(
        Long searchId,
        String originalQuery,
        List<String> extractedFeatures,
        List<RecommendFoodResponse> recommendations,
        LocalDateTime createdAt
) {

    public static SearchResponse from(SearchHistory searchHistory, RecommendResponse recommendResponse) {
        return new SearchResponse(
                searchHistory.getSearchId(),
                recommendResponse.originalQuery(),
                recommendResponse.extractedFeatures(),
                recommendResponse.recommendations(),
                searchHistory.getCreatedAt()
        );
    }

    public static SearchResponse from(SearchHistory searchHistory, List<RecommendFoodResponse> recommendations) {
        return new SearchResponse(
                searchHistory.getSearchId(),
                searchHistory.getQuery(),
                searchHistory.getExtractedFeatureList(),
                recommendations,
                searchHistory.getCreatedAt()
        );
    }
}
