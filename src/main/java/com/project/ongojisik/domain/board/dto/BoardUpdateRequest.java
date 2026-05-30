package com.project.ongojisik.domain.board.dto;

import jakarta.validation.constraints.NotBlank;

public record BoardUpdateRequest(
        @NotBlank(message = "게시글 제목은 필수입니다.")
        String title,
        @NotBlank(message = "게시글 내용은 필수입니다.")
        String content,
        String imageUrl
) {
}
