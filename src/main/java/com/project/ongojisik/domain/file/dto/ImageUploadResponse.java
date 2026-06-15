package com.project.ongojisik.domain.file.dto;

import java.util.List;

public record ImageUploadResponse(
        List<String> imageUrls
) {
}
