package com.project.ongojisik.domain.analysis.service;

import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.entity.ImageGenerationJob;
import com.project.ongojisik.domain.analysis.entity.ImageGenerationStatus;
import com.project.ongojisik.domain.analysis.repository.ImageGenerationJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageGenerationWorker {

    private final ImageGenerationJobRepository imageGenerationJobRepository;
    private final FoodImageGenerationService foodImageGenerationService;

    // 추천 API 응답과 분리해서 이미지 생성 API 호출 및 S3 업로드를 백그라운드에서 처리한다.
    @Async("imageGenerationExecutor")
    @Transactional
    public void generate(Long jobId) {
        ImageGenerationJob job = imageGenerationJobRepository.findById(jobId).orElse(null);
        if (job == null || job.getStatus() == ImageGenerationStatus.COMPLETED) {
            return;
        }

        try {
            // worker가 작업을 잡은 시점부터 프론트에는 PROCESSING 상태로 노출된다.
            job.markProcessing();
            Food food = job.getFood();
            String imageUrl = foodImageGenerationService.generateAndStoreImageIfNeeded(food);
            if (!StringUtils.hasText(imageUrl)) {
                job.markFailed("Image generation did not return an image URL.");
                return;
            }
            job.markCompleted(imageUrl);
        } catch (Exception exception) {
            // 예외를 밖으로 던지지 않고 FAILED로 저장해 polling 클라이언트가 종료 상태를 받을 수 있게 한다.
            log.error("Failed to process image generation job: jobId={}", jobId, exception);
            job.markFailed(exception.getMessage());
        }
    }
}
