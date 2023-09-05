package com.starcloud.ops.business.app.enums.limit;

import com.starcloud.ops.business.app.api.limit.dto.AppLimitConfigDTO;
import com.starcloud.ops.business.app.enums.RecommendAppConsts;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-28
 */
@Getter
public enum AppLimitConfigEnum implements IEnumable<Integer> {

    /**
     * 默认使用频率限流配置
     */
    RATE(1, "默认使用频率限流配置") {
        @Override
        public AppLimitConfigDTO getDefaultConfig() {
            AppLimitConfigDTO config = new AppLimitConfigDTO();
            config.setCode(name());
            config.setEnable(Boolean.FALSE);
            config.setLimitBy(AppLimitByEnum.APP.name());
            config.setThreshold(60);
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
            config.setLimitBy(AppLimitByEnum.APP.name());
            config.setThreshold(60);
            config.setTimeInterval(60L);
            config.setIgnoreApps(RATE_IGNORE_APPS);
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
            config.setLimitBy(AppLimitByEnum.USER.name());
            config.setThreshold(60);
            config.setTimeInterval(1L);
            config.setTimeUnit("MONTHS");
            config.setMessage("抱歉，您已经达到最大访问上限！");
            return config;
        }

        @Override
        public AppLimitConfigDTO getDefaultSystemConfig() {
            AppLimitConfigDTO config = new AppLimitConfigDTO();
            config.setCode(name());
            config.setEnable(Boolean.TRUE);
            config.setLimitBy(AppLimitByEnum.USER.name());
            config.setThreshold(60);
            config.setTimeInterval(1L);
            config.setTimeUnit("MONTHS");
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
            config.setLimitBy(AppLimitByEnum.ADVERTISING.name());
            config.setThreshold(20);
            config.setTimeInterval(1L);
            config.setTimeUnit("MONTHS");
            config.setMessage("魔法AI - 基于AI技术，轻松创建数字员工，赶快来魔法AI创建一个属于自己的数字员工吧。");
            return config;
        }

        @Override
        public AppLimitConfigDTO getDefaultSystemConfig() {
            AppLimitConfigDTO config = new AppLimitConfigDTO();
            config.setCode(name());
            config.setEnable(Boolean.TRUE);
            config.setLimitBy(AppLimitByEnum.ADVERTISING.name());
            config.setThreshold(20);
            config.setTimeInterval(1L);
            config.setTimeUnit("MONTHS");
            config.setMessage("魔法AI - 基于AI技术，轻松创建数字员工，赶快来魔法AI创建一个属于自己的数字员工吧。");
            return config;
        }
    },

    /**
     * 推荐应用限流规则
     */
    RECOMMEND_APP(4, "默认广告限流配置") {
        @Override
        public AppLimitConfigDTO getDefaultConfig() {
            return null;
        }

        @Override
        public AppLimitConfigDTO getDefaultSystemConfig() {
            AppLimitConfigDTO config = new AppLimitConfigDTO();
            config.setCode(name());
            config.setEnable(Boolean.TRUE);
            config.setLimitBy(AppLimitByEnum.APP.name());
            config.setThreshold(100);
            config.setTimeInterval(60L);
            config.setMatchApps(DEFAULT_RECOMMEND_APPS);
            config.setTimeUnit("SECONDS");
            config.setMessage("当前访问用户过多，请稍后再试！");
            return config;
        }
    },

    /**
     * 推荐应用限流规则
     */
    BASE_GENERATE_IMAGE(5, "默认图片生产限流") {
        @Override
        public AppLimitConfigDTO getDefaultConfig() {
            return null;
        }

        @Override
        public AppLimitConfigDTO getDefaultSystemConfig() {
            AppLimitConfigDTO config = new AppLimitConfigDTO();
            config.setCode(name());
            config.setEnable(Boolean.TRUE);
            config.setLimitBy(AppLimitByEnum.APP.name());
            config.setThreshold(100);
            config.setTimeInterval(60L);
            config.setMatchApps(Collections.singletonList(RecommendAppConsts.BASE_GENERATE_IMAGE));
            config.setTimeUnit("SECONDS");
            config.setMessage("当前访问用户过多，请稍后再试！");
            return config;
        }
    },
    ;

    /**
     * 应用级别限流忽略的应用
     */
    private static final List<String> RATE_IGNORE_APPS = Arrays.asList(RecommendAppConsts.GENERATE_TEXT, RecommendAppConsts.GENERATE_ARTICLE, RecommendAppConsts.BASE_GENERATE_IMAGE, RecommendAppConsts.CHAT_ROBOT);

    /**
     * 推荐应用限流
     */
    private static final List<String> DEFAULT_RECOMMEND_APPS = Arrays.asList(RecommendAppConsts.GENERATE_TEXT, RecommendAppConsts.GENERATE_ARTICLE, RecommendAppConsts.CHAT_ROBOT);


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
    AppLimitConfigEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
