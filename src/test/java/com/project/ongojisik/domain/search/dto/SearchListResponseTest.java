package com.project.ongojisik.domain.search.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchListResponseTest {

    @Test
    void fromWrapsSearches() {
        SearchSummaryResponse search = new SearchSummaryResponse(
                1L,
                "spicy food",
                LocalDateTime.of(2026, 6, 14, 21, 0)
        );

        SearchListResponse response = SearchListResponse.from(List.of(search));

        assertThat(response.searches()).containsExactly(search);
    }
}
