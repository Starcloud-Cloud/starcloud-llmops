package com.starcloud.ops.business.app.enums.vsearch;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 取样器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-10
 */
public enum SamplerEnum implements IEnumable<Integer> {

    /**
     * ddim
     */
    DDIM(0, "SAMPLER_DDPM_LABEL", "SAMPLER_DDPM_DESCRIPTION", ""),

    /**
     * ddpm
     */
    DDPM(1, "SAMPLER_DDPM_LABEL", "SAMPLER_DDPM_DESCRIPTION", ""),

    /**
     * k_euler
     */
    K_EULER(2, "SAMPLER_K_EULER_LABEL", "SAMPLER_K_EULER_DESCRIPTION", ""),

    /**
     * k_euler_ancestral
     */
    K_EULER_ANCESTRAL(3, "SAMPLER_K_EULER_ANCESTRAL_LABEL", "SAMPLER_K_EULER_ANCESTRAL_DESCRIPTION", ""),

    /**
     * k_heun
     */
    K_HEUN(4, "SAMPLER_K_HEUN_LABEL", "SAMPLER_K_HEUN_DESCRIPTION", ""),

    /**
     * k_dpm_2
     */
    K_DPM_2(5, "SAMPLER_K_DPM_2_LABEL", "SAMPLER_K_DPM_2_DESCRIPTION", ""),

    /**
     * k_dpm_2_ancestral
     */
    K_DPM_2_ANCESTRAL(6, "SAMPLER_K_DPM_2_ANCESTRAL_LABEL", "SAMPLER_K_DPM_2_ANCESTRAL_DESCRIPTION", ""),

    /**
     * k_lms
     */
    K_LMS(7, "SAMPLER_K_LMS_LABEL", "SAMPLER_K_LMS_DESCRIPTION", ""),

    /**
     * k_dpmpp_2s_ancestral
     */
    K_DPMPP_2S_ANCESTRAL(8, "SAMPLER_K_DPMPP_2S_ANCESTRAL_LABEL", "SAMPLER_K_DPMPP_2S_ANCESTRAL_DESCRIPTION", ""),

    /**
     * k_dpmpp_2m sd 默认采样器
     */
    K_DPMPP_2M(9, "SAMPLER_K_DPMPP_2M_LABEL", "SAMPLER_K_DPMPP_2M_DESCRIPTION", ""),

    /**
     * k_dpmpp_sde
     */
    K_DPMPP_SDE(10, "SAMPLER_K_DPMPP_SDE_LABEL", "SAMPLER_K_DPMPP_SDE_DESCRIPTION", ""),
    ;

    /**
     * 采样器 code
     */
    @Getter
    private final Integer code;

    /**
     * 采样器 label
     */
    @Getter
    private final String label;

    /**
     * 采样器描述
     */
    @Getter
    private final String description;

    /**
     * 采样器图片
     */
    @Getter
    private final String image;

    /**
     * 构造方法
     *
     * @param code        采样器 code
     * @param label       采样器 label
     * @param description 采样器描述
     * @param image       采样器图片
     */
    SamplerEnum(Integer code, String label, String description, String image) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.image = image;
    }
}
