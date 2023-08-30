package com.starcloud.ops.business.app.enums.limit;

import com.starcloud.ops.business.app.api.limit.dto.LimitConfigDTO;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-28
 */
@Getter
public enum LimitConfigEnum implements IEnumable<Integer> {

    /**
     * 默认使用频率限流配置
     */
    RATE(1, "默认使用频率限流配置") {
        @Override
        public LimitConfigDTO getDefaultConfig() {
            LimitConfigDTO config = new LimitConfigDTO();
            config.setCode(name());
            config.setEnable(Boolean.FALSE);

            return LimitConfigDTO.of(Boolean.FALSE, 1, 60L, "SECONDS", "当前咨询用户过多，请排队等候！");
        }

        @Override
        public LimitConfigDTO getDefaultSystemConfig() {
            return LimitConfigDTO.of(Boolean.FALSE, 1, 60L, "SECONDS", "系统默认：当前咨询用户过多，请排队等候！");
        }
    },

    /**
     * 默认用户总量限流配置
     */
    USER_RATE(2, "默认用户使用频率限流配置") {
        @Override
        public LimitConfigDTO getDefaultConfig() {
            return LimitConfigDTO.of(Boolean.FALSE, 2, 60L, "SECONDS", "抱歉，您已经达到最大访问上限！");
        }

        @Override
        public LimitConfigDTO getDefaultSystemConfig() {
            return LimitConfigDTO.of(Boolean.FALSE, 2, 60L, "SECONDS", "系统默认：抱歉，您已经达到最大访问上限！");
        }
    },

    /**
     * 默认广告限流配置
     */
    ADVERTISING(3, "默认广告限流配置") {
        @Override
        public LimitConfigDTO getDefaultConfig() {
            return LimitConfigDTO.of(Boolean.FALSE, 1, 1L, "YEARS", "快点开通 VIP，享受更好的体验！");
        }

        @Override
        public LimitConfigDTO getDefaultSystemConfig() {
            return LimitConfigDTO.of(Boolean.FALSE, 1, 1L, "YEARS", "系统默认：快点开通 VIP，享受更好的体验！");
        }
    },
    ;

    /**
     * 获取限流默认配置
     *
     * @return 默认限流配置
     */
    public abstract LimitConfigDTO getDefaultConfig();

    /**
     * 获取系统默认限流配置
     *
     * @return 默认限流配置
     */
    public abstract LimitConfigDTO getDefaultSystemConfig();

    /**
     * 配置编码
     */
    private final Integer code;

    /**
     * 配置标签
     */
    private final String label;

    /**
     * 构造方法
     *
     * @param code  编码
     * @param label 标签
     */
    LimitConfigEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
