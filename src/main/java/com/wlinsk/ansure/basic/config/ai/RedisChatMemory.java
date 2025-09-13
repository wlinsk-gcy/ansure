package com.wlinsk.ansure.basic.config.ai;

import com.wlinsk.ansure.basic.config.holder.SpringContextHolder;
import com.wlinsk.ansure.basic.config.redis.RedisUtils;
import com.wlinsk.ansure.basic.utils.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Author: wlinsk
 * @Date: 2025/7/5
 */
public class RedisChatMemory implements ChatMemory {
    public static final String DEFAULT_PREFIX = "CHAT:";

    private final String prefix;

    private final RedisUtils redisUtils = SpringContextHolder.getBean(RedisUtils.class);

    public RedisChatMemory() {
        this.prefix = DEFAULT_PREFIX;
    }

    public RedisChatMemory(String prefix) {
        this.prefix = prefix;
    }



    @Override
    public void add(String conversationId, Message message) {
        if (Objects.isNull(message)) {
            return;
        }
        redisUtils.pushList(getKey(conversationId), MessageUtil.toJson(message));
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }
        redisUtils.pushAllList(getKey(conversationId),
                messages.stream().map(MessageUtil::toJson).toList());
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        if (lastN <= 0) {
            return List.of();
        }
        String redisKey = getKey(conversationId);
        List<String> rangeList = redisUtils.getRangeList(redisKey, lastN);
        return rangeList.stream()
                .filter(StringUtils::isNotBlank)
                .map(MessageUtil::toMessage)
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        redisUtils.delVal(getKey(conversationId));
    }

    private String getKey(String conversationId) {
        return prefix + conversationId;
    }
}
