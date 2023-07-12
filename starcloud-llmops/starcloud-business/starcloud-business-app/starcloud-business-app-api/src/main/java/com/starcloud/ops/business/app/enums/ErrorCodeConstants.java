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
     * 应用名称重复
     */
    ErrorCode APP_NAME_DUPLICATE = new ErrorCode(300100004, "The app name: {} has been exist, please change the app name and try again.");

    /**
     * 应用 UID 为必须的
     */
    ErrorCode APP_UID_IS_REQUIRED = new ErrorCode(300100006, "The App UID Is Required, Please Check And Try Again ! ");


    // ========== 应用市场错误码 3-002-000-000 ==========

    /**
     * 应用市场应用不存在
     */
    ErrorCode APP_MARKET_NO_EXISTS_UID = new ErrorCode(300200000, "The app is not exists in the market ! [UID: {}]");

    /**
     * 应用市场应用不存在
     */
    ErrorCode APP_MARKET_NO_EXISTS_UID_VERSION = new ErrorCode(300200002, "The app is not exists in the market ! [UID: {}, version: {}]");

    /**
     * 应用市场应用不能安装
     */
    ErrorCode APP_MARKET_CANNOT_INSTALL = new ErrorCode(300200003, "The app is not exists in the market or the audit fails ! [UID: {}, version: {}]");

    /**
     * 应用市场操作不支持
     */
    ErrorCode APP_MARKET_OPERATE_NOT_SUPPORTED = new ErrorCode(300200004, "The [{}] operate is not supported");

    /**
     * 应用已经下载过
     */
    ErrorCode APP_HAS_BEEN_INSTALLED = new ErrorCode(300200006, "The app has been installed, Please check and try again ! ");

    /**
     * 应用市场操作失败
     */
    ErrorCode APP_MARKET_FAIL = new ErrorCode(300200008, "App market fail: {} ");

    /**
     * 用户未登录
     */
    ErrorCode USER_MAY_NOT_LOGIN = new ErrorCode(300200010, "User may not login");

    /**
     * 应用市场操作为必须的
     */
    ErrorCode APP_OPERATE_IS_REQUIRED = new ErrorCode(300200012, "The app operate is required, Please check and try again ! ");

    /**
     * 应用市场版本为必须的
     */
    ErrorCode APP_MARKET_UID_REQUIRED = new ErrorCode(300200014, "The app uid is required, please check and try again ! ");

    /**
     * 应用市场版本为必须的
     */
    ErrorCode APP_MARKET_VERSION_REQUIRED = new ErrorCode(300200014, "The app version is required, please check and try again ! ");

    /**
     * 应用市场审核为必须的
     */
    ErrorCode APP_MARKET_AUDIT_IS_REQUIRED = new ErrorCode(300200016, "The app audit is required, please check and try again ! ");

    /**
     * 应用市场审核不支持
     */
    ErrorCode APP_MARKET_AUDIT_IS_NOT_SUPPORT = new ErrorCode(300200018, "The app audit is not support, please check and try again! [audit: {}]");

    /**
     * 该应用已经被收藏了
     */
    ErrorCode APP_HAS_FAVORITE = new ErrorCode(300200020, "The app has been favorite, you can find it in the favorite list! [uid: {}]");

    /**
     * 应用收藏不存在
     */
    ErrorCode APP_FAVORITE_NOT_EXISTS = new ErrorCode(300200022, "The app favorite not exists ! [uid: {}]");

    /**
     * 聊天应用自动编排异常
     */
    ErrorCode CHAT_ROLE_GENERATE_ERROR = new ErrorCode(300200023,"please provide a new target audience and provide a detailed description of the problem to be address");

    /**
     * 语音转文字异常
     */
    ErrorCode AUDIO_TRANSCRIPT_ERROR = new ErrorCode(300200024,"audio transcript error, {}");

}
