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

@Tag(name = "검색", description = "사용자 최근 검색 기록 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/searches")
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "최근 검색어 목록 조회", description = "현재 로그인한 사용자의 최근 검색 기록 목록을 조회합니다.")
    @GetMapping("/recent")
    public ApiResponse<SearchListResponse> getRecentSearches(
            @AuthenticationPrincipal String userId
    ) {
        return ApiResponse.success(searchService.getRecentSearches(Long.valueOf(userId)));
    }

    @Operation(summary = "최근 검색 결과 조회", description = "최근 검색 기록에 저장된 추천 음식 목록을 조회합니다.")
    @GetMapping("/recent/{searchId}")
    public ApiResponse<SearchResponse> getRecentSearchResult(
            @AuthenticationPrincipal String userId,
            @PathVariable Long searchId
    ) {
        return ApiResponse.success(searchService.getRecentSearchResult(Long.valueOf(userId), searchId));
    }

    @Operation(summary = "최근 검색어 단건 삭제", description = "현재 로그인한 사용자의 최근 검색 기록 중 하나를 삭제합니다.")
    @DeleteMapping("/recent/{searchId}")
    public ApiResponse<Void> deleteRecentSearch(
            @AuthenticationPrincipal String userId,
            @PathVariable Long searchId
    ) {
        searchService.deleteRecentSearch(Long.valueOf(userId), searchId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "최근 검색어 전체 삭제", description = "현재 로그인한 사용자의 최근 검색 기록을 모두 삭제합니다.")
    @DeleteMapping("/recent")
    public ApiResponse<Void> deleteAllRecentSearches(
            @AuthenticationPrincipal String userId
    ) {
        searchService.deleteAllRecentSearches(Long.valueOf(userId));
        return ApiResponse.success(null);
    }
}
