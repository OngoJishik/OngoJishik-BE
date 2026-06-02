package com.project.ongojisik.domain.bookmark.dto;

import com.project.ongojisik.domain.bookmark.entity.Bookmark;
import java.time.LocalDateTime;

public record BookmarkResponse(
        Long bookmarkId,
        Long boardId,
        String title,
        String imageUrl,
        Long authorId,
        String authorNickname,
        LocalDateTime bookmarkedAt
) {
    public static BookmarkResponse from(Bookmark bookmark) {
        return new BookmarkResponse(
                bookmark.getBookmarkId(),
                bookmark.getBoard().getBoardId(),
                bookmark.getBoard().getTitle(),
                bookmark.getBoard().getImageUrl(),
                bookmark.getBoard().getUser().getUserId(),
                bookmark.getBoard().getUser().getNickname(),
                bookmark.getCreatedAt()
        );
    }
}
