package com.wlinsk.ansure.model.vo;

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
public class ChatEventVO {
    /**
     * 文本内容
     */
    private Object eventData;

    /**
     * 事件类型，1001-数据事件，1002-停止事件，1003-参数事件，1004-异常事件
     */
    private int eventType;
}
