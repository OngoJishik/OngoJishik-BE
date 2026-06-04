package com.project.ongojisik.domain.boardlike.dto;

public record BoardLikeResponse(
        Long boardId,
        boolean liked,
        Long likeCount
) {
    public static BoardLikeResponse of(Long boardId, boolean liked, Long likeCount) {
        return new BoardLikeResponse(boardId, liked, likeCount);
    }
}
