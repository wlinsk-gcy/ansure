package com.wlinsk.ansure.model.vo;

import com.wlinsk.ansure.basic.enums.MessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author: wlinsk
 * @Date: 2025/7/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {
    private String id;
    /**
     * 消息类型，USER表示用户提问，ASSISTANT表示AI的回答
     */
    private MessageTypeEnum type;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 附加参数
     */
    private Map<String, Object> params;
}
