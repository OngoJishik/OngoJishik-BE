package com.project.ongojisik.domain.analysis.service;

import com.project.ongojisik.domain.analysis.dto.ImageGenerationJobResponse;
import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.entity.ImageGenerationJob;
import com.project.ongojisik.domain.analysis.entity.ImageGenerationStatus;
import com.project.ongojisik.domain.analysis.repository.ImageGenerationJobRepository;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ImageGenerationJobService {

    // 같은 음식에 대해 대기 중이거나 처리 중인 작업이 있으면 새 job을 만들지 않는다.
    private static final List<ImageGenerationStatus> ACTIVE_STATUSES = List.of(
            ImageGenerationStatus.PENDING,
            ImageGenerationStatus.PROCESSING
    );

    private final ImageGenerationJobRepository imageGenerationJobRepository;
    private final ImageGenerationWorker imageGenerationWorker;

    // 이미지가 이미 있으면 job이 필요 없고, 없으면 프론트가 추적할 수 있는 job을 생성하거나 재사용한다.
    public ImageGenerationJob requestImageGenerationIfNeeded(Food food) {
        if (food == null || StringUtils.hasText(food.getFoodPicture())) {
            return null;
        }

        ImageGenerationJob job = imageGenerationJobRepository
                .findFirstByFoodFoodIdAndStatusInOrderByCreatedAtDesc(food.getFoodId(), ACTIVE_STATUSES)
                .orElseGet(() -> imageGenerationJobRepository.save(ImageGenerationJob.create(food)));

        startAfterCommit(job.getJobId());
        return job;
    }

    // polling API에서 jobId로 현재 이미지 생성 상태를 조회할 때 사용한다.
    @Transactional(readOnly = true)
    public ImageGenerationJobResponse getJob(Long jobId) {
        ImageGenerationJob job = imageGenerationJobRepository.findById(jobId)
                .orElseThrow(() -> new APIException(ErrorCode.IMAGE_GENERATION_JOB_NOT_FOUND));
        return ImageGenerationJobResponse.from(job);
    }

    // 추천/검색 기록 저장 트랜잭션이 커밋된 뒤 worker를 실행해, worker가 저장 전 데이터를 조회하는 상황을 막는다.
    private void startAfterCommit(Long jobId) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    imageGenerationWorker.generate(jobId);
                }
            });
            return;
        }

        imageGenerationWorker.generate(jobId);
    }
}
