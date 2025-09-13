package com.wlinsk.ansure.model.entity;

import lombok.Data;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.content.Media;

import java.util.List;
import java.util.Map;

/**
 * @Author: wlinsk
 * @Date: 2025/7/5
 */
@Data
public class RedisMessage {
    private String messageType;
    private Map<String, Object> metadata = Map.of();
    private List<Media> media = List.of();
    private List<AssistantMessage.ToolCall> toolCalls = List.of();
    private String textContent;
    private List<ToolResponseMessage.ToolResponse> toolResponses = List.of();
    private Map<String, Object> properties = Map.of();
    private Map<String, Object> params = Map.of();
}
