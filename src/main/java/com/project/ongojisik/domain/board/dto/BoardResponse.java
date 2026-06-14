package com.project.ongojisik.domain.board.dto;

import com.project.ongojisik.domain.board.entity.Board;
import com.project.ongojisik.domain.board.entity.BoardCategory;
import java.time.LocalDateTime;

public record BoardResponse(
        Long boardId,
        String title,
        String content,
        String imageUrl,
        BoardCategory category,
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
                board.getImageUrl(),
                board.getCategory(),
                board.getUser().getUserId(),
                board.getUser().getNickname(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }
}
