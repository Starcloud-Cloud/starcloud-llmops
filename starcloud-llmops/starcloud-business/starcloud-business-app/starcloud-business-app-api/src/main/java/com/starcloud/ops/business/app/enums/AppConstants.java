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

    /**
     * 默认反义词
     */
    String DEFAULT_NEGATIVE_PROMPT = "Ugly, poorly drawn hands, poorly drawn feet, poorly drawn faces, out of frame, redundant limbs, disfigurement, deformation, body out of frame, bad anatomy, watermark, signature, cut, low contrast, underexposed, overexposed, bad art, beginner, amateur, distorted face, blur, draft, grainy, monster, nudity, female, sex, pornography, nude, violence, murder, perversion, finger, stump, arm, body, nude, nipple, sex organ, low quality, distortion, ugly, normal quality, low quality, worst quality, painting, sketch, (worst quality, low quality: 1.4), bad anatomy, watermark, text, signature, blur, messy, low quality, bad artist sketch, (semi-reality, sketch, cartoon, painting, anime: 1.4), cropping, off-frame, worst quality, low quality, artifact vignetting, NSFW, distortion, plastic feeling, blur, draft, grainy, bad art, low contrast.";
}
