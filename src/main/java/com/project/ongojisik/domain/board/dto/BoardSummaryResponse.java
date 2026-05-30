package com.project.ongojisik.domain.board.dto;

import com.project.ongojisik.domain.board.entity.Board;
import java.time.LocalDateTime;

public record BoardSummaryResponse(
        Long boardId,
        String title,
        String imageUrl,
        Long authorId,
        String authorNickname,
        LocalDateTime createdAt
) {

    public static BoardSummaryResponse from(Board board) {
        return new BoardSummaryResponse(
                board.getBoardId(),
                board.getTitle(),
                board.getImageUrl(),
                board.getUser().getUserId(),
                board.getUser().getNickname(),
                board.getCreatedAt()
        );
    }
}
