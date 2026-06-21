package com.project.ongojisik.domain.board.dto;

import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.entity.BoardCategory;
import java.time.LocalDateTime;
import java.util.List;

public record BoardResponse(
        Long boardId,
        String title,
        String content,
        List<String> imageUrls,
        BoardCategory category,
        String recipeId,
        Long likeCount,
        Long commentCount,
        boolean isLiked,
        Long authorId,
        String authorNickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getBoardId(),
                board.getTitle(),
                board.getContent(),
                board.getImageUrls(),
                board.getCategory(),
                board.getRecipeId(),
                0L,
                0L,
                false,
                board.getUser().getUserId(),
                board.getUser().getNickname(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }
}
