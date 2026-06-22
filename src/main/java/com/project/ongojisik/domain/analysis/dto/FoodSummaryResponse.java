package com.project.ongojisik.domain.analysis.dto;

import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.entity.ImageGenerationJob;
import com.project.ongojisik.domain.analysis.entity.ImageGenerationStatus;
import com.project.ongojisik.domain.analysis.util.FoodTextUtils;
import java.util.List;
import org.springframework.util.StringUtils;

public record FoodSummaryResponse(
        String foodId,
        String foodName,
        String category,
        List<String> features,
        String foodPicture,
        ImageGenerationStatus imageStatus,
        Long imageJobId
) {
    public static FoodSummaryResponse from(Food food) {
        return from(food, null);
    }

    public static FoodSummaryResponse from(Food food, ImageGenerationJob imageGenerationJob) {
        return new FoodSummaryResponse(
                food.getFoodId(),
                food.getFoodName(),
                food.getCategory(),
                FoodTextUtils.splitComma(food.getFoodFeatures()),
                food.getFoodPicture(),
                resolveImageStatus(food, imageGenerationJob),
                imageGenerationJob == null ? null : imageGenerationJob.getJobId()
        );
    }

    private static ImageGenerationStatus resolveImageStatus(Food food, ImageGenerationJob imageGenerationJob) {
        if (StringUtils.hasText(food.getFoodPicture())) {
            return ImageGenerationStatus.COMPLETED;
        }

        if (imageGenerationJob != null) {
            return imageGenerationJob.getStatus();
        }

        return ImageGenerationStatus.PENDING;
    }
}
