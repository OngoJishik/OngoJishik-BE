package com.project.ongojisik.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient foodFeatureChatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
