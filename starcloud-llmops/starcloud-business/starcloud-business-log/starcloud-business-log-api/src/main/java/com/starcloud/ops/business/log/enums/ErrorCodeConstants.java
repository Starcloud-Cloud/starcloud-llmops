package com.starcloud.ops.business.log.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * @author df007df
 */
public interface ErrorCodeConstants {


    ErrorCode APP_CONVERSATION_NOT_EXISTS = new ErrorCode(1003001001, "日志参数不可以为空");

    ErrorCode APP_MESSAGE_FEEDBACKS_NOT_EXISTS = new ErrorCode(1003001001, "日志参数不可以为空");

    ErrorCode APP_MESSAGE_ANNOTATIONS_NOT_EXISTS = new ErrorCode(1003001001, "日志参数不可以为空");

    ErrorCode APP_MESSAGE_SAVE_NOT_EXISTS = new ErrorCode(1003001001, "日志参数不可以为空");
}
