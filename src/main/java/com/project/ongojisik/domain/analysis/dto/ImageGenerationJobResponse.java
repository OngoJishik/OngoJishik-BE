package com.project.ongojisik.domain.analysis.dto;

import com.project.ongojisik.domain.analysis.entity.ImageGenerationJob;
import com.project.ongojisik.domain.analysis.entity.ImageGenerationStatus;

// 이미지 생성 상태 polling API에서 프론트로 내려주는 응답 DTO
public record ImageGenerationJobResponse(
        Long jobId,
        String foodId,
        ImageGenerationStatus status,
        String imageUrl,
        String errorMessage
) {

    public static ImageGenerationJobResponse from(ImageGenerationJob job) {
        return new ImageGenerationJobResponse(
                job.getJobId(),
                job.getFood().getFoodId(),
                job.getStatus(),
                job.getImageUrl(),
                job.getErrorMessage()
        );
    }
}
