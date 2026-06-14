package com.project.ongojisik.domain.analysis.llm;

import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiFeatureExtractor implements FeatureExtractor {

    private final ChatClient foodFeatureChatClient;
    private final FeatureTaxonomyCatalog taxonomyCatalog;

    @Override
    public FeatureExtractionResult extract(String query) {
        if (!StringUtils.hasText(query)) {
            throw new APIException(ErrorCode.MISSING_REQUIRED_FIELD);
        }

        try {
            FeatureExtractionResult result = foodFeatureChatClient.prompt()
                    .user(user -> user
                            .text("""
                                    아래 사용자 입력을 분석해 음식 특징과 카테고리를 추출하라.

                                    [사용자 입력]
                                    {query}

                                    [추출 기준]
                                    - 직접 언급됐거나 의미가 명확히 대응되는 label만 선택한다.
                                    - 맛, 색감, 온도와 무게감, 조리 방식, 음식 형태, 상황과 목적을 검토한다.
                                    - 적절한 값이 없으면 해당 배열을 비워 둔다.

                                    [출력 필드]
                                    - features: taxonomy의 feature.label 목록
                                    - categories: taxonomy의 categories.label 목록
                                    """)
                            .param("query", query))
                    .call()
                    .entity(FeatureExtractionResult.class, spec -> spec.validateSchema());

            if (result == null) {
                throw new APIException(ErrorCode.LLM_INVALID_RESPONSE);
            }

            taxonomyCatalog.validate(result);
            return result;
        } catch (APIException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            log.error("Gemini feature extraction request failed", exception);
            throw new APIException(ErrorCode.LLM_REQUEST_FAILED, exception);
        }
    }
}
