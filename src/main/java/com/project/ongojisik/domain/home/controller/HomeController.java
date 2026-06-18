package com.project.ongojisik.domain.home.controller;

import com.project.ongojisik.domain.home.dto.HomeResponse;
import com.project.ongojisik.domain.home.service.HomeService;
import com.project.ongojisik.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "홈", description = "홈 화면 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "오늘의 추천 전통음식 조회")
    @GetMapping
    public ApiResponse<HomeResponse> getTodayFood() {
        return ApiResponse.success(homeService.getTodayFoods());
    }
}
