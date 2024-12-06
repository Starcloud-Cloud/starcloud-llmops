package com.starcloud.ops.business.app.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
@SuppressWarnings("all")
public interface ErrorCodeConstants {

    // ============ 节点处理器执行异常错误码 =========================

    ErrorCode EXECUTE_APP_FAILURE = new ErrorCode(300_100_500, "应用执行异常！请检查您的配置或请稍候重试！");

    /**
     * 步骤处理器顶级错误码。
     */
    ErrorCode EXECUTE_APP_ACTION_FAILURE = new ErrorCode(300_100_510, "【{}】步骤执行异常！请稍候重试！");

    /**
     * 步骤结果处理异常
     */
    ErrorCode EXECUTE_APP_ACTION_PARSE_RESULT_FAILURE = new ErrorCode(300_100_511, "【{}】步骤结果处理异常，请稍候重试！");

    /**
     * 节点处理器顶级错误码。
     */
    ErrorCode EXECUTE_STEP_HANDLER_FAILURE = new ErrorCode(300_100_530, "处理器执行异常！请稍候重试！");

    /**
     * 大模型执行异常
     */
    ErrorCode EXECUTE_LLM_FAILURE = new ErrorCode(300_100_550, "大模型执行异常！请稍候重试！");

    /**
     * 大模型执行异常
     */
    ErrorCode EXECUTE_POSTER_FAILURE = new ErrorCode(300_100_551, "海报执行异常！请稍候重试！");

    ErrorCode EXECUTE_POSTER_EXCEPTION = new ErrorCode(350400200, "海报执行异常！请稍候重试！");

    /**
     * 基础响应状态码：响应结果不存在。<br>
     * 比如：查询用户详情的时候，未查询到用户信息。即可以使用该状态码。
     */
    ErrorCode RESULT_NOT_EXIST = new ErrorCode(300_900_001, "结果不存在！");

    /**
     * 基础响应状态码：响应结果已存在。<br>
     * 比如：新增用户时候，用户已存在。即可以使用该状态码。
     */
    ErrorCode RESULT_ALREADY_EXIST = new ErrorCode(300_900_002, "结果已存在！");

    // 1.========== 通用错误 300 000 xxx ==========

    /**
     * 通用参数异常错误码
     */
    ErrorCode PARAMETER_EXCEPTION = new ErrorCode(300000400, "参数异常，请检查后重试！");

    /**
     * 用户未登录
     */
    ErrorCode USER_MAY_NOT_LOGIN = new ErrorCode(300000401, "用户未登录，请先登录您的账号！");

    /**
     * Prompt 是必须的
     */
    ErrorCode PROMPT_IS_REQUIRED = new ErrorCode(300000402, "Prompt是必须的，请检查后重试！");

    /**
     * 应用执行上下文是必须的
     */
    ErrorCode APP_CONTEXT_REQUIRED = new ErrorCode(300000402, "应用执行上下文是必须的，请检查后重试！");

    /**
     * MAX_TOKENS超出限制
     */
    ErrorCode MAX_TOKENS_OUT_OF_LIMIT = new ErrorCode(300000403, "最大返回Tokens超出限制（{}）需要在1～4000之间，请检查后重试！");

    /**
     * MAX_TOKENS格式错误
     */
    ErrorCode MAX_TOKENS_FORMAT_ERROR = new ErrorCode(300000404, "最大返回Tokens格式错误（{}）必须是正整数，请检查后重试！");

    /**
     * 温度值超出限制
     */
    ErrorCode TEMPERATURE_OUT_OF_LIMIT = new ErrorCode(300000405, "温度值超出限制（{}）温度值必须在0～2之间，请检查后重试！");

    /**
     * 温度值格式错误
     */
    ErrorCode TEMPERATURE_FORMAT_ERROR = new ErrorCode(300000406, "温度值格式错误（{}）温度值必须是数字且需要在0～2之间，请检查后重试！");

    /**
     * 应用配置错误
     */
    ErrorCode WORKFLOW_CONFIG_FAILURE = new ErrorCode(300000407, "应用配置错误，请检查后重试！");

    /**
     * 应用执行步骤不存在
     */
    ErrorCode WORKFLOW_STEP_NOT_EXIST = new ErrorCode(300000407, "应用执行步骤不存在，请稍后重试或者联系管理员！");

