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

    ErrorCode NOTIFICATION_BUDGET_ERROR = new ErrorCode(700010008, "通告总预算要大于0");

    ErrorCode MISSION_BUDGET_ERROR = new ErrorCode(700010009, "单个任务预算要大于0");

    ErrorCode TOO_MANY_MISSION = new ErrorCode(700010010, "绑定太多任务，总预算不足");

    ErrorCode DONT_ALLOW_DELETE =  new ErrorCode(700010011, "存在已认领任务不允许删除");

    ErrorCode EXCEL_IS_EMPTY =  new ErrorCode(700010012, "导入的excel为空");

    ErrorCode  NOT_EXIST_UID =  new ErrorCode(700010014, "导入的uid不存在 {}");

    ErrorCode  CONTENT_INCONSISTENT =  new ErrorCode(700010015, "任务内容和用户发布内容不一致");

    ErrorCode  ONLY_STAY_CLAIM =  new ErrorCode(700010016, "只有待认领状态可以导入");
}
