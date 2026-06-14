package com.project.ongojisik.domain.analysis.llm;

import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class FeatureTaxonomyCatalog {

    private static final String FEATURE_PATH =
            "llm/taxonomy/ongojisik_feature_taxonomy_v1_1.json";
    private static final String CATEGORY_PATH =
            "llm/taxonomy/ongojisik_category_list_v1.json";

    private final Set<String> allowedFeatures;
    private final Set<String> allowedCategories;
    private final String promptCatalog;

    public FeatureTaxonomyCatalog(ObjectMapper objectMapper) {
        try {
            JsonNode featureRoot = readJson(objectMapper, FEATURE_PATH);
            JsonNode categoryRoot = readJson(objectMapper, CATEGORY_PATH);
            this.allowedFeatures = Set.copyOf(extractFeatureLabels(featureRoot));
            this.allowedCategories = Set.copyOf(extractCategoryLabels(categoryRoot));
            this.promptCatalog = objectMapper.writeValueAsString(Map.of(
                    "feature_groups", featureRoot.path("feature_groups"),
                    "categories", categoryRoot.path("categories")
            ));
        } catch (IOException exception) {
            throw new IllegalStateException("LLM taxonomy resources could not be loaded.", exception);
        }
    }

    public String promptCatalog() {
        return promptCatalog;
    }

    public List<String> allowedFeatures() {
        return List.copyOf(allowedFeatures);
    }

    public List<String> allowedCategories() {
        return List.copyOf(allowedCategories);
    }

    public void validate(FeatureExtractionResult result) {
        if (!allowedFeatures.containsAll(result.features())
                || !allowedCategories.containsAll(result.categories())) {
            throw new APIException(ErrorCode.LLM_INVALID_RESPONSE);
        }
    }

    private static JsonNode readJson(ObjectMapper objectMapper, String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readTree(inputStream);
        }
    }

    private static Set<String> extractFeatureLabels(JsonNode root) {
        Set<String> labels = new LinkedHashSet<>();
        root.path("feature_groups").forEach(group ->
                group.path("features").forEach(feature -> labels.add(feature.path("label").asText()))
        );
        validateLabels(labels, FEATURE_PATH);
        return labels;
    }

    private static Set<String> extractCategoryLabels(JsonNode root) {
        Set<String> labels = new LinkedHashSet<>();
        root.path("categories").forEach(category -> labels.add(category.path("label").asText()));
        validateLabels(labels, CATEGORY_PATH);
        return labels;
    }

    private static void validateLabels(Set<String> labels, String path) {
        List<String> invalidLabels = new ArrayList<>();
        labels.stream()
                .filter(String::isBlank)
                .forEach(invalidLabels::add);

        if (labels.isEmpty() || !invalidLabels.isEmpty()) {
            throw new IllegalStateException("Invalid labels in taxonomy resource: " + path);
        }
    }
}
