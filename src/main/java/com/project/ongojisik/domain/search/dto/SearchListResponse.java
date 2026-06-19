package com.project.ongojisik.domain.search.dto;

import java.util.List;

public record SearchListResponse(
        int totalCount,
        List<SearchSummaryResponse> searches
) {

    public static SearchListResponse from(List<SearchSummaryResponse> searches) {
        return new SearchListResponse(searches.size(), searches);
    }
}
