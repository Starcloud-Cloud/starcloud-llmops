package com.starcloud.ops.business.app.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 错误码
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
public interface ChatErrorCodeConstants {

    // ========== 机器人错误码 ==========

    /**
     * 聊天执行LLM总结异常
     */
    ErrorCode MEMORY_SUMMARY_ERROR = new ErrorCode(400100001, "AI思考异常，请稍后重试");


    /**
     * 聊天执行工具
     */
    ErrorCode TOOL_RUN_ERROR = new ErrorCode(400200001, "AI调用技能异常，请稍后重试");


    /**
     * 聊天功能权限判断
     * 模型切换
     */
    ErrorCode CONFIG_MODEL_ERROR = new ErrorCode(400300001, "大语言模型切换异常，你还没有模型权限[{}]");

    /**
     * 聊天功能权限判断
     * 联网
     */
    ErrorCode CONFIG_WEB_SEARCH_ERROR = new ErrorCode(400300002, "联网功能开启异常，你还没有联网权限");


}
