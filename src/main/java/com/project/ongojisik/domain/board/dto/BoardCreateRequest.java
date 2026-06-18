package com.project.ongojisik.domain.board.dto;

import com.project.ongojisik.domain.board.entity.BoardCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BoardCreateRequest(
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
