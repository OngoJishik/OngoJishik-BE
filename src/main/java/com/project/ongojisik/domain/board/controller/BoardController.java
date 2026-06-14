package com.project.ongojisik.domain.board.controller;

import com.project.ongojisik.domain.board.dto.BoardCreateRequest;
import com.project.ongojisik.domain.board.dto.BoardResponse;
import com.project.ongojisik.domain.board.dto.BoardSummaryResponse;
import com.project.ongojisik.domain.board.dto.BoardUpdateRequest;
import com.project.ongojisik.domain.board.entity.BoardCategory;
import com.project.ongojisik.domain.board.service.BoardService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "게시판", description = "게시판 기본 CRUD API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시글 작성", description = "로그인한 사용자가 게시글을 작성합니다.")
    @PostMapping
    public ApiResponse<BoardResponse> createBoard(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody BoardCreateRequest request
    ) {
        return ApiResponse.success(boardService.createBoard(Long.valueOf(userId), request));
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글을 최신순으로 페이지 조회합니다.")
    @GetMapping
    public ApiResponse<Page<BoardSummaryResponse>> getBoardList(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
            @RequestParam(required = false) BoardCategory category
    ) {
        return ApiResponse.success(boardService.getBoardList(Long.valueOf(userId), category, pageable));
    }

    @Operation(summary = "게시글 제목 검색", description = "제목에 포함된 문자열과 카테고리로 게시글을 검색합니다.")
    @GetMapping("/search")
    public ApiResponse<Page<BoardSummaryResponse>> searchBoards(
            @AuthenticationPrincipal String userId,
            @RequestParam String title,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
            @RequestParam(required = false) BoardCategory category
    ) {
        return ApiResponse.success(boardService.searchBoardsByTitle(Long.valueOf(userId), title, category, pageable));
    }

    @Operation(summary = "내 게시글 목록 조회", description = "로그인한 사용자가 작성한 게시글 목록을 최신순으로 페이지 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<Page<BoardSummaryResponse>> getMyBoardList(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(boardService.getMyBoardList(Long.valueOf(userId), pageable));
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 단건 조회합니다.")
    @GetMapping("/{boardId}")
    public ApiResponse<BoardResponse> getBoard(
            @AuthenticationPrincipal String userId,
            @PathVariable Long boardId
    ) {
        return ApiResponse.success(boardService.getBoard(Long.valueOf(userId), boardId));
    }

    @Operation(summary = "게시글 수정", description = "작성자만 게시글을 수정합니다.")
    @PatchMapping("/{boardId}")
    public ApiResponse<BoardResponse> updateBoard(
            @AuthenticationPrincipal String userId,
            @PathVariable Long boardId,
            @Valid @RequestBody BoardUpdateRequest request
    ) {
        return ApiResponse.success(boardService.updateBoard(Long.valueOf(userId), boardId, request));
    }

    @Operation(summary = "게시글 삭제", description = "작성자만 게시글을 삭제합니다.")
    @DeleteMapping("/{boardId}")
    public ApiResponse<Void> deleteBoard(@AuthenticationPrincipal String userId, @PathVariable Long boardId) {
        boardService.deleteBoard(Long.valueOf(userId), boardId);
        return ApiResponse.success(null);
    }
}
