package com.starcloud.ops.business.app.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 错误码
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
public interface ErrorCodeConstants {

    // ========== 应用错误码 ==========


    /**
     * 应用市场应用不存在
     */
    ErrorCode APP_NO_EXISTS_UID = new ErrorCode(300200002, "The app is not exists! [UID: {}]");

    /**
     * 应用字段必填错误
     */
    ErrorCode APP_FIELD_REQUIRED = new ErrorCode(300100000, "The app {} is required, please check and try again!");

    /**
     * 应用字段不支持错误
     */
    ErrorCode APP_FIELD_UNSUPPORTED = new ErrorCode(300100001, "The app {}({}) is unsupported, please check and try again!");

    /**
     * 应用名称重复
     */
    ErrorCode APP_NAME_DUPLICATE = new ErrorCode(300100003, "The app name: {} has been exist, please change the app name and try again.");

    /**
     * 应用不能为空
     */
    ErrorCode APP_DATA_IS_NULL = new ErrorCode(300100004, "The {} must can't be null");

    /**
     * 应用名称为必须的
     */
    ErrorCode APP_FIELD_IS_REQUIRED = new ErrorCode(300100005, "The App {} Is Required, Please Check And Try Again ! ");

    /**
     * 不支持的应用类型
     */
    ErrorCode APP_FIELD_NOT_SUPPORT = new ErrorCode(300100006, "The App {}: {} Is Not Supported");

    /**
     * 应用 UID 为必须的
     */
    ErrorCode APP_UID_IS_REQUIRED = new ErrorCode(300100007, "The App UID Is Required, Please Check And Try Again ! ");


    // ========== 应用市场错误码 3-002-000-000 ==========

    /**
     * 应用市场应用不存在
     */
    ErrorCode APP_MARKET_NO_EXISTS_UID_VERSION = new ErrorCode(300200001, "The App Is Not Exists In The Market! [UID: {}, version: {}]");

    /**
     * 应用市场操作不支持
     */
    ErrorCode APP_MARKET_OPERATE_NOT_SUPPORTED = new ErrorCode(300200003, "The [{}] operate is not supported");

    /**
     * 应用已经下载过
     */
    ErrorCode APP_HAS_BEEN_INSTALLED = new ErrorCode(300200006, "The app has been installed, Please check and try again ! ");

    /**
     * 应用市场操作失败
     */
    ErrorCode APP_MARKET_FAIL = new ErrorCode(300200004, "App market fail: {} ");

    ErrorCode USER_MAY_NOT_LOGIN = new ErrorCode(300200010, "User may not login");

    /**
     * 应用市场操作为必须的
     */
    ErrorCode APP_OPERATE_IS_REQUIRED = new ErrorCode(300200009, "The app operate is required, Please check and try again ! ");

    /**
     * 应用市场版本为必须的
     */
    ErrorCode APP_MARKET_VERSION_REQUIRED = new ErrorCode(300200005, "The App Version Is Required, Please Check And Try Again ! ");

    /**
     * 应用市场审核为必须的
     */
    ErrorCode APP_MARKET_AUDIT_IS_REQUIRED = new ErrorCode(300200007, "The App Audit Is Required, Please Check And Try Again ! ");

    /**
     * 应用市场审核不支持
     */
    ErrorCode APP_MARKET_AUDIT_IS_NOT_SUPPORT = new ErrorCode(300200008, "The app audit is not support, please check and try again! [audit: {}]");

}
