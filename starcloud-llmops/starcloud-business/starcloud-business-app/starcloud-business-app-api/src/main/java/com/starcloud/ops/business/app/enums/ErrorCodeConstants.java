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
    ErrorCode CHAT_ROLE_GENERATE_ERROR = new ErrorCode(300200023, "please provide a new target audience and provide a detailed description of the problem to be address");

    /**
     * 语音转文字异常
     */
    ErrorCode AUDIO_TRANSCRIPT_ERROR = new ErrorCode(300200024, "audio transcript error, {}");

    /**
     * 应用执行异常
     */
    ErrorCode APP_EXECUTE_FAIL = new ErrorCode(300300000, "{}");

    // ========== 图片生成服务错误 3-003-000-00 ==========
    /**
     * 生成图片失败，请重试或者联系管理员
     */
    ErrorCode GENERATE_IMAGE_FAIL = new ErrorCode(300300000, "Generate image fail, please try again or contact the administrator !");

    /**
     * 图片 Prompt 为必须的
     */
    ErrorCode IMAGE_PROMPT_REQUIRED = new ErrorCode(300300010, "The image prompt is required, please check and try again ! ");

    /**
     * 应用发布记录不存在
     */
    ErrorCode APP_PUBLISH_RECORD_NO_EXISTS_UID = new ErrorCode(300400000, "The app publish record is not exists! [UID: {}]");

    /**
     * 审核状态不支持
     */
    ErrorCode APP_PUBLISH_AUDIT_NOT_SUPPORTED = new ErrorCode(300400002, "The app publish audit is not supported! [audit: {}]");

    /**
     * 应用发布记录不存在
     */
    ErrorCode APP_PUBLISH_APP_INFO_NOT_FOUND = new ErrorCode(300400004, "The app publish info is not exists!");

    /**
     * 重复发布
     */
    ErrorCode APP_PUBLISH_DUPLICATE = new ErrorCode(300400006, "The app has been published, please cancel the publish and try again !");

    /**
     * 应用发布渠道 Uid 是必须的
     */
    ErrorCode APP_CHANNEL_UID_IS_REQUIRED = new ErrorCode(300500001, "The app publish channel uid is required, please check and try again ! ");

    /**
     * 应用渠道不存在，无法更新
     */
    ErrorCode APP_CHANNEL_NOT_EXIST = new ErrorCode(300500002, "Cant get the app publish channel, please check it is enabled!,  uid or mediumUid: {} ");

    /**
     * 应用发布渠道类型是必须的
     */
    ErrorCode APP_PUBLISH_CHANNEL_TYPE_NOT_NULL = new ErrorCode(300500003, "The app publish channel type is not be null");

    /**
     * 不支持的应用发布渠道类型
     */
    ErrorCode APP_PUBLISH_CHANNEL_TYPE_NOT_SUPPORTED = new ErrorCode(300500004, "The app publish channel type is not supported, type: {}");

    /**
     * 应用发布渠道配置 API Key 是必须的
     */
    ErrorCode APP_PUBLISH_CHANNEL_CONFIG_API_KEY_IS_REQUIRED = new ErrorCode(300500005, "The app publish channel config api key is required, please check and try again ! ");

    /**
     * 应用发布渠道配置 Share Link 是必须的
     */
    ErrorCode APP_PUBLISH_CHANNEL_CONFIG_SHARE_LINK_IS_REQUIRED = new ErrorCode(300500006, "The app publish channel config share link is required, please check and try again ! ");

    /**
     * 应用发布渠道配置 JS Iframe Slug 是必须的
     */
    ErrorCode APP_PUBLISH_CHANNEL_CONFIG_JS_IFRAME_SLUG_IS_REQUIRED = new ErrorCode(300500007, "The app publish channel config js iframe slug is required, please check and try again ! ");

    /**
     * 系统错误
     */
    ErrorCode PUBLISH_CHANNEL_UNKNOWN_ERROR = new ErrorCode(300500007, "Publish channel unknown error, please check and try again ! ");

    /**
     * 应用发布限流不存在
     */
    ErrorCode APP_PUBLISH_LIMIT_NOT_EXISTS_UID = new ErrorCode(300500008, "The app publish limit is not exists! [UID: {}]");

    /**
     * 不支持的应用发布限流操作
     */
    ErrorCode APP_PUBLISH_LIMIT_OPERATE_NOT_SUPPORTED = new ErrorCode(300600016, "The app publish limit operate is not supported! [operate: {}]");

    /**
     * 文件类型不是图片
     */
    ErrorCode FILE_TYPE_NOT_IMAGES = new ErrorCode(300500009, "The file type is not a picture");

    /**
     * 应用发布渠道配置 UID 是必须的
     */
    ErrorCode APP_PUBLISH_CHANNEL_CONFIG_UID_IS_REQUIRED = new ErrorCode(300500010, "The app publish channel config uid is required, please check and try again ! ");

    /**
     * 应用发布记录不存在
     */
    ErrorCode APP_PUBLISH_NOT_EXISTS_UID = new ErrorCode(300500011, "The app publish is not exists! [UID: {}]");

    /**
     * 应用执行步骤
     */
    ErrorCode APP_EXECUTE_STEP_ID_IS_REQUIRED = new ErrorCode(300600010, "应用执行步骤ID(stepId)不能为空!");

    /**
     * 应用执行配置信息不能为空
     */
    ErrorCode APP_EXECUTE_APP_CONFIG_IS_NULL = new ErrorCode(300600011, "应用执行配置信息不能为空！");

    /**
     * 应用执行应用信息不能为空
     */
    ErrorCode APP_EXECUTE_APP_IS_NULL = new ErrorCode(300600012, "应用执行应用信息不能为空！");

    /**
     * 应用执行步骤不能为空
     */
    ErrorCode APP_EXECUTE_STEPS_CANT_BE_EMPTY = new ErrorCode(300600013, "应用执行步骤不能为空！");

    /**
     * 应用执行步骤不存在
     */
    ErrorCode APP_EXECUTE_STEPS_NOT_FOUND = new ErrorCode(300600014, "应用执行步骤[{}]不存在！");

    ErrorCode OPENAI_ERROR = new ErrorCode(300600015, "大模型异常!");
}
