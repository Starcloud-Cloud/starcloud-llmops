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
     * 应用 UID 为必须的
     */
    ErrorCode APP_UID_REQUIRED = new ErrorCode(300100000, "The app uid is required, please check and try again!");

    /**
     * 应用名称为必须的
     */
    ErrorCode APP_NAME_REQUIRED = new ErrorCode(300100001, "The app name is required, please check and try again!");

    /**
     * 应用模型为必须的
     */
    ErrorCode APP_MODEL_REQUIRED = new ErrorCode(300100001, "The app model is required, please check and try again!");

    /**
     * 应用模型不支持
     */
    ErrorCode APP_MODEL_UNSUPPORTED = new ErrorCode(300100001, "The app model({}) is unsupported, please check and try again!");

    /**
     * 应用类型为必须的
     */
    ErrorCode APP_TYPE_REQUIRED = new ErrorCode(300100001, "The app type is required, please check and try again!");

    /**
     * 应用类型不支持
     */
    ErrorCode APP_TYPE_UNSUPPORTED = new ErrorCode(300100001, "The app type({}) is unsupported, please check and try again!");

    /**
     * 应用来源为必须的
     */
    ErrorCode APP_SOURCE_REQUIRED = new ErrorCode(300100001, "The app source is required, please check and try again!");

    /**
     * 应用来源不支持
     */
    ErrorCode APP_SOURCE_UNSUPPORTED = new ErrorCode(300100001, "The app source({}) is unsupported, please check and try again!");

    /**
     * 应用分类为必须的
     */
    ErrorCode APP_CATEGORY_REQUIRED = new ErrorCode(300100001, "The app category is required, please check and try again!");

    /**
     * 生成式应用配置为必须
     */
    ErrorCode APP_CONFIG_REQUIRED = new ErrorCode(300100001, "The app config is required for the app COMPLETION model!");

    /**
     * 聊天式应用配置为必须
     */
    ErrorCode APP_CHAT_CONFIG_REQUIRED = new ErrorCode(300100001, "The app chat config is required for the app CHAT model!");

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
     * 该应用在应用市场不存在
     */
    ErrorCode APP_MARKET_NOT_EXISTS = new ErrorCode(300200000, "The App Is Not Exists In The Market! [ID: {}]");

    /**
     * 应用市场应用不存在
     */
    ErrorCode APP_MARKET_NO_EXISTS_UID = new ErrorCode(300200001, "The App Is Not Exists In The Market! [UID: {}]");

    /**
     * 应用市场应用不存在
     */
    ErrorCode APP_MARKET_NO_EXISTS_UID_VERSION = new ErrorCode(300200001, "The App Is Not Exists In The Market! [UID: {}, version: {}]");

    /**
     * 应用市场数据不能为空
     */
    ErrorCode APP_MARKET_DATA_IS_NULL = new ErrorCode(300200002, "The {} must can't be null");

    /**
     * 应用市场操作不支持
     */
    ErrorCode APP_MARKET_OPERATE_NOT_SUPPORTED = new ErrorCode(300200003, "The [{}] operate is not supported");

    /**
     * 应用市场操作失败
     */
    ErrorCode APP_MARKET_FAIL = new ErrorCode(300200004, "App Market Fail: {} ");

    /**
     * 应用市场版本为必须的
     */
    ErrorCode APP_MARKET_VERSION_REQUIRED = new ErrorCode(300200005, "The App Version Is Required, Please Check And Try Again ! ");

    /**
     * 应用市场模型未知
     */
    ErrorCode APP_MODEL_IS_UNKNOWN = new ErrorCode(300200006, "The App Model Is Unknown, Please Check And Try Again ! [model: {}]");

    /**
     * 应用市场审核为必须的
     */
    ErrorCode APP_MARKET_AUDIT_IS_REQUIRED = new ErrorCode(300200007, "The App Audit Is Required, Please Check And Try Again ! ");

    /**
     * 应用市场审核不支持
     */
    ErrorCode APP_MARKET_AUDIT_IS_NOT_SUPPORT = new ErrorCode(300200008, "The App Audit Is Not Support, Please Check And Try Again ! [audit: {}]");

}
