package com.wlinsk.ansure.model.constant;

/**
 * @Author: wlinsk
 * @Date: 2025/7/6
 */
public interface Constant {

    String REQUEST_ID = "requestId";
    String STOP = "STOP";
    String ID = "id";

    interface Tools {
        String QUERY_APP_BY_APP_NAME = "根据App名称模糊查询课程列表";
        String QUERY_LAST_APP_ID = "获取当前最新的可用题库应用ID";
    }

    interface ToolsTaskType {
        String QUERY_APP_BY_APP_NAME = "appInfos";
        String QUERY_LAST_APP_ID = "QUERY_LAST_APP_ID";
    }

    interface ToolParams {
        String APP_NAME = "app_name";
    }

    interface Redis {
        String VERIFY_TIME = "verify_time";
        String VERIFY_CODE = "verify_code";
    }
}