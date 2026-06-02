package com.project.ongojisik.domain.bookmark.controller;

import com.project.ongojisik.domain.bookmark.dto.BookmarkResponse;
import com.project.ongojisik.domain.bookmark.service.BookmarkService;
import com.project.ongojisik.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "즐겨찾기", description = "게시글 즐겨찾기 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "즐겨찾기 추가", description = "로그인한 사용자가 게시글을 즐겨찾기에 추가합니다.")
    @PostMapping("/{boardId}")
    public ApiResponse<BookmarkResponse> addBookmark(
            @AuthenticationPrincipal String userId,
            @PathVariable Long boardId
    ) {
        return ApiResponse.success(bookmarkService.addBookmark(Long.valueOf(userId), boardId));
    }

    @Operation(summary = "즐겨찾기 삭제", description = "로그인한 사용자가 게시글 즐겨찾기를 삭제합니다.")
    @DeleteMapping("/{boardId}")
    public ApiResponse<Void> deleteBookmark(
            @AuthenticationPrincipal String userId,
            @PathVariable Long boardId
    ) {
        bookmarkService.deleteBookmark(Long.valueOf(userId), boardId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "즐겨찾기 목록 조회", description = "로그인한 사용자의 즐겨찾기 게시글 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<Page<BookmarkResponse>> getBookmarkList(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(bookmarkService.getBookmarkList(Long.valueOf(userId), pageable));
    }
}
