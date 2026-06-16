package com.project.ongojisik.domain.analysis.llm;

public interface FeatureExtractor {

    FeatureExtractionResult extract(String query);
}
