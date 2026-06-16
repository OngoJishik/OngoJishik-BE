package com.project.ongojisik.domain.bookmark.dto;

import com.project.ongojisik.domain.bookmark.entity.Bookmark;

public record BookmarkResponse(
        String foodId,
        String foodName,
        String category,
        String foodFeature,
        String foodPicture
) {
    public static BookmarkResponse from(Bookmark bookmark) {
        return new BookmarkResponse(
                bookmark.getFood().getFoodId(),
                bookmark.getFood().getFoodName(),
                bookmark.getFood().getCategory(),
                bookmark.getFood().getFoodFeatures(),
                bookmark.getFood().getFoodPicture()
        );
    }
}
