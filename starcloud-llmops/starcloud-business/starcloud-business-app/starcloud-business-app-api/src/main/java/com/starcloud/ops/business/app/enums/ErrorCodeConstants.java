package com.starcloud.ops.business.app.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
public interface ErrorCodeConstants {

    // 1.========== 通用错误 300 000 xxx ==========

    /**
     * 用户未登录
     */
    ErrorCode USER_MAY_NOT_LOGIN = new ErrorCode(300000401, "用户未登录，请先登录您的账号！");


    // ========== 基本增删改查错误码 ==========

    // 1.========== 应用错误码 300 100 xxx ==========

    /**
     * 应用市场应用不存在
     */
    ErrorCode APP_NON_EXISTENT = new ErrorCode(300100110, "应用不存在，请稍后重试或者联系管理员（{}）！");

    /**
     * 应用名称重复
     */
    ErrorCode APP_NAME_DUPLICATE = new ErrorCode(300100120, "应用名称重复，请检查后重试（{}）！");

    /**
     * 应用 UID 为必须的
     */
    ErrorCode APP_UID_REQUIRED = new ErrorCode(300100130, "应用UID是必须的，请检查后重试！");

    /**
     * 应用步骤名称重复
     */
    ErrorCode APP_STEP_NAME_DUPLICATE = new ErrorCode(300100140, "应用步骤名称重复，请检查后重试（{}）！");

    /**
     * 不支持的应用分类
     */
    ErrorCode APP_CATEGORY_NONSUPPORT = new ErrorCode(300100150, "不支持的应用分类，请检查后重试（{}）！");

    /**
     * 不支持的选择一级分类
     */
    ErrorCode APP_CATEGORY_NONSUPPORT_FIRST = new ErrorCode(300100151, "不支持的选择一级分类，请检查后重试（{}）！");

    /**
     * 应用类目不存在
     */
    ErrorCode APP_CATEGORY_NON_EXISTENT = new ErrorCode(300100152, "应用类目不存在，请检查后重试（{}）！");

    // ========== 应用市场错误码 300 200 xxx ==========

    /**
     * 应用市场应用不存在
     */
    ErrorCode MARKET_APP_NON_EXISTENT = new ErrorCode(300200110, "应用市场应用不存在，请稍后重试或者联系管理员（{}）！");

    /**
     * 获取应用市场应用UID失败
     */
    ErrorCode MARKET_GET_UID_FAILURE = new ErrorCode(300200120, "获取应用市场应用UID失败，请稍后重试或者联系管理员（{}）！");

    /**
     * 获取应用市场应用版本失败
     */
    ErrorCode MARKET_GET_VERSION_FAILURE = new ErrorCode(300200130, "获取应用市场应用版本失败，请稍后重试或者联系管理员（{}）！");

    /**
     * 应用市场UID为必须的
     */
    ErrorCode MARKET_UID_REQUIRED = new ErrorCode(300200140, "应用市场UID是必须的，请检查后重试！");

    /**
     * 应用已经下载过
     */
    ErrorCode MARKET_APP_INSTALLED = new ErrorCode(300200310, "应用已成功下载过，无法重复下载！");

    /**
     * 应用市场操作不支持
     */
    ErrorCode MARKET_OPERATE_NOT_SUPPORTED = new ErrorCode(300200410, "应用市场不支持该操作，请稍后重试或者联系管理员（{}）！");

    // ========== 应用收藏错误码 300 300 xxx ==========

    /**
     * 应用收藏不存在
     */
    ErrorCode FAVORITE_APP_NON_EXISTENT = new ErrorCode(300300110, "应用收藏不存在，请稍后重试或者联系管理员（{}）！");

    /**
     * 该应用已经被收藏了
     */
    ErrorCode FAVORITE_APP_BEAN = new ErrorCode(300300120, "该应用已经被收藏了，您可以在收藏列表中查看！");

    // ========== 应用发布错误码 300 400 xxx ==========

    /**
     * 应用发布记录不存在
     */
    ErrorCode PUBLISH_APP_NON_EXISTENT = new ErrorCode(300400110, "应用发布记录不存在，请稍后重试或者联系管理员（{}）!");

