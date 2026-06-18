package com.project.ongojisik.domain.bookmark.dto;

import com.project.ongojisik.domain.analysis.dto.FoodSummaryResponse;
import java.util.List;

public record BookmarkListResponse(
        int totalCount,
        List<FoodSummaryResponse> bookmarks
) {

    public static BookmarkListResponse from(List<FoodSummaryResponse> bookmarks) {
        return new BookmarkListResponse(bookmarks.size(), bookmarks);
    }
}
