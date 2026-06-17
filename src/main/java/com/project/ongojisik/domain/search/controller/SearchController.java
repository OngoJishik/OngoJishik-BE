package com.project.ongojisik.domain.search.controller;

import com.project.ongojisik.domain.search.dto.SearchListResponse;
import com.project.ongojisik.domain.search.dto.SearchResponse;
import com.project.ongojisik.domain.search.service.SearchService;
import com.project.ongojisik.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Search", description = "Food search and recent search history API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/searches")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "Get recent searches", description = "Returns recent search histories for the current user.")
    @GetMapping("/recent")
    public ApiResponse<SearchListResponse> getRecentSearches(
            @AuthenticationPrincipal String userId
    ) {
        return ApiResponse.success(searchService.getRecentSearches(Long.valueOf(userId)));
    }

    @Operation(summary = "Get recent search result", description = "Returns the recommendation list saved for a recent search.")
    @GetMapping("/recent/{searchId}")
    public ApiResponse<SearchResponse> getRecentSearchResult(
            @AuthenticationPrincipal String userId,
            @PathVariable Long searchId
    ) {
        return ApiResponse.success(searchService.getRecentSearchResult(Long.valueOf(userId), searchId));
    }

    @Operation(summary = "Delete recent search", description = "Deletes one recent search history.")
    @DeleteMapping("/recent/{searchId}")
    public ApiResponse<Void> deleteRecentSearch(
            @AuthenticationPrincipal String userId,
            @PathVariable Long searchId
    ) {
        searchService.deleteRecentSearch(Long.valueOf(userId), searchId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "Delete all recent searches", description = "Deletes all recent search histories for the current user.")
    @DeleteMapping("/recent")
    public ApiResponse<Void> deleteAllRecentSearches(
            @AuthenticationPrincipal String userId
    ) {
        searchService.deleteAllRecentSearches(Long.valueOf(userId));
        return ApiResponse.success(null);
    }
}