    /**
     * 审核状态不支持
     */
    ErrorCode PUBLISH_AUDIT_NOT_SUPPORTED = new ErrorCode(300400120, "发布审核状态不支持，请检查后重试（{}）!");

    /**
     * 应用发布记录不存在
     */
    ErrorCode PUBLISH_APP_INFO_NON_EXISTENT = new ErrorCode(300400130, "发布应用配置信息不存在，请稍后重试或者联系管理员！");

    /**
     * 重复发布
     */
    ErrorCode PUBLISH_APP_REPEAT = new ErrorCode(300400140, "应用已经发布过了，请检查后重试（{}）!");

    // ========== 应用发布渠道错误码 300 500 xxx ==========

    /**
     * 应用渠道不存在，无法更新
     */
    ErrorCode CHANNEL_NON_EXISTENT = new ErrorCode(300500110, "应用发布渠道不存在，请稍后重试或者联系管理员（{}）！");

    /**
     * 应用发布渠道 Uid 是必须的
     */
    ErrorCode CHANNEL_UID_REQUIRED = new ErrorCode(300500120, "应用发布渠道UID是必须的，请检查后重试！");

    /**
     * 应用发布渠道类型是必须的
     */
    ErrorCode CHANNEL_TYPE_REQUIRED = new ErrorCode(300500130, "应用发布渠道类型是必须的，请检查后重试！");

    /**
     * 不支持的应用发布渠道类型
     */
    ErrorCode CHANNEL_TYPE_NOT_SUPPORTED = new ErrorCode(300500140, "不支持的应用发布渠道类型，请检查后重试（{}）！");

    /**
     * 应用发布渠道配置 API Key 是必须的
     */
    ErrorCode CHANNEL_CONFIG_API_KEY_REQUIRED = new ErrorCode(300500510, "应用发布渠道配置【apiKey】是必须的，请检查后重试！");

    /**
     * 应用发布渠道配置 Share Link 是必须的
     */
    ErrorCode CHANNEL_CONFIG_SLUG_REQUIRED = new ErrorCode(300500520, "应用发布渠道配置【slug】是必须的，请检查后重试！");

    /**
     * 应用发布渠道配置 UID 是必须的
     */
    ErrorCode CHANNEL_MEDIUM_UID_REQUIRED = new ErrorCode(300500530, "应用发布渠道媒介UID是必须的，请检查后重试！");

    /**
     * 应用发布渠道媒介UID必须与Slug一致，请检查后重试！
     */
    ErrorCode CHANNEL_MEDIUM_UID_NE_SLUG = new ErrorCode(300500540, "应用发布渠道媒介UID必须与Slug一致，请检查后重试！");

    // ========== 应用发布限流错误码 300 600 xxx ==========

    /**
     * 应用发布限流不存在
     */
    ErrorCode LIMIT_NON_EXISTENT = new ErrorCode(300600110, "应用发布限流不存在，请稍后重试或者联系管理员（{}）！");

    /**
     * 应用发布限制已存在
     */
    ErrorCode LIMIT_ALREADY_EXISTENT = new ErrorCode(300600120, "应用发布限制已存在！");


    // 应用执行错误码


    /**
     * 总执行异常，兜底异常
     */
    ErrorCode EXECUTE_BASE_FAILURE = new ErrorCode(310000000, "执行异常，请稍后重试或者联系管理员（{}）！");

    /**
     * 执行场景不存在
     */
    ErrorCode EXECUTE_APP_UID_REQUIRED = new ErrorCode(310000110, "执行【appUid】是必须的，请检查后重试！");

    /**
     * 执行场景不存在
     */
    ErrorCode EXECUTE_MEDIUM_UID_REQUIRED = new ErrorCode(310000120, "执行【mediumUid】是必须的，请检查后重试！");

    /**
     * 执行【mediumUid】和【appUid】不能同时为空
     */
    ErrorCode EXECUTE_APP_OR_MEDIUM_UID_NOT_NULL_AT_THE_SAME_TIME = new ErrorCode(310000130, "执行【mediumUid】和【appUid】不能同时为空，请检查后重试！");

