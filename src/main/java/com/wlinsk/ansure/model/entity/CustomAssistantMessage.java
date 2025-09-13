package com.wlinsk.ansure.model.entity;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.content.Media;

import java.util.List;
import java.util.Map;

/**
 * 主要为了扩展params字段
 * @Author: wlinsk
 * @Date: 2025/7/9
 */
public class CustomAssistantMessage extends AssistantMessage {
    private Map<String, Object> params = Map.of();

    public CustomAssistantMessage(String content) {
        super(content);
    }

    public CustomAssistantMessage(String content, Map<String, Object> properties) {
        super(content, properties);
    }

    public CustomAssistantMessage(String content, Map<String, Object> properties, List<ToolCall> toolCalls) {
        super(content, properties, toolCalls);
    }

    public CustomAssistantMessage(String content, Map<String, Object> properties, List<ToolCall> toolCalls, Map<String, Object> params) {
        super(content, properties, toolCalls);
        this.params = params;
    }

    public CustomAssistantMessage(String content, Map<String, Object> properties, List<ToolCall> toolCalls, List<Media> media) {
        super(content, properties, toolCalls, media);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
