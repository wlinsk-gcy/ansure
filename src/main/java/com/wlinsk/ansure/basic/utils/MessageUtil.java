package com.wlinsk.ansure.basic.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson2.JSON;
import com.wlinsk.ansure.basic.config.holder.SpringContextHolder;
import com.wlinsk.ansure.basic.id_generator.IdUtils;
import com.wlinsk.ansure.basic.config.redis.RedisUtils;
import com.wlinsk.ansure.model.constant.Constant;
import com.wlinsk.ansure.model.entity.CustomAssistantMessage;
import com.wlinsk.ansure.model.entity.RedisMessage;
import com.wlinsk.ansure.basic.enums.MessageTypeEnum;
import org.springframework.ai.chat.messages.*;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wlinsk
 * @Date: 2025/7/5
 */
public class MessageUtil {

    public static String toJson(Message message) {
        RedisMessage bean = BeanUtil.toBean(message, RedisMessage.class);
        if (MessageTypeEnum.USER.name().equals(bean.getMessageType())) {
            bean.getMetadata().putIfAbsent("id", IdUtils.build("user:"));
        }
        bean.setTextContent(message.getText());
        if (message instanceof AssistantMessage assistantMessage) {
            bean.setToolCalls(assistantMessage.getToolCalls());
            String messageId = Convert.toStr(assistantMessage.getMetadata().get(Constant.ID));
            RedisUtils redisUtils = SpringContextHolder.getBean(RedisUtils.class);
            String requestId = Convert.toStr(redisUtils.getHashVal(messageId, Constant.REQUEST_ID));
            Map<Object, Object> params = redisUtils.getHashStringAll(requestId);
            if (!CollectionUtils.isEmpty(params)) {
                bean.setParams(convertMap(params));
            }
            redisUtils.delVal(messageId);
        }
        if (message instanceof ToolResponseMessage toolResponseMessage) {
            bean.setToolResponses(toolResponseMessage.getResponses());
        }
        return JSON.toJSONString(bean);
    }

    public static Message toMessage(String json) {
        RedisMessage redisMessage = JSON.parseObject(json, RedisMessage.class);
        MessageType messageType = MessageType.valueOf(redisMessage.getMessageType());
        switch (messageType) {
            case SYSTEM -> {
                return new SystemMessage(redisMessage.getTextContent());
            }
            case USER -> {
                return new UserMessage(redisMessage.getTextContent(), redisMessage.getMedia(), redisMessage.getMetadata());
            }
            case ASSISTANT -> {
                return new CustomAssistantMessage(redisMessage.getTextContent(), redisMessage.getProperties(),
                        redisMessage.getToolCalls(), redisMessage.getParams());
            }
            case TOOL -> {
                return new ToolResponseMessage(redisMessage.getToolResponses(), redisMessage.getMetadata());
            }
        }
        throw new RuntimeException("Message data conversion failed.");
    }


    public static Map<String, Object> convertMap(Map<Object, Object> source) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : source.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key instanceof String) {
                result.put((String) key, value);
            } else {
                // 可选：处理非字符串 key
                result.put(String.valueOf(key), value);
            }
        }
        return result;
    }

}
