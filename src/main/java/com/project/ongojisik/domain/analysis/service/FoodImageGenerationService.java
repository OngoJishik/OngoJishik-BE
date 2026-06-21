package com.project.ongojisik.domain.analysis.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import com.project.ongojisik.global.storage.ImageStorageService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodImageGenerationService {

    private final ImageStorageService imageStorageService;

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @Value("${app.gemini.image-generation.model:gemini-3.1-flash-image}")
    private String imageModel;

    @Value("${app.gemini.image-generation.enabled:true}")
    private boolean enabled;

    public String generateAndStoreImageIfNeeded(Food food) {
        if (food == null || StringUtils.hasText(food.getFoodPicture())) {
            return food == null ? "" : food.getFoodPicture();
        }

        if (!enabled || !StringUtils.hasText(apiKey)) {
            if (!enabled) {
                log.warn("Food image generation is disabled: foodId={}, foodName={}", food.getFoodId(), food.getFoodName());
                return food.getFoodPicture();
            }

            log.error("Gemini API key is not configured for food image generation: foodId={}, foodName={}",
                    food.getFoodId(), food.getFoodName());
            throw new APIException(ErrorCode.LLM_NOT_CONFIGURED);
        }

        if (!StringUtils.hasText(imageModel)) {
            log.error("Gemini image generation model is not configured: foodId={}, foodName={}",
                    food.getFoodId(), food.getFoodName());
            throw new APIException(ErrorCode.LLM_NOT_CONFIGURED);
        }

        try {
            GeneratedImage generatedImage = generateImage(food);
            String imageUrl = imageStorageService.uploadImage(
                    generatedImage.bytes(),
                    normalizeMimeType(generatedImage.mimeType()),
                    "food-images"
            );
            food.updateFoodPicture(imageUrl);
            return imageUrl;
        } catch (APIException exception) {
            throw exception;
        } catch (IllegalStateException | IllegalArgumentException exception) {
            log.error("Invalid Gemini image generation response: foodId={}, foodName={}",
                    food.getFoodId(), food.getFoodName(), exception);
            throw new APIException(ErrorCode.LLM_INVALID_RESPONSE, exception);
        } catch (Exception exception) {
            log.error("Failed to generate or store food image: foodId={}, foodName={}",
                    food.getFoodId(), food.getFoodName(), exception);
            throw new APIException(ErrorCode.LLM_REQUEST_FAILED, exception);
        }
    }

    private String normalizeMimeType(String mimeType) {
        if (StringUtils.hasText(mimeType)) {
            return mimeType;
        }

        return MediaType.IMAGE_PNG_VALUE;
    }

    private GeneratedImage generateImage(Food food) {
        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseModalities("TEXT", "IMAGE")
                .build();

        try (Client client = Client.builder().apiKey(apiKey).build()) {
            GenerateContentResponse response = client.models.generateContent(
                    imageModel,
                    createPrompt(food),
                    config
            );

            return findGeneratedImage(response);
        }
    }

    private String createPrompt(Food food) {
        return """
            Create a realistic and appetizing photo of this Korean traditional food.
            The food name, ingredients, and key visual characteristics must be clearly reflected in the image.
            Make the main ingredient or defining ingredient visually noticeable.
            If the food name contains a specific ingredient, show that ingredient clearly in the food's appearance.
            For example, if the food is chestnut rice cake, do not make it look like a plain rice cake.
            Show visible chestnut pieces on the surface or inside the rice cake.

            Do not include text, letters, labels, watermarks, logos, people, hands, or packaging.
            Use a clean plate or bowl, a neat table setting, and natural lighting.
            Use a close-up food photography composition so the color, texture, shape, and ingredients are clearly visible.
            Make the image look like a realistic Korean traditional food photograph, not an illustration.

            Food name: %s
            Category: %s
            Features: %s
            Ingredients: %s
            Recipe: %s
            """.formatted(
                nullToEmpty(food.getFoodName()),
                nullToEmpty(food.getCategory()),
                nullToEmpty(food.getFoodFeatures()),
                nullToEmpty(food.getIngredients()),
                nullToEmpty(food.getRecipe())
        );
    }

    private GeneratedImage findGeneratedImage(GenerateContentResponse response) {
        if (response == null || response.parts() == null) {
            throw new IllegalStateException("Gemini image generation response is empty.");
        }

        return response.parts().stream()
                .map(Part::inlineData)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(blob -> blob.data().isPresent() && blob.data().get().length > 0)
                .map(blob -> new GeneratedImage(
                        blob.data().get(),
                        blob.mimeType().orElse(MediaType.IMAGE_PNG_VALUE)
                ))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Gemini response does not contain image data."));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private record GeneratedImage(
            byte[] bytes,
            String mimeType
    ) {
    }
}
