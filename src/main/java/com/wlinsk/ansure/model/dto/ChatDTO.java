package com.wlinsk.ansure.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: wlinsk
 * @Date: 2025/7/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    /**
     * 用户的问题
     */
    private String question;

    /**
     * 会话id
     */
    private String sessionId;
}
