package com.project.ongojisik.domain.analysis.repository;

import com.project.ongojisik.domain.analysis.entity.ImageGenerationJob;
import com.project.ongojisik.domain.analysis.entity.ImageGenerationStatus;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageGenerationJobRepository extends JpaRepository<ImageGenerationJob, Long> {

    // 같은 음식의 PENDING/PROCESSING job을 재사용해 중복 이미지 생성 요청을 줄인다.
    Optional<ImageGenerationJob> findFirstByFoodFoodIdAndStatusInOrderByCreatedAtDesc(
            String foodId,
            Collection<ImageGenerationStatus> statuses
    );
}
