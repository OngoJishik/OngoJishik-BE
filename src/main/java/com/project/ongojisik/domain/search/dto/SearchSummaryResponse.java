package com.project.ongojisik.domain.search.dto;

import com.project.ongojisik.domain.search.entity.SearchHistory;
import java.time.LocalDateTime;

public record SearchSummaryResponse(
        Long searchId,
        String query,
        LocalDateTime createdAt
) {

    public static SearchSummaryResponse from(SearchHistory searchHistory) {
        return new SearchSummaryResponse(
                searchHistory.getSearchId(),
                searchHistory.getQuery(),
                searchHistory.getCreatedAt()
        );
    }
}
