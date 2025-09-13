package com.wlinsk.ansure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wlinsk.ansure.model.entity.ChatSession;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.wlinsk.entity.ChatSession
 */
public interface ChatSessionMapper extends BaseMapper<ChatSession> {

    int save(@Param("entity") ChatSession entity);

    List<ChatSession> queryTitleRecords(@Param("userId") String userId);

    ChatSession queryBySessionId(@Param("sessionId") String sessionId);

    int updateTitle(@Param("question")String question, @Param("sessionId") String sessionId);

    int updateTime(@Param("sessionId") String sessionId);
}




