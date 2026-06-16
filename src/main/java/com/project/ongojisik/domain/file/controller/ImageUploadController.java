package com.project.ongojisik.domain.file.controller;

import com.project.ongojisik.domain.file.dto.ImageUploadResponse;
import com.project.ongojisik.global.response.ApiResponse;
import com.project.ongojisik.global.storage.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "파일 업로드", description = "이미지 파일 업로드 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uploads")
public class ImageUploadController {

    private final ImageStorageService imageStorageService;

    @Operation(summary = "이미지 업로드", description = "여러 이미지를 업로드하고 저장된 URL 목록을 반환합니다.")
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ImageUploadResponse> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        return ApiResponse.success(new ImageUploadResponse(imageStorageService.uploadImages(files)));
    }
}
