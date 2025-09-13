package com.wlinsk.ansure.basic.config.ai;

import com.wlinsk.ansure.tools.AppTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wlinsk
 * @Date: 2025/7/5
 */
@Configuration
public class SpringAIConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory, AppTools appTools){
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor())
                .defaultTools(appTools)
                .build();
    }
    @Bean
    public ChatClient simpleChatClient(ChatModel chatModel){
        return ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }

    @Bean
    public ChatMemory chatMemory(){
        return new RedisChatMemory();
    }
}
