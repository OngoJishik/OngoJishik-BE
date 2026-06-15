package com.project.ongojisik.domain.analysis.controller;

import com.project.ongojisik.domain.analysis.dto.FoodDetailResponse;
import com.project.ongojisik.domain.analysis.dto.RecommendRequest;
import com.project.ongojisik.domain.analysis.dto.RecommendResponse;
import com.project.ongojisik.domain.analysis.service.RecommendService;
import com.project.ongojisik.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "음식 추천", description = "사용자 자연어 기반 전통 음식 추천 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class RecommendController {
    private final RecommendService recommendService;

    @Operation(
            summary = "음식 추천 결과 조회",
            description = "사용자가 입력한 문장을 기반으로 전통 음식 3개를 추천합니다."
    )
    @PostMapping("/recommend")
    public ApiResponse<RecommendResponse> recommend(
            @RequestBody RecommendRequest request
    ) {
        return ApiResponse.success(recommendService.recommend(request.query()));
    }

    @Operation(
            summary = "음식 상세 정보 조회",
            description = "추천 음식 ID로 음식 상세 정보를 조회합니다."
    )
    @GetMapping("/{foodId}")
    public ApiResponse<FoodDetailResponse> getFoodDetail(
            @AuthenticationPrincipal String userId,
            @PathVariable String foodId
    ) {
        return ApiResponse.success(
                "음식 상세 정보 조회에 성공했습니다.",
                recommendService.getFoodDetail(Long.valueOf(userId), foodId)
        );
    }
}
