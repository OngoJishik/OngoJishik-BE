package com.project.ongojisik.domain.comment.controller;

import com.project.ongojisik.domain.comment.dto.CommentRequest;
import com.project.ongojisik.domain.comment.dto.CommentResponse;
import com.project.ongojisik.domain.comment.dto.MyCommentResponse;
import com.project.ongojisik.domain.comment.service.CommentService;
import com.project.ongojisik.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "댓글", description = "게시글 댓글 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "로그인한 사용자가 게시글에 댓글을 작성합니다.")
    @PostMapping("/boards/{boardId}/comments")
    public ApiResponse<CommentResponse> createComment(
            @AuthenticationPrincipal String userId,
            @PathVariable Long boardId,
            @Valid @RequestBody CommentRequest request
    ) {
        return ApiResponse.success(commentService.createComment(Long.valueOf(userId), boardId, request));
    }

    @Operation(summary = "게시글별 댓글 목록 조회", description = "게시글에 작성된 댓글 목록을 최신순으로 조회합니다.")
    @GetMapping("/boards/{boardId}/comments")
    public ApiResponse<Page<CommentResponse>> getCommentList(
            @PathVariable Long boardId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(commentService.getCommentList(boardId, pageable));
    }

    @Operation(summary = "내가 작성한 댓글 목록 조회", description = "로그인한 사용자가 작성한 댓글과 게시글 제목을 조회합니다.")
    @GetMapping("/comments/me")
    public ApiResponse<Page<MyCommentResponse>> getMyCommentList(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(commentService.getMyCommentList(Long.valueOf(userId), pageable));
    }

    @Operation(summary = "댓글 수정", description = "댓글 작성자만 댓글을 수정합니다.")
    @PatchMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse> updateComment(
            @AuthenticationPrincipal String userId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request
    ) {
        return ApiResponse.success(commentService.updateComment(Long.valueOf(userId), commentId, request));
    }

    @Operation(summary = "댓글 삭제", description = "댓글 작성자만 댓글을 삭제합니다.")
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @AuthenticationPrincipal String userId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(Long.valueOf(userId), commentId);
        return ApiResponse.success(null);
    }
}
