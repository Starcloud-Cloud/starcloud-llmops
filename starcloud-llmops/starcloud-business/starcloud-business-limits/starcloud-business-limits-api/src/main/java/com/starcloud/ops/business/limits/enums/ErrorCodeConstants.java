package com.starcloud.ops.business.limits.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 用户限制错误码
 */
public interface ErrorCodeConstants {

    /**
     * 错误码解释 2-008-001-000
     * <p>
     *  2        ->  starCloud          业务
     *  008      ->  business-limit     模板
     *  001      ->  BENEFITS_STRATEGY  用户权益策略
     *  0  - 99  ->  数据校验
     *  100- 199 ->  逻辑校验
     * <p>
     *    用户权益策略    ->  2008001001
     * <p>
     *    用户权益       ->  2008002001
     * <p>
     *    用户权益使用记录 ->  2008003001
     * <p>
     *
     */

    //======================================用户权益策略-错误码区间 [2-008-001-000 ~ 2-008-001-100] ======================================
    ErrorCode BENEFITS_STRATEGY_DATA_NOT_EXISTS = new ErrorCode(2008001001, "策略数据不存在，请刷新后重试！");

    ErrorCode BENEFITS_STRATEGY_TYPE_NOT_EXISTS = new ErrorCode(2008001002, "策略类型不存在，请联系管理员");

    ErrorCode BENEFITS_STRATEGY_CAN_NOT_DELETE = new ErrorCode(2008001003, "该策略权益已经被使用，无法删除，如果你必须不使用该条配置，可以归档");

    ErrorCode BENEFITS_STRATEGY_CAN_NOT_MODIFY_ENABLE = new ErrorCode(2008001004, "该策略类型不可以修改，数据未停用，请启用后操作");
    ErrorCode BENEFITS_STRATEGY_CAN_NOT_MODIFY_NO_ENABLE = new ErrorCode(2008001004, "该策略类型不可以修改，数据未启用，请启用后操作");

    ErrorCode BENEFITS_STRATEGY_CAN_NOT_MODIFY_ARCHIVED = new ErrorCode(2008001005, "该策略类型不可以修改，数据已经归档！");

    ErrorCode BENEFITS_STRATEGY_CAN_NOT_MODIFY_USED = new ErrorCode(2008001006, "该策略权益已经被使用，无法修改");

    ErrorCode BENEFITS_STRATEGY_CAN_NOT_ENABLE_ENABLE = new ErrorCode(2008001007, "该策略类型修改失败，数据已经是启用状态，请启用后操作");

    ErrorCode BENEFITS_TYPE_NOT_EXISTS = new ErrorCode(2008003008, "操作失败，权益类型不存在");
    ErrorCode BENEFITS_STRATEGY_CODE_EXISTS = new ErrorCode(2008003009, "权益码已经存在，请重试！");

    ErrorCode BENEFITS_STRATEGY_SYS_DATA_NOT_EXISTS = new ErrorCode(2008001010, "策略数据不存在，请刷新后重试！");

    ErrorCode BENEFITS_STRATEGY_PREFIX_NO_VALIDITY = new ErrorCode(2008001002, "权益码不合法，请勿修改权益码前缀");

    //======================================用户权益==========================================
    ErrorCode USER_BENEFITS_NOT_EXISTS = new ErrorCode(2008002001, "权益不存在，请重新输入");
    ErrorCode USER_BENEFITS_GET_FAIL_MANY_TIMES = new ErrorCode(2008002002, "权益重复领取");

    ErrorCode USER_BENEFITS_ILLEGAL_USE = new ErrorCode(2008002003, "权益非法使用方式");

    ErrorCode USER_BENEFITS_CASH_COUNT_EXCEEDED = new ErrorCode(2008002004, "权益超出兑换次数");
    ErrorCode USER_BENEFITS_USAGE_FREQUENCY_EXCEEDED = new ErrorCode(2008002005, "权益使用频率超出限制");

    ErrorCode USER_BENEFITS_USELESS_INTEREST = new ErrorCode(2008002006, "无可用权益");
    ErrorCode USER_BENEFITS_USELESS_INSUFFICIENT = new ErrorCode(2008002007, "当前使用的{}不足");



    //======================================用户权益使用记录======================================
    ErrorCode BENEFITS_USAGE_LOG_NOT_EXISTS = new ErrorCode(2008003001, "用户权益使用日志不存在");

    ErrorCode BENEFITS_USAGE_LOG_ACTION_TYPE_NOT_EXISTS = new ErrorCode(2008003002, "操作失败，操作类型不存在");

    ErrorCode BENEFITS_USAGE_LOG_BENEFITS_TYPE_NOT_EXISTS = new ErrorCode(2008003003, "操作失败，权益类型不存在");
}
