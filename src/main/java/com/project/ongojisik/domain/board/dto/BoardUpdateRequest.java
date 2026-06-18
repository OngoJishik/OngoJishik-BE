package com.project.ongojisik.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record BoardUpdateRequest(
        @NotBlank(message = "게시글 제목은 필수입니다.")
        String title,
        @NotBlank(message = "게시글 내용은 필수입니다.")
        String content,
        List<String> imageUrls,
        @NotNull(message = "게시글 카테고리는 필수입니다.")
        BoardCategory category,
        List<String> hashtag,
        String recipeId
) {
}
