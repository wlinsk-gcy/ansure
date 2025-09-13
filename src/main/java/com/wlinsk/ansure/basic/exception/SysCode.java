package com.wlinsk.ansure.basic.exception;



public enum SysCode implements ReturnCode {
    success("00000", "success"),

    SYSTEM_ERROE("9999", "system error"),
    ENUM_ERROR("9998", "enum typeHandler error"),
    DATABASE_DELETE_ERROR("9997", "database delete result no 1"),
    DATABASE_INSERT_ERROR("9996", "database insert result no 1"),
    DATABASE_UPDATE_ERROR("9995", "database update result no 1"),
    REDIS_EXPIRED_TIME_ERROR("9994", "redis expired time error"),
    TRANSACTION_EXCEPTION("9993", "TransactionException"),
    HTTP_CLINT_ERROR("9992", "http clint error"),
    SYS_TOKEN_EXPIRE("9991", "Invalid token"),// token失效
    SYS_URL_UNAUTHORIZED("9990", "权限不足"),
    DATA_NOT_FOUND("9989", "数据不存在"),
    SYSTEM_FILE_UPLOAD_ERROR("9988", "文件上传失败"),
    SYSTEM_NO_PERMISSION("9987", "没有操作权限"),
    SYSTEM_STRATEGY_HANDLER_NO_FOUND("9986", "策略处理器不存在"),
    NOT_SUPPORT_OPERATION("9985", "不支持的操作"),
    THREE_PART_LOGIN_ERROR("9984", "第三方登录失败"),

    PARAMETER_ERROR("9000", "Parameter validation error"),
    USER_ACCOUNT_ALREADY_EXIST("10000", "账号已存在"),
    USER_ACCOUNT_NOT_EXIST("10001", "账号不存在"),
    USER_ACCOUNT_PASSWORD_ERROR("10002", "账号或密码有误，请重新输入"),
    USER_ACCOUNT_PASSWORD_ERROR_TIMES_EXCEED("10003", "Account or password error times exceed"),
    USER_ACCOUNT_PASSWORD_ERROR_TIMES_EXCEED_LOCK("10004", "Account or passworderror times exceed, account locked"),
    USER_REGISTER_ERROR("10005","用户注册失败，请稍后重试"),
    USER_DISABLED("10006","当前用户已被禁用，请联系管理员处理"),
    USER_PASSWORD_ERROR("10007","密码错误，请重新输入"),
    USER_PASSWORD_NOT_SAME("10008","新密码与确认密码不一致，请重新输入"),
    USER_PROFILE_LENGTH_ERROR("10009","用户简介长度不能超过256字符"),
    SEND_VERIFY_CODE_TOO_OFTEN("10010","发送验证码太频繁，请稍后重试。"),
    UNKNOW_VERIFY_CODE("10011","验证码已过期"),

    APP_REVIEW_STATUS_HAS_CHANGED("11000","应用状态已修改"),
    APP_NOT_REVIEW("11001","当前应用未审核"),
    SCORING_RESULT_RANGE_IS_NULL("11002","结果得分范围不可为空"),
    SCORING_PROP_IS_NULL("11003","结果属性集合不可为空"),
    APP_ID_NOT_EXIST("11004","应用Id不可为空"),
    QUESTION_NUMBER_NOT_EXIST("11005","题目数量不可为空"),
    OPTION_NUMBER_NOT_EXIST("11006","选项数量不可为空"),
    NOT_SUPPORT_TEST_APP("11007","暂不支持测评类的应用"),
    NOT_SUPPORT_AI_SCORE("11008","暂不支持AI评分"),
    SCORE_NON_ZERO("11009","每道题目的总选项得分不能为0"),
    QUESTION_NON_FOUND("11010","当前应用未设置题目，无法审核"),
    SCORING_RESULT_NON_FOUND("11011","当前应用未评分规则，无法审核"),

    AI_SERVICE_ERROR("12000","AI服务异常，请稍后重试。"),
    USER_AI_POINT_NOT_ENOUGH("12001","用户AI点数不足"),
    AI_POINT_NOT_ENOUGH("12002","AI积分不足，请明天再来"),
    AI_GENERATE_QUESTION_ERROR("12003","AI生成题目渠道繁忙，请稍后重试。"),
    AI_POLISH_ERROR("12003","AI润色/改写渠道繁忙，请稍后重试。"),
    ;


    private final String code;

    private final String message;

    SysCode(String code, String message) {
        this.code = code;
        this.message = message;
    }


    public String getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }
}