    /**
     * 不支持的素材类型
     */
    ErrorCode APP_MATERIAL_TYPE_NONSUPPORT = new ErrorCode(300000408, "不支持的素材类型，请检查后重试（{}）！");

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
    ErrorCode APP_CATEGORY_REQUIRED = new ErrorCode(300100150, "应用分类是必填项！");

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

    /**
     * 不支持的应用类型
     */
    ErrorCode APP_TYPE_NONSUPPORT = new ErrorCode(300100153, "不支持的应用类型，请检查后重试（{}）！");

    /**
     * 应用类型是必填的
     */
    ErrorCode APP_TYPE_REQUIRED = new ErrorCode(300100153, "应用类型是必填的！");

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

    /**
     * 请求参数不能为 null
     */
    ErrorCode MARKET_QUERY_REQUIRED = new ErrorCode(300200410, "请求参数是必填的！");

    /**
     * tagType 不支持
     */
    ErrorCode MARKET_TAG_TYPE_NOT_SUPPORTED = new ErrorCode(300200410, "参数tagType({})不支持！请检查后重试！");

    // ========== 应用收藏错误码 300 300 xxx ==========

    /**
     * 收藏UID是必须的
     */
    ErrorCode FAVORITE_UID_IS_REQUIRED = new ErrorCode(300300110, "收藏UID是必须的，请检查后重试！");

    /**
     * 应用收藏不存在
     */
    ErrorCode FAVORITE_APP_NON_EXISTENT = new ErrorCode(300300120, "应用收藏不存在，请稍后重试或者联系管理员（{}）！");

    /**
     * 该应用已经被收藏了
     */
    ErrorCode FAVORITE_APP_ALREADY_EXISTS = new ErrorCode(300300130, "该应用已经被收藏了，您可以在收藏列表中查看！");


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

    ErrorCode APP_PUBLISHED = new ErrorCode(300400141, "该应用已发布到应用市场，请删除应用市场后再进行操作！");
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

    /**
     * AI结果解析异常
     */
    ErrorCode EXECUTE_JSON_RESULT_PARSE_ERROR = new ErrorCode(310000321, "AI结果解析异常！请稍候重试！");

    // ========== 应用执行错误码 310 100 xxx ==========

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

    /**
     * 执行结果不存在
     */
    ErrorCode EXECUTE_APP_ANSWER_NOT_EXIST = new ErrorCode(31010511, "生成内容不存在！请稍后重试或者联系管理员（{}）！");

    /**
     * 执行结果不存在
     */
    ErrorCode EXECUTE_APP_GET_COST_FAILURE = new ErrorCode(31010512, "应用执行获取扣除魔力值数值异常！");

    /**
     * 海报执行步骤不存在或者当前步骤不支持海报生成
     */
    ErrorCode EXECUTE_APP_STEP_NON_EXISTENT_OR_NOT_SUPPORT = new ErrorCode(31010513, "海报执行步骤不存在或者当前步骤不支持海报生成（{}）！");

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
     * 图片应用执行失败，未找到对应的图片处理器！
     */
    ErrorCode EXECUTE_IMAGE_HANDLER_NOT_FOUND = new ErrorCode(310400001, "图片应用执行失败，未找到对应的图片处理器！");

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
     * 生成图片失败, feign 调用失败， 系统异常！
     */
    ErrorCode EXECUTE_IMAGE_FEIGN_500_FAILURE = new ErrorCode(310400450, "生成图片失败：系统异常！请稍后重试或联系管理员！");

    /**
     * 生成图片失败, feign 调用失败， 系统异常！
     */
    ErrorCode EXECUTE_IMAGE_FEIGN_TIME_OUT_FAILURE = new ErrorCode(310400460, "生成图片失败：连接超时！。请稍后重试或联系管理员！");

    /**
     * 创建图片实体失败
     */
    ErrorCode BUILD_IMAGE_ENTITY_FAILURE = new ErrorCode(310400500, "图片生成失败，不支持的appUid（{}）！");

    // ========== Listing 应用执行错误码 310 500 xxx ==========

    /**
     * Listing 生成失败
     */
    ErrorCode EXECUTE_LISTING_FAILURE = new ErrorCode(310500000, "Listing 生成失败，请稍后重试或联系管理员！");

