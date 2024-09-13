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

    ErrorCode  BUDGET_ERROR =  new ErrorCode(700010017, "通告总预算要大于单个任务预算");

    ErrorCode  CAN_NOT_REFRESH =  new ErrorCode(700010018, "先发布通告再刷新互动信息");

    ErrorCode  MISSION_IS_CHANGE =  new ErrorCode(700010019, "只有待认领状态任务可以认领,请刷新页面后重试");

    ErrorCode  MISSION_CAN_NOT_CLAIM =  new ErrorCode(700010020, "任务状态为{} ，只有待认领状态任务可以认领");

    ErrorCode  BUDGET_PRICE_ERROR =  new ErrorCode(700010021, "任务预算要大于单价之和");

    ErrorCode  MISSION_CAN_NOT_PUBLISH_STATUS =  new ErrorCode(700020001, "任务状态为{} ，只有认领状态任务可以发布");

    ErrorCode  MISSION_CAN_NOT_PUBLISH_USERID =  new ErrorCode(700020002, "只可以发布自己认领的任务");

    ErrorCode  MISSION_CAN_NOT_ABANDON_STATUS =  new ErrorCode(700020003, "任务状态为{} ，只有未结算的任务可以取消");

    ErrorCode  MISSION_CAN_NOT_ABANDON_USERID =  new ErrorCode(700020004, "只可以取消自己认领的任务");

    ErrorCode  RETRY =  new ErrorCode(700020004,"请重试");

    ErrorCode  ALL_CLAIMED =  new ErrorCode(700020005,"已全部领取，请领取其他任务");

    ErrorCode  MORE_THAN_CLAIMED_NUM =  new ErrorCode(700020006,"最多领取{}次，请领取其他任务");

    ErrorCode  NOTIFICATION_CLOSED =  new ErrorCode(700020007,"通告任务已关闭，请领取其他任务");

    ErrorCode  NOT_FOR_SELF =  new ErrorCode(700020008,"只能查询自己领取的任务");

    ErrorCode  CLAIM_TIME_END =  new ErrorCode(700020009,"认领时间结束，请领取其他任务");

    ErrorCode  END_TIME_OVER =  new ErrorCode(700020010,"任务时间结束");

    ErrorCode  GROUP_NOT_EXIST =  new ErrorCode(700020011,"用户分组不存在，请先绑定分组");

}
