package com.starcloud.ops.business.app.enums.vsearch;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-19
 */
public enum EngineEnum implements IEnumable<String> {

    /**
     * stable-diffusion-xl-1024-v0-9
     */
    STABLE_DIFFUSION_XL_1024_V0_9("stable-diffusion-xl-1024-v0-9", "SDXL v0.9", "Stability-AI Stable Diffusion XL v0.9", "PICTURE", ""),

    /**
     * stable-diffusion-512-v2-1
     */
    STABLE_DIFFUSION_512_V2_1("stable-diffusion-512-v2-1", "Stable Diffusion v2.1", "Stability-AI Stable Diffusion v2.1", "PICTURE", "IMAGE"),

    /**
     * stable-diffusion-768-v2-1
     */
    STABLE_DIFFUSION_768_V2_1("stable-diffusion-768-v2-1", "Stable Diffusion v2.1-768", "Stability-AI Stable Diffusion 768 v2.1", "PICTURE", "IMAGE"),

    /**
     * stable-diffusion-xl-beta-v2-2-2
     */
    STABLE_DIFFUSION_XL_BETA_V2_2_2("stable-diffusion-xl-beta-v2-2-2", "SDXL Beta", "Stability-AI Stable Diffusion XL Beta v2.2.2", "PICTURE", "IMAGE"),

    /**
     * esrgan-v1-x2plus
     */
    ESRGAN_V1_X2PLUS("esrgan-v1-x2plus", "Real-ESRGAN x2", "Real-ESRGAN_x2plus upscaler model", "PICTURE", "UPSCALING"),

    /**
     * stable-diffusion-x4-latent-upscaler
     */
    STABLE_DIFFUSION_X4_LATENT_UPSCALER("stable-diffusion-x4-latent-upscaler", "Stable Diffusion x4 Latent Upscaler", "Stable Diffusion x4 Latent Upscaler", "PICTURE", "UPSCALING"),

    ;

    @Getter
    private final String code;

    @Getter
    private final String label;

    @Getter
    private final String description;

    @Getter
    private final String type;

    @Getter
    private final String engineType;

    EngineEnum(String code, String label, String description, String type, String engineType) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.type = type;
        this.engineType = engineType;
    }
}
