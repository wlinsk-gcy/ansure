package com.wlinsk.ansure.tools;

import com.alibaba.fastjson2.JSON;
import com.wlinsk.ansure.basic.config.redis.RedisUtils;
import com.wlinsk.ansure.mapper.AppMapper;
import com.wlinsk.ansure.model.constant.Constant;
import com.wlinsk.ansure.model.entity.App;
import com.wlinsk.ansure.model.entity.AppInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Author: wlinsk
 * @Date: 2025/7/6
 */
@Component
@RequiredArgsConstructor
public class AppTools {
    private final AppMapper appMapper;
    private final RedisUtils redisUtils;

    @Tool(description = Constant.Tools.QUERY_APP_BY_APP_NAME)
    public List<AppInfo> queryAppByAppName(@ToolParam(description = Constant.ToolParams.APP_NAME) String appName, ToolContext toolContext) {
        List<AppInfo> appInfos = queryList(appName);
        String requestId = (String) toolContext.getContext().get(Constant.REQUEST_ID);
        redisUtils.setHashVal(requestId, Constant.ToolsTaskType.QUERY_APP_BY_APP_NAME, JSON.toJSONString(appInfos));
        return appInfos;
    }

    @Tool(description = Constant.Tools.QUERY_LAST_APP_ID)
    public String queryLastAppId(ToolContext toolContext) {
        AppInfo appInfo = queryLast();
        if (Objects.isNull(appInfo)) {
            return null;
        }
        String requestId = (String) toolContext.getContext().get(Constant.REQUEST_ID);
        redisUtils.setHashVal(requestId, Constant.ToolsTaskType.QUERY_LAST_APP_ID, appInfo.getAppId());
        return appInfo.getAppId();
    }

    public List<AppInfo> queryList(String appName) {
        List<App> apps = appMapper.queryFiveRecords(appName);
        if (CollectionUtils.isEmpty(apps)) {
            return List.of();
        }
        return apps.stream().map(app -> {
            AppInfo respDTO = new AppInfo();
            BeanUtils.copyProperties(app, respDTO);
            respDTO.setAppType(app.getAppType().getMessage());
            respDTO.setScoringStrategy(app.getScoringStrategy().getMessage());
            respDTO.setReviewStatus(app.getReviewStatus().getMessage());
            return respDTO;
        }).toList();
    }

    public AppInfo queryLast() {
        App app = appMapper.queryLast();
        if (Objects.isNull(app)) {
            return new AppInfo();
        }
        AppInfo respDTO = new AppInfo();
        BeanUtils.copyProperties(app, respDTO);
        respDTO.setAppType(app.getAppType().getMessage());
        respDTO.setScoringStrategy(app.getScoringStrategy().getMessage());
        respDTO.setReviewStatus(app.getReviewStatus().getMessage());
        return respDTO;
    }


}
