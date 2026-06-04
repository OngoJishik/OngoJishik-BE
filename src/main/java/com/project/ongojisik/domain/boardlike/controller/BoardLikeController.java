package com.project.ongojisik.domain.boardlike.controller;

import com.project.ongojisik.domain.boardlike.dto.BoardLikeResponse;
import com.project.ongojisik.domain.boardlike.service.BoardLikeService;
import com.project.ongojisik.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "게시글 좋아요", description = "게시글 좋아요 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/likes")
public class BoardLikeController {

    private final BoardLikeService boardLikeService;

    @Operation(summary = "게시글 좋아요 등록/취소", description = "로그인한 사용자가 게시글 좋아요를 토글합니다.")
    @PostMapping
    public ApiResponse<BoardLikeResponse> toggleBoardLike(
            @AuthenticationPrincipal String userId,
            @PathVariable Long boardId
    ) {
        return ApiResponse.success(boardLikeService.toggleBoardLike(Long.valueOf(userId), boardId));
    }

    @Operation(summary = "게시글 좋아요 개수 조회", description = "게시글의 좋아요 개수를 조회합니다.")
    @GetMapping("/count")
    public ApiResponse<Long> getBoardLikeCount(@PathVariable Long boardId) {
        return ApiResponse.success(boardLikeService.getBoardLikeCount(boardId));
    }
}
