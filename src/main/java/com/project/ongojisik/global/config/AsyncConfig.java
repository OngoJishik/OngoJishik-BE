package com.project.ongojisik.global.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    // 이미지 생성은 외부 API 호출과 S3 업로드가 포함된 느린 작업이므로 별도 스레드 풀에서 처리한다.
    @Bean(name = "imageGenerationExecutor")
    public Executor imageGenerationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 동시에 처리할 기본 작업 수와 순간적으로 늘릴 수 있는 최대 작업 수를 제한한다.
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        // 요청이 몰릴 때 즉시 거절하지 않고 대기시킬 이미지 생성 작업 수이다.
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("image-generation-");
        executor.initialize();
        return executor;
    }
}
