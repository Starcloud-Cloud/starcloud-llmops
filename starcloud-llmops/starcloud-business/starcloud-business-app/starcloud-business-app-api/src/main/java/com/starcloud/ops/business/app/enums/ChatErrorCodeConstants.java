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

}