    /**
     * Listing 生成失败，生成结果为空
     */
    ErrorCode EXECUTE_LISTING_RESULT_IS_NULL = new ErrorCode(310500001, "Listing 生成失败，生成结果为空，请稍候重试或者联系管理员！");

    /**
     * Listing 生成失败，生成结果异常
     */
    ErrorCode EXECUTE_LISTING_RESULT_FAILURE = new ErrorCode(310500002, "Listing 生成失败，生成结果异常，请稍候重试或者联系管理员！");

    /**
     * Listing 生成失败，系统配置错误
     */
    ErrorCode EXECUTE_LISTING_CONFIG_FAILURE = new ErrorCode(310500100, "Listing 生成失败，系统配置错误！请联系管理员！");

    /**
     * Listing 生成失败，系统配置错误
     */
    ErrorCode EXECUTE_LISTING_STEP_FAILURE = new ErrorCode(310500101, "Listing 生成失败，系统步骤错误！请联系管理员！");

    /**
     * Listing 生成失败，系统变量错误
     */
    ErrorCode EXECUTE_LISTING_VARIABLE_FAILURE = new ErrorCode(310500102, "Listing 生成失败，系统变量错误！请联系管理员！");

    /**
     * Listing 生成失败，系统变量错误
     */
    ErrorCode EXECUTE_LISTING_TYPE_FAILURE = new ErrorCode(310500103, "Listing 生成失败，Listing 类型不支持（{}）！请联系管理员！");

    // ========== 图片上传错误码 370 000 xxx ==========

    /**
     * 上传图片失败
     */
    ErrorCode UPLOAD_FAILURE = new ErrorCode(370000000, "{}");

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
     * 图片像素超过限制
     */
    ErrorCode IMAGE_PIXEL_LIMIT_FAILURE = new ErrorCode(370000170, "上传图片大小不能超过{}({}像素)，请重新上传！");

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


    ErrorCode CREATIVE_CONTENT_NOT_EXIST = new ErrorCode(300500001, "创作内容不存在, {}");

    ErrorCode CREATIVE_CONTENT_IS_EXECUTEING = new ErrorCode(300500002, "创作内容执行中稍后重试, {}");

    ErrorCode CREATIVE_CONTENT_GREATER_RETRY = new ErrorCode(300500003, "创作内容最多重试 {} 次");

    ErrorCode NO_CREATIVE_CONTENT_CAN_EXECUTE = new ErrorCode(300500004, "不存在可执行的创作任务");

    ErrorCode UNSUPPORTED_TYPE = new ErrorCode(300500005, "不支持的任务类型 {}");

    ErrorCode EXECTURE_ERROR = new ErrorCode(300500006, "{} 执行失败请稍后重试 {}");

    ErrorCode XHS_URL_ERROR = new ErrorCode(300500007, "小红书笔记地址不正确 {}");

    ErrorCode XHS_REMOTE_ERROR = new ErrorCode(300500008, "{}");

    ErrorCode CREATIVE_CONTENT_CLAIMED = new ErrorCode(300500009, "创作任务已绑定,不允许修改 {}");

    ErrorCode DUPLICATE_LABEL = new ErrorCode(300500010, "存在重复label: {}");


    // ========== 媒体评论 错误码 ==========
    ErrorCode MEDIA_COMMENTS_NOT_EXISTS = new ErrorCode(300700101, "媒体评论不存在");

    ErrorCode MEDIA_COMMENTS_ACTION_NOT_EXISTS = new ErrorCode(300700201, "{}不存在");

    ErrorCode MEDIA_STRATEGY_NOT_EXISTS = new ErrorCode(300700301, "回复策略不存在");

    ErrorCode MEDIA_STRATEGY_SAME_EXISTS = new ErrorCode(300700302, "存在相同的策略，请核对后重新提交");


    // ==========图片搜索 错误码 ==========
    ErrorCode PIXABAY_API_KEYS_LIMIT = new ErrorCode(300701201, "请求频率太快了，请等一分钟后再试");

    ErrorCode PIXABAY_API_KEYS_REQUEST_ERROR = new ErrorCode(300701202, "请求异常，请稍候再试");

    ErrorCode PIXABAY_API_KEYS_NETWORK_ERROR = new ErrorCode(300701203, "网络出小差了，请稍候再试");


