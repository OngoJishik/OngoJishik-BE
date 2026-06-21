package com.project.ongojisik.domain.analysis.entity;

public enum ImageGenerationStatus {
    PENDING, // 이미지 생성 대기
    PROCESSING, // 이미지 생성 중
    COMPLETED,
    FAILED
}
