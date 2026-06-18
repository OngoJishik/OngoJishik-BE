package com.project.ongojisik.domain.home.dto;

import java.util.List;

public record HomeResponse(
        List<HomeFoodResponse> foods
) {

    public static HomeResponse from(List<HomeFoodResponse> foods) {
        return new HomeResponse(foods);
    }
}
