package com.project.ongojisik.domain.search.entity;

import com.project.ongojisik.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "search")
public class SearchHistory {

    private static final String DELIMITER = ",";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "search_id", nullable = false)
    private Long searchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "query", nullable = false)
    private String query;

    @Column(name = "extracted_features", columnDefinition = "TEXT")
    private String extractedFeatures;

    @Column(name = "recommended_food_ids", columnDefinition = "TEXT")
    private String recommendedFoodIds;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private SearchHistory(
            User user,
            String query,
            String extractedFeatures,
            String recommendedFoodIds,
            LocalDateTime createdAt
    ) {
        this.user = user;
        this.query = query;
        this.extractedFeatures = extractedFeatures;
        this.recommendedFoodIds = recommendedFoodIds;
        this.createdAt = createdAt;
    }

    public static SearchHistory create(
            User user,
            String query,
            List<String> extractedFeatures,
            List<String> recommendedFoodIds
    ) {
        return new SearchHistory(
                user,
                query,
                join(extractedFeatures),
                join(recommendedFoodIds),
                LocalDateTime.now()
        );
    }

    public List<String> getExtractedFeatureList() {
        return split(extractedFeatures);
    }

    public List<String> getRecommendedFoodIdList() {
        return split(recommendedFoodIds);
    }

    private static String join(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }

        return String.join(DELIMITER, values);
    }

    private static List<String> split(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }

        return Arrays.stream(value.split(DELIMITER))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }
}
