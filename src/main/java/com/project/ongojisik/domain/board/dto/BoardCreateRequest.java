package com.project.ongojisik.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record BoardCreateRequest(
        @NotBlank(message = "게시글 제목은 필수입니다.")
        String title,
        @NotBlank(message = "게시글 내용은 필수입니다.")
        String content,
        List<String> imageUrls,
        List<String> category,
        String recipeId
) {
}
