package com.project.ongojisik.domain.analysis.llm;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.project.ongojisik.global.exception.APIException;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class FeatureTaxonomyCatalogTest {

    private final FeatureTaxonomyCatalog catalog = new FeatureTaxonomyCatalog(new ObjectMapper());

    @Test
    void loadsFeatureAndCategoryLabelsFromClasspathJson() {
        assertTrue(catalog.allowedFeatures().contains("매운맛"));
        assertTrue(catalog.allowedCategories().contains("국/탕류"));
    }

    @Test
    void rejectsLabelsOutsideTaxonomy() {
        assertDoesNotThrow(() -> catalog.validate(
                new FeatureExtractionResult(List.of("매운맛"), List.of("국/탕류"))
        ));

        assertThrows(APIException.class, () -> catalog.validate(
                new FeatureExtractionResult(List.of("없는특징"), List.of())
        ));
    }
}
