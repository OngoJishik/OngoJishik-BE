package com.project.ongojisik.domain.board.dto;

import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.entity.BoardCategory;
import java.time.LocalDateTime;
import java.util.List;

public record BoardSummaryResponse(
        Long boardId,
        String title,
        List<String> imageUrls,
        BoardCategory category,
        String recipeId,
        Long likeCount,
        Long commentCount,
        boolean isLiked,
        Long authorId,
        String authorNickname,
        LocalDateTime createdAt
) {

    public static BoardSummaryResponse from(Board board) {
        return new BoardSummaryResponse(
                board.getBoardId(),
                board.getTitle(),
                board.getImageUrls(),
                board.getCategory(),
                board.getRecipeId(),
                0L,
                0L,
                false,
                board.getUser().getUserId(),
                board.getUser().getNickname(),
                board.getCreatedAt()
        );
    }
}
