package com.wlinsk.ansure.basic.config;

import com.wlinsk.ansure.model.vo.SessionVO;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author: wlinsk
 * @Date: 2025/7/4
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "assistant.session")
public class SessionProperties {
    /**
     * AI助手的标题，用于显示助手的名称或身份。
     */
    private String title;

    /**
     * AI助手的描述，简要介绍助手的功能或特点。
     */
    private String describe;

    /**
     * 示例列表，包含一些使用助手的示例。
     */
    private List<SessionVO.Example> examples;
}
