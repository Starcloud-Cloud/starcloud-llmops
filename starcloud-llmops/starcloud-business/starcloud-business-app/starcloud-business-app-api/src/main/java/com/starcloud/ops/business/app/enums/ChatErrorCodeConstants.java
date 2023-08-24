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

    // ========== 应用错误码 ==========

    /**
     * 应用市场应用不存在
     */
    ErrorCode MEMORY_SUMMARY_ERROR = new ErrorCode(400100001, "思考异常，请稍后重试");

}
