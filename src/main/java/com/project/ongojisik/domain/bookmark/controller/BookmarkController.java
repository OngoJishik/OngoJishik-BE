package com.project.ongojisik.domain.bookmark.controller;

import com.project.ongojisik.domain.analysis.dto.FoodSummaryResponse;
import com.project.ongojisik.domain.bookmark.dto.BookmarkedRecipeResponse;
import com.project.ongojisik.domain.bookmark.service.BookmarkService;
import com.project.ongojisik.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "즐겨찾기", description = "음식 즐겨찾기 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "음식 즐겨찾기 추가")
    @PostMapping("/{foodId}")
    public ApiResponse<FoodSummaryResponse> addBookmark(
            @AuthenticationPrincipal String userId,
            @PathVariable String foodId
    ) {
        return ApiResponse.success(bookmarkService.addBookmark(Long.valueOf(userId), foodId));
    }

    @Operation(summary = "음식 즐겨찾기 삭제")
    @DeleteMapping("/{foodId}")
    public ApiResponse<Void> deleteBookmark(
            @AuthenticationPrincipal String userId,
            @PathVariable String foodId
    ) {
        bookmarkService.deleteBookmark(Long.valueOf(userId), foodId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "내 즐겨찾기 목록 조회")
    @GetMapping
    public ApiResponse<List<FoodSummaryResponse>> getBookmarks(
            @AuthenticationPrincipal String userId
    ) {
        return ApiResponse.success(bookmarkService.getBookmarks(Long.valueOf(userId)));
    }

    @Operation(summary = "북마크 음식 레시피 목록 조회", description = "게시글 작성/수정 시 recipeId로 선택 가능한 북마크 음식의 레시피 목록을 조회합니다.")
    @GetMapping("/recipes")
    public ApiResponse<List<BookmarkedRecipeResponse>> getBookmarkedRecipes(
            @AuthenticationPrincipal String userId
    ) {
        return ApiResponse.success(bookmarkService.getBookmarkedRecipes(Long.valueOf(userId)));
    }
}