    /**
     * 执行场景不存在
     */
    ErrorCode EXECUTE_SCENE_REQUIRED = new ErrorCode(310000310, "执行场景是必须的，请检查后重试！");

    /**
     * 不支持的执行场景
     */
    ErrorCode EXECUTE_SCENE_UNSUPPORTED = new ErrorCode(310000320, "不支持的执行场景，请检查后重试！");


    // ========== 应用执行错误码 310 100 xxx ==========

    /**
     * 应用执行通用错误码
     */
    ErrorCode EXECUTE_APP_FAILURE = new ErrorCode(310100000, "应用执行失败，请稍后重试或者联系管理员（{}）！");

    /**
     * 应用执行应用信息不能为空
     */
    ErrorCode APP_EXECUTE_APP_IS_NULL = new ErrorCode(310100310, "应用执行应用信息不能为空！");

    /**
     * 应用执行配置信息不能为空
     */
    ErrorCode EXECUTE_APP_CONFIG_REQUIRED = new ErrorCode(310100320, "应用执行配置信息不能为空！");

    /**
     * 应用执行步骤
     */
    ErrorCode EXECUTE_APP_STEP_ID_REQUIRED = new ErrorCode(310100330, "应用执行步骤ID【stepId】是必须的！");

    /**
     * 应用执行步骤不能为空
     */
    ErrorCode EXECUTE_APP_STEPS_REQUIRED = new ErrorCode(300600013, "应用执行步骤配置是必须的！");

    /**
     * 应用执行步骤不存在
     */
    ErrorCode EXECUTE_APP_STEP_NON_EXISTENT = new ErrorCode(300600014, "应用执行步骤不存在（{}）！");

    /**
     * 执行结果不存在
     */
    ErrorCode EXECUTE_APP_RESULT_NON_EXISTENT = new ErrorCode(31010510, "生成信息不存在，请稍后重试或者联系管理员（{}）！");


    // ========== 应用市场执行错误码 310 200 xxx ==========

    /**
     * 应用市场执行通用错误码
     */
    ErrorCode EXECUTE_MARKET_FAILURE = new ErrorCode(310200000, "市场应用执行失败，请稍后重试或者联系管理员（{}）！");

    // ========== 聊天应用执行错误码 310 300 xxx ==========

    /**
     * 应用市场执行通用错误码
     */
    ErrorCode EXECUTE_CHAT_FAILURE = new ErrorCode(310300000, "聊天应用执行失败，请稍后重试或者联系管理员（{}）！");

    // ========== 图片应用执行错误码 310 400 xxx ==========

    /**
     * 图片应用执行错误码
     */
    ErrorCode EXECUTE_IMAGE_FAILURE = new ErrorCode(310400000, "图片应用执行失败，请稍后重试或者联系管理员（{}）！");

    /**
     * 生成图片失败，请重试或者联系管理员
     */
    ErrorCode EXECUTE_IMAGE_REQUEST_FAILURE = new ErrorCode(310400100, "生成图片参数错误：{}");

    /**
     * 生成图片为空
     */
    ErrorCode GENERATE_IMAGE_EMPTY = new ErrorCode(310400200, "图片生成失败，生成结果为空！");

    /**
     * 生成图片失败, feign 调用失败
     */
    ErrorCode EXECUTE_IMAGE_FEIGN_FAILURE = new ErrorCode(310400400, "生成图片失败：请求参数错误！");

    /**
     * 生成图片失败, feign 调用失败， 无 Api Key，无法进行访问
     */
    ErrorCode EXECUTE_IMAGE_FEIGN_401_FAILURE = new ErrorCode(310400401, "生成图片失败：缺少api密钥，无法进行访问。请联系管理员！");

    /**
     * 生成图片失败, feign 调用失败， 余额不足！请购买点数！
     */
    ErrorCode EXECUTE_IMAGE_FEIGN_402_FAILURE = new ErrorCode(310400402, "生成图片失败：您的帐户没有剩余积分，您可以在您的帐户页面购买更多积分！请联系管理员！");

