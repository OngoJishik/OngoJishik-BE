package com.project.ongojisik.domain.analysis.dto;

import java.util.List;

public record RecommendResponse(
        String originalQuery,
        List<String> extractedFeatures,
        List<FoodSummaryResponse> recommendations
) {
}