    ErrorCode XHS_OCR_PARAM_REQUIRED = new ErrorCode(300701301, "缺少必填字段");

    ErrorCode IMAGE_OCR_ERROR = new ErrorCode(300701302, "ocr 异常：{}");

    // 敏感词检测
    ErrorCode RISK_WORD_ERROR = new ErrorCode(300701401, "敏感词检测错误：{}");


    // ==========素材库 错误码 ==========
    ErrorCode MATERIAL_LIBRARY_NOT_EXISTS = new ErrorCode(300702201, "素材库不存在，请刷新后再试");

    ErrorCode MATERIAL_LIBRARY_FORAMT_NO_MODIFY = new ErrorCode(300702202, "素材库类型创建后不支持修改");


    ErrorCode MATERIAL_LIBRARY_TABLE_COLUMN_NOT_EXISTS = new ErrorCode(300703203, "素材表列信息不存在");

    ErrorCode MATERIAL_LIBRARY_SLICE_NOT_EXISTS = new ErrorCode(300704204, "素材数据不存在");

    ErrorCode MATERIAL_LIBRARY_TABLE_COULMN_ADD_FAIL_NO_EXCEL = new ErrorCode(300704205, "当前素材库非表格类型，无法自定义字段");

    ErrorCode MATERIAL_LIBRARY_TABLE_COULMN_ADD_FAIL_SAME_COULMN = new ErrorCode(300704206, "设置素材库列名失败，存在重复的字段名称:{}");
    ErrorCode MATERIAL_LIBRARY_TABLE_COULMN_BATCH_ADD_FAIL = new ErrorCode(300704207, "批量设置素材列失败，类型转换异常");

    ErrorCode MATERIAL_LIBRARY_EXPORT_FAIL_ERROR_TYPE = new ErrorCode(300702208, "导入素材库模板失败，非表格类型的素材库不支持导出模板");

    ErrorCode MATERIAL_LIBRARY_EXPORT_FAIL_COULMN_EMPTY = new ErrorCode(300702209, "导入素材库模板失败，不存在自定义字段");
    ErrorCode MATERIAL_LIBRARY_IMPORT_FAIL_IMAGE_NO_SUPPRT = new ErrorCode(300702210, "暂不支持图片素材库导入");
    ErrorCode MATERIAL_LIBRARY_EXPORT_FAIL_EXCEL_NO_SUPPRT = new ErrorCode(300702211, "暂不支持Excel素材库导入");

    ErrorCode MATERIAL_LIBRARY_ID_EMPTY = new ErrorCode(300702212, "素材库编号为空，请核对后再试");

    ErrorCode MATERIAL_LIBRARY_TABLE_COULMN_ERROR = new ErrorCode(300702213, "表头与当前表结构不一致,列名称及顺序需保持一致，表头需要与现有表格结构保持一致。");

    ErrorCode MATERIAL_LIBRARY_NO_BIND_APP = new ErrorCode(300702214, "当前应用未绑定素材库");

    ErrorCode MATERIAL_LIBRARY_APP_BIND_NOT_EXISTS = new ErrorCode(300702215, "素材与应用不存在绑定关系");

    ErrorCode MATERIAL_LIBRARY_APPUID_EMPTY = new ErrorCode(300702216, "应用编号为空，查询素材数据失败");

    ErrorCode MATERIAL_LIBRARY_SLICE_DATA_MISSING = new ErrorCode(300702217, "部分数据缺失，请核对后再试");

    ErrorCode MATERIAL_LIBRARY_DATA_UPLOAD_OVERTIME = new ErrorCode(300702218, "数据上传超时，请重试");
    ErrorCode MATERIAL_LIBRARY_SLICE_LIBRARY_ID_MISSING = new ErrorCode(300702219, "素材库编号为空，操作失败");
    ErrorCode MATERIAL_LIBRARY_DATA_UPLOAD_BIG = new ErrorCode(300702219, "导入数据量过大，最多支持{}条数据，请分批导入");
    ErrorCode MATERIAL_LIBRARY_IMPORT_LIBRARY_EMPTY = new ErrorCode(300702220, "数据导入失败，素材库编号不存在");
    ErrorCode MATERIAL_LIBRARY_IMPORT_DATA_EMPTY = new ErrorCode(300702221, "数据导入失败，数据为空数据");


}
