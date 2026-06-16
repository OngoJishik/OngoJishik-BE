package com.project.ongojisik.domain.analysis.llm;

import java.util.List;

public record FeatureExtractionResult(
        List<String> features,
        List<String> categories
) {

    public FeatureExtractionResult {
        features = features == null ? List.of() : List.copyOf(features);
        categories = categories == null ? List.of() : List.copyOf(categories);
    }

    public List<String> searchTerms() {
        return java.util.stream.Stream.concat(categories.stream(), features.stream())
                .distinct()
                .toList();
    }
}
