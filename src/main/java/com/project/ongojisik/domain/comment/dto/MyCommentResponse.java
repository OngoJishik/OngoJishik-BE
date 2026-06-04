package com.project.ongojisik.domain.comment.dto;

import com.project.ongojisik.domain.comment.entity.Comment;
import java.time.LocalDateTime;

public record MyCommentResponse(
        Long commentId,
        Long boardId,
        String boardTitle,
        String commentContent,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MyCommentResponse from(Comment comment) {
        return new MyCommentResponse(
                comment.getCommentId(),
                comment.getBoard().getBoardId(),
                comment.getBoard().getTitle(),
                comment.getCommentContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
