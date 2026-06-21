package com.project.ongojisik.domain.analysis.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "image_generation_job")
public class ImageGenerationJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ImageGenerationStatus status;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    // 이미지 생성 job 내부 실패 이유 (vs ApiResponse.message = "job 상태 조회 API 호출이 성공했는지/실패했는지")
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 추천 응답 이후 백그라운드에서 처리할 이미지 생성 작업을 생성한다.
    private ImageGenerationJob(Food food, LocalDateTime now) {
        this.food = food;
        this.status = ImageGenerationStatus.PENDING;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static ImageGenerationJob create(Food food) {
        return new ImageGenerationJob(food, LocalDateTime.now());
    }

    // worker가 실제 이미지 생성 API 호출을 시작했음을 기록한다.
    public void markProcessing() {
        this.status = ImageGenerationStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    // 이미지 생성과 저장이 끝나면 프론트가 polling으로 받을 이미지 URL을 저장한다.
    public void markCompleted(String imageUrl) {
        this.status = ImageGenerationStatus.COMPLETED;
        this.imageUrl = imageUrl;
        this.errorMessage = null;
        this.updatedAt = LocalDateTime.now();
    }

    // 실패 상태를 DB에 남겨 프론트가 무한 로딩하지 않고 실패 UI를 보여줄 수 있게 한다.
    public void markFailed(String errorMessage) {
        this.status = ImageGenerationStatus.FAILED;
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }
}
