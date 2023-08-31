package com.starcloud.ops.business.app.enums.limit;

import com.starcloud.ops.business.app.api.limit.dto.AppLimitConfigDTO;
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
        public AppLimitConfigDTO getDefaultConfig() {
            AppLimitConfigDTO config = new AppLimitConfigDTO();
            config.setCode(name());
            config.setEnable(Boolean.FALSE);
            config.setLimitBy(LimitByEnum.APP.name());
            config.setLimit(60);
            config.setTimeInterval(60L);
            config.setTimeUnit("SECONDS");
            config.setMessage("当前访问用户过多，请稍后再试！");
            return config;
        }

        @Override
        public AppLimitConfigDTO getDefaultSystemConfig() {
            AppLimitConfigDTO config = new AppLimitConfigDTO();
            config.setCode(name());
            config.setEnable(Boolean.TRUE);
            config.setLimitBy(LimitByEnum.APP.name());
            config.setLimit(60);
            config.setTimeInterval(60L);
            config.setTimeUnit("SECONDS");
            config.setMessage("当前访问用户过多，请稍后再试！");
            return config;
        }
    },

    /**
     * 默认用户总量限流配置
     */
    USER_RATE(2, "默认用户使用频率限流配置") {
        @Override
        public AppLimitConfigDTO getDefaultConfig() {
            AppLimitConfigDTO config = new AppLimitConfigDTO();
            config.setCode(name());
            config.setEnable(Boolean.FALSE);
            config.setLimitBy(LimitByEnum.USER.name());
            config.setLimit(20);
            config.setTimeInterval(60L);
            config.setTimeUnit("SECONDS");
            config.setMessage("抱歉，您已经达到最大访问上限！");
            return config;
        }

        @Override
        public AppLimitConfigDTO getDefaultSystemConfig() {
            AppLimitConfigDTO config = new AppLimitConfigDTO();
            config.setCode(name());
            config.setEnable(Boolean.TRUE);
            config.setLimitBy(LimitByEnum.USER.name());
            config.setLimit(20);
            config.setTimeInterval(60L);
            config.setTimeUnit("SECONDS");
            config.setMessage("抱歉，您已经达到最大访问上限！");
            return config;
        }
    },

    /**
     * 默认广告限流配置
     */
    ADVERTISING(3, "默认广告限流配置") {
        @Override
        public AppLimitConfigDTO getDefaultConfig() {
            AppLimitConfigDTO config = new AppLimitConfigDTO();
            config.setCode(name());
            config.setEnable(Boolean.FALSE);
            config.setLimitBy(LimitByEnum.ADVERTISING.name());
            config.setLimit(5);
            config.setTimeInterval(60L);
            config.setTimeUnit("SECONDS");
            config.setMessage("基于AI技术，智能推荐，智能对话，为您推荐更多优质内容！");
            return config;
        }

        @Override
        public AppLimitConfigDTO getDefaultSystemConfig() {
            AppLimitConfigDTO config = new AppLimitConfigDTO();
            config.setCode(name());
            config.setEnable(Boolean.TRUE);
            config.setLimitBy(LimitByEnum.ADVERTISING.name());
            config.setLimit(5);
            config.setTimeInterval(60L);
            config.setTimeUnit("SECONDS");
            config.setMessage("基于AI技术，智能推荐，智能对话，为您推荐更多优质内容！");
            return config;
        }
    },
    ;

    /**
     * 获取限流默认配置
     *
     * @return 默认限流配置
     */
    public abstract AppLimitConfigDTO getDefaultConfig();

    /**
     * 获取系统默认限流配置，最后的兜底
     *
     * @return 默认限流配置
     */
    public abstract AppLimitConfigDTO getDefaultSystemConfig();

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
