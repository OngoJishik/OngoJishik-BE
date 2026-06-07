package com.project.ongojisik.domain.comment.dto;

import com.project.ongojisik.domain.comment.entity.Comment;
import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        Long boardId,
        Long authorId,
        String authorName,
        String commentContent,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getBoard().getBoardId(),
                comment.getUser().getUserId(),
                comment.getAuthorName(),
                comment.getCommentContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
