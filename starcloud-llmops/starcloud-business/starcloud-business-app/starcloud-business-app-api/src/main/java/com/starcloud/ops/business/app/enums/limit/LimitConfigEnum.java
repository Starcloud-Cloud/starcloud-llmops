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
     * 默认总量限流配置
     */
    QUOTA(0, Boolean.FALSE, 1, "默认总量限流配置") {
        @Override
        public LimitConfigDTO getDefaultConfig() {
            return LimitConfigDTO.of(1, 1L, "YEARS", "抱歉，该应用已经达到最大访问上限！");
        }

        @Override
        public LimitConfigDTO getDefaultSystemConfig() {
            return getDefaultConfig();
        }
    },

    /**
     * 默认使用频率限流配置
     */
    RATE(1, Boolean.FALSE, 2, "默认使用频率限流配置") {
        @Override
        public LimitConfigDTO getDefaultConfig() {
            return LimitConfigDTO.of(1, 60L, "SECONDS", "当前咨询用户过多，请排队等候！");
        }

        @Override
        public LimitConfigDTO getDefaultSystemConfig() {
            return getDefaultConfig();
        }
    },

    /**
     * 默认用户总量限流配置
     */
    USER_QUOTA(2, Boolean.FALSE, 3, "默认用户总量限流配置") {
        @Override
        public LimitConfigDTO getDefaultConfig() {
            return LimitConfigDTO.of(1, 1L, "MONTHS", "抱歉，您已经达到最大访问上限！");
        }

        @Override
        public LimitConfigDTO getDefaultSystemConfig() {
            return getDefaultConfig();
        }
    },

    /**
     * 默认广告限流配置
     */
    ADVERTISING(3, Boolean.FALSE, 4, "默认广告限流配置") {
        @Override
        public LimitConfigDTO getDefaultConfig() {
            return LimitConfigDTO.of(1, 1L, "YEARS", " 快点开通 VIP，享受更好的体验！");
        }

        @Override
        public LimitConfigDTO getDefaultSystemConfig() {
            return getDefaultConfig();
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
     * 是否启用
     */
    private final Boolean enable;

    /**
     * 操作类型
     */
    private final Integer sort;

    /**
     * 配置标签
     */
    private final String label;


    LimitConfigEnum(Integer code, Boolean enable, Integer sort, String label) {
        this.code = code;
        this.sort = sort;
        this.label = label;
        this.enable = enable;
    }
}
