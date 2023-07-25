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
     * 国内测试环境分享链接
     */
    String APP_SHARE_LINK_CN_TEST = "http://cn-test.llmops-ui-user.hotsalestar.com/app/share/";

    /**
     * 国内生产环境分享链接
     */
    String APP_SHARE_LINK_CN_PROD = "https://mofaai.com.cn/app/share/";

    /**
     * 海外生产环境分享链接
     */
    String APP_SHARE_LINK_US_PROD = "https://mofaai.com.cn/app/share/";

    /**
     * APP 应用前缀
     */
    String APP_PREFIX = "app";

    /**
     * 应用市场应用前缀
     */
    String MARKET_PREFIX = "market";

    /**
     * 应用分类字典类型
     */
    String APP_CATEGORY_DICT_TYPE = "STAR_CLOUD_LLM_APP_CATEGORY_TYPE";

    /**
     * 图片生成 Prompt 案例
     */
    String IMAGE_EXAMPLE_PROMPT = "STAR_CLOUD_LLM_IMAGE_EXAMPLE_PROMPT";

    /**
     * 应用市场应用默认图片
     */
    String APP_MARKET_DEFAULT_IMAGE = "https://download.hotsalecloud.com/mofaai/images/category/seo.jpg";
}
