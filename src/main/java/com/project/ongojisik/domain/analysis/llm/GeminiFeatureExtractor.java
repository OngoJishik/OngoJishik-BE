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
                    .system("""
                너는 전통 음식 추천 서비스의 음식 특징 추출기다.

                [역할]
                - 사용자의 자연어 입력을 분석하여 음식 추천에 사용할 features와 categories를 추출한다.
                - 출력값은 반드시 제공된 taxonomy JSON 안에 존재하는 label만 사용한다.
                - taxonomy에 없는 label, 유사어, 새 단어, 설명 문장은 절대 만들지 않는다.

                [추출 원칙]
                1. 사용자가 직접 언급한 특징을 가장 우선한다.
                2. 직접 언급하지 않았더라도 사용자 의도와 의미적으로 가까운 taxonomy label이 있으면 선택한다.
                3. 정확히 일치하는 label이 없으면 taxonomy 안에서 가장 가까운 label로 매핑한다.
                4. 음식 추천이 가능하도록 최소 1개 이상의 feature를 추출하려고 시도한다.
                5. 단, 사용자 입력에 음식 선호나 상황 단서가 전혀 없으면 features를 빈 배열로 둔다.
                6. categories는 음식 형태가 명확할 때만 선택한다.
                7. 관련성이 낮은 label을 억지로 선택하지 않는다.
                8. 같은 label을 중복해서 넣지 않는다.

                [출력 규칙]
                - 반드시 JSON 스키마에 맞게 응답한다.
                - features에는 taxonomy.feature_groups[*].features[*].label에 있는 값만 넣는다.
                - categories에는 taxonomy.categories[*].label에 있는 값만 넣는다.
                - label이 아닌 id, description, 임의 설명은 출력하지 않는다.
                - JSON 외의 설명, 마크다운, 코드 블록은 출력하지 않는다.
                """)
                    .user(user -> user
                            .text("""
                        다음 사용자 입력에서 음식 특징과 카테고리를 추출하라.

                        [사용자 입력]
                        {query}

                        [허용 taxonomy JSON]
                        {taxonomy}

                        [판단 기준]
                        - 사용자가 말한 표현과 정확히 같은 label이 없어도 된다.
                        - 의미가 가까운 label이 taxonomy 안에 있으면 그 label로 변환한다.
                        - 예를 들어 "알록달록한 음식"은 taxonomy에 "다채로운"이 있다면 "다채로운"으로 추출한다.
                        - 예를 들어 "속 편한 음식"은 taxonomy에 "부드러운", "담백한맛", "따뜻한" 등이 있다면 가장 가까운 label을 선택한다.
                        - 예를 들어 "비 오는 날 먹고 싶은 음식"은 taxonomy에 "따뜻한", "국물있는", "든든한" 등이 있다면 상황에 맞는 label을 선택한다.
                        - 단, taxonomy에 없는 label은 절대 만들지 않는다.
                        - 음식 추천과 무관한 추측은 하지 않는다.
                        """)
                            .param("query", query)
                            .param("taxonomy", taxonomyCatalog.promptCatalog()))
                    .call()
                    .entity(FeatureExtractionResult.class, ChatClient.EntityParamSpec::validateSchema);
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
