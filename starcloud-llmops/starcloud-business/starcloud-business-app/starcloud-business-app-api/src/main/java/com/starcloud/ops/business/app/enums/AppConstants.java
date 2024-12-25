package com.starcloud.ops.business.app.enums;

/**
 * App 常量类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
public interface AppConstants {

    /**
     * 默认版本
     */
    Integer DEFAULT_VERSION = 1;

    /**
     * 默认版本
     */
    Integer STEP_VERSION_V2 = 2;

    /**
     * 魔法AI 租户ID
     */
    Long MOFAAI_TENANT_ID = 2L;

    /**
     * 矩阵租户ID
     */
    Long JUZHEN_TENANT_ID = 3L;

    /**
     * Prompt 变量表示
     */
    String PROMPT = "PROMPT";

    /**
     * Model 变量表示
     */
    String MODEL = "MODEL";

    /**
     * MAX_TOKENS 变量表示
     */
    String MAX_TOKENS = "MAX_TOKENS";

    /**
     * TEMPERATURE 变量表示
     */
    String TEMPERATURE = "TEMPERATURE";

    /**
     * 默认模式类型标识
     */
    String DEFAULT_MODEL_TYPE = "DEFAULT_MODEL_TYPE";

    /**
     * 默认生成模式类型标识
     */
    String DEFAULT_COMPLETION_MODEL_TYPE = "DEFAULT_COMPLETION_MODEL_TYPE";

    /**
     * 默认聊天模式类型标识
     */
    String DEFAULT_CHAT_MODEL_TYPE = "DEFAULT_CHAT_MODEL_TYPE";

    /**
     * 默认媒体矩阵模式类型标识
     */
    String DEFAULT_MEDIA_MATRIX_MODEL_TYPE = "DEFAULT_MEDIA_MATRIX_MODEL_TYPE";

    /**
     * 默认大模型映射关系
     */
    String DEFAULT_LLM_MODEL_TYPE_MAP = "DEFAULT_LLM_MODEL_TYPE_MAP";

    /**
     * 应用分类字典类型
     */
    String APP_CATEGORY_DICT_TYPE = "STAR_CLOUD_LLM_APP_CATEGORY_TYPE";

    /**
     * 矩阵应用分类字典类型
     */
    String APP_CATEGORY_DICT_TYPE_JU_ZHEN = "STAR_CLOUD_LLM_APP_CATEGORY_TYPE_JU_ZHEN";

    /**
     * 热门应用分类字典类型
     */
    String APP_HOT_SEARCH_MARKET = "STAR_CLOUD_LLM_APP_HOT_SEARCH_MARKET";

    /**
     * 热门应用分类字典类型
     */
    String APP_HOT_SEARCH_MARKET_JU_ZHEN = "STAR_CLOUD_LLM_APP_HOT_SEARCH_MARKET_JU_ZHEN";

    /**
     * 热门应用分类字典类型
     */
    String APP_TEMPLATE_SEARCH_MARKET = "STAR_CLOUD_LLM_APP_TEMPLATE_SEARCH_MARKET";

    /**
     * 热门应用分类字典类型
     */
    String APP_TEMPLATE_SEARCH_MARKET_JU_ZHEN = "STAR_CLOUD_LLM_APP_TEMPLATE_SEARCH_MARKET_JU_ZHEN";

    /**
     * 图片生成 Prompt 案例
     */
    String IMAGE_EXAMPLE_PROMPT = "STAR_CLOUD_LLM_IMAGE_EXAMPLE_PROMPT";

    /**
     * 应用限流兜底配置
     */
    String APP_LIMIT_DEFAULT_CONFIG = "STAR_CLOUD_LLM_APP_LIMIT_DEFAULT_CONFIG";

    /**
     * 应用限流总开关
     */
    String APP_LIMIT_SWITCH = "STAR_CLOUD_LLM_APP_LIMIT_SWITCH";

    /**
     * 应用限流用户白名单
     */
    String APP_LIMIT_USER_WHITE_LIST = "STAR_CLOUD_LLM_APP_LIMIT_USER_WHITE_LIST";

    /**
     * 创作方案字典类型
     */
    String CREATIVE_SCHEME_CATEGORY_DICT_TYPE = "STAR_CLOUD_LLM_CREATIVE_SCHEME_CATEGORY_TYPE";

    /**
     * 默认应用配置
     */
    String DEFAULT_APP_CONFIGURATION = "DEFAULT_APP_CONFIGURATION";

    /**
     * 应用市场应用默认图片
     */
    String APP_MARKET_DEFAULT_IMAGE = "https://download.hotsalecloud.com/mofaai/images/category/seo.jpg";

    /**
     * 默认 Prompt
     */
    String DEFAULT_IMAGE_PROMPT = "perfectionism,max detail,4k uhd,ornate,HQ,";

    /**
     * 默认反义词
     */
    String DEFAULT_IMAGE_NEGATIVE_PROMPT = "Ugly, poorly drawn hands, poorly drawn feet, poorly drawn faces, out of frame, redundant limbs, disfigurement, deformation, body out of frame, bad anatomy, watermark, signature, cut, low contrast, underexposed, overexposed, bad art, beginner, amateur, distorted face, blur, draft, grainy, monster, nudity, female, sex, pornography, nude, violence, murder, perversion, finger, stump, arm, body, nude, nipple, sex organ, low quality, distortion, ugly, normal quality, low quality, worst quality, painting, sketch, (worst quality, low quality: 1.4), bad anatomy, watermark, text, signature, blur, messy, low quality, bad artist sketch, (semi-reality, sketch, cartoon, painting, anime: 1.4), cropping, off-frame, worst quality, low quality, artifact vignetting, NSFW, distortion, plastic feeling, blur, draft, grainy, bad art, low contrast";

    /**
     * CACHE_CONTROL
     */
    String CACHE_CONTROL = "Cache-Control";

    /**
     * CACHE_CONTROL_VALUE
     */
    String CACHE_CONTROL_VALUE = "no-cache, no-transform";

    /**
     * X_ACCEL_BUFFERING
     */
    String X_ACCEL_BUFFERING = "X-Accel-Buffering";

    /**
     * X_ACCEL_BUFFERING_VALUE
     */
    String X_ACCEL_BUFFERING_VALUE = "no";

    /**
     * ROOT
     */
    String ROOT = "ROOT";

    /**
     * ALL
     */
    String ALL = "ALL";

    /**
     * 生成字符串的范围
     */
    String CHARS = "ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    String SYSTEM_USER_PROMPT_SPLIT = "------SYSTEM_USER_PROMPT_SPLIT------";
}
