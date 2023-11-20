package com.starcloud.ops.business.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstant {

    ErrorCode NOTIFICATION_NOT_EXISTS = new ErrorCode(700010001, "通告不存在，{}");

    ErrorCode NOTIFICATION_STATUS_NOT_SUPPORT = new ErrorCode(700010002, "当前通告状态不支持，{}");

    ErrorCode MISSION_NOT_EXISTS = new ErrorCode(700010003, "任务不存在，{}");

    ErrorCode NOTIFICATION_NOT_BOUND_MISSION = new ErrorCode(700010004, "通告未绑定任务，{}");

    ErrorCode EXISTING_BOUND_CREATIVE = new ErrorCode(700010005, "存在已绑定的创作内容");

    ErrorCode MISSION_STATUS_NOT_SUPPORT = new ErrorCode(700010006, "当前状态不支持操作");

    ErrorCode NOTIFICATION_NAME_EXISTS = new ErrorCode(700010007, "通告名称已存在，{}");
}
