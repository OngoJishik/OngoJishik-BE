package com.project.ongojisik.domain.search.dto;

import jakarta.validation.constraints.NotBlank;

public record SearchRequest(
        @NotBlank(message = "query is required.")
        String query
) {
}