    /**
     * 生成图片失败, feign 调用失败， 无效或已撤销的api密钥！
     */
    ErrorCode EXECUTE_IMAGE_FEIGN_403_FAILURE = new ErrorCode(310400403, "生成图片失败：无效或已撤销的api密钥。请联系管理员！");

    /**
     * 生成图片失败, feign 调用失败， 当前请求过多！
     */
    ErrorCode EXECUTE_IMAGE_FEIGN_429_FAILURE = new ErrorCode(310400429, "生成图片失败：当前请求过多！。请稍后重试或联系管理员！");

    /**
     * 生成图片失败, feign 调用失败， 余额不足！请购买点数！
     */
    ErrorCode EXECUTE_IMAGE_FEIGN_500_FAILURE = new ErrorCode(310400450, "生成图片失败：系统异常！。请稍后重试或联系管理员！");

    /**
     * 创建图片实体失败
     */
    ErrorCode BUILD_IMAGE_ENTITY_FAILURE = new ErrorCode(310400500, "图片生成失败，不支持的appUid（{}）！");

    // ========== 图片上传错误码 370 000 xxx ==========

    /**
     * 上传图片失败
     */
    ErrorCode UPLOAD_IMAGE_FAILURE = new ErrorCode(370000000, "上传图片失败，请稍后重试或者联系管理员！");

    /**
     * 上传图片名称不能为空
     */
    ErrorCode UPLOAD_IMAGE_NAME_REQUIRED = new ErrorCode(370000110, "上传图片名称不能为空！");

    /**
     * 不支持的图片类型
     */
    ErrorCode UNSUPPORTED_IMAGE_TYPES = new ErrorCode(370000120, "不支持的图片类型（{}）！支持的图片类型：{}");

    /**
     * 后缀名不存在
     */
    ErrorCode FILE_EXTENSION_FAILURE = new ErrorCode(370000130, "不支持的文件类型！");

    /**
     * 上传图片路径不存在
     */
    ErrorCode UPLOAD_IMAGE_PATH_NON_EXISTENT = new ErrorCode(370000140, "图片上传路径不存在，请稍后重试或者联系管理员！");

    /**
     * 上传图片IO异常
     */
    ErrorCode UPLOAD_IMAGE_IO_FAILURE = new ErrorCode(370000150, "图片读取失败，请稍后重试或者联系管理员！");

    /**
     * 图片信息获取失败
     */
    ErrorCode IMAGE_INFO_FAILURE = new ErrorCode(370000160, "图片信息获取失败，请稍后重试或者联系管理员！");

    /**
     * 获取文件信息失败
     */
    ErrorCode GET_IMAGE_FAILURE = new ErrorCode(370100000, "图片获取失败：{}");

    /**
     * 文件类型不是图片
     */
    ErrorCode FILE_TYPE_NOT_IMAGES = new ErrorCode(300500009, "The file type is not a picture");

    /**
     * 聊天应用自动编排异常
     */
    ErrorCode CHAT_ROLE_GENERATE_ERROR = new ErrorCode(300200023, "please provide a new target audience and provide a detailed description of the problem to be address");

    /**
     * 语音转文字异常
     */
    ErrorCode AUDIO_TRANSCRIPT_ERROR = new ErrorCode(300200024, "audio transcript error, {}");

    /**
     * 大模型异常
     */
    ErrorCode OPENAI_ERROR = new ErrorCode(300600015, "大模型异常!");

    /**
     * chat配置为null
     */
    ErrorCode CONFIG_ERROR = new ErrorCode(300400007, "{}不能为空!");

    /**
     * 配置类型不支持
     */
    ErrorCode CHAT_CONFIG_TYPE_ERROR = new ErrorCode(300400008, "不支持此配置类型:{}");

    /**
     * chat配置不存在
     */
    ErrorCode CHAT_CONFIG_NOT_EXIST = new ErrorCode(300400009, "此id对应的配置不存在:{}");

    /**
     * 修改配置的类型错误
     */
    ErrorCode MODIFY_CONFIG_ERROR = new ErrorCode(300400010, "{} 实际的配置类型是 {}");

    ErrorCode CHAT_CONFIG_IS_REPEAT = new ErrorCode(300400011, "{}已经存在 , {}");


}
