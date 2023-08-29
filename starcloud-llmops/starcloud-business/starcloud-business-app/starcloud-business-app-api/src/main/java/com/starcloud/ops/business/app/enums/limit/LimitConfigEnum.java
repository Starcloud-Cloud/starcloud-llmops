package com.starcloud.ops.business.app.enums.limit;

import com.starcloud.ops.business.app.api.limit.dto.LimitConfigDTO;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.time.temporal.ChronoUnit;

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
    DEFAULT_QUOTA_LIMIT(0, Boolean.FALSE, "QUOTA", "默认总量限流配置") {
        @Override
        public LimitConfigDTO getDefaultConfig() {
            return LimitConfigDTO.of(1, 1, ChronoUnit.YEARS, "抱歉，该应用已经达到最大访问上限");
        }
    },

    /**
     * 默认使用频率限流配置
     */
    DEFAULT_RATE_LIMIT(1, Boolean.FALSE, "RATE", "默认使用频率限流配置") {
        @Override
        public LimitConfigDTO getDefaultConfig() {
            return LimitConfigDTO.of(1, 60, ChronoUnit.SECONDS, "当前咨询用户过多，请排队等候");
        }
    },

    /**
     * 默认用户总量限流配置
     */
    DEFAULT_USER_QUOTA_LIMIT(2, Boolean.FALSE, "USER_QUOTA", "默认用户总量限流配置") {
        @Override
        public LimitConfigDTO getDefaultConfig() {
            return LimitConfigDTO.of(1, 1, ChronoUnit.MONTHS, "抱歉，您已经达到最大访问上限");
        }
    },

    /**
     * 默认广告限流配置
     */
    DEFAULT_ADVERTISING_LIMIT(3, Boolean.FALSE, "ADVERTISING", "默认广告限流配置") {
        @Override
        public LimitConfigDTO getDefaultConfig() {
            return LimitConfigDTO.of(1, 1, ChronoUnit.YEARS, " 快点开通 VIP，享受更好的体验");
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
    private final String operate;

    /**
     * 配置标签
     */
    private final String label;


    LimitConfigEnum(Integer code, Boolean enable, String operate, String label) {
        this.code = code;
        this.operate = operate;
        this.label = label;
        this.enable = enable;
    }
}
