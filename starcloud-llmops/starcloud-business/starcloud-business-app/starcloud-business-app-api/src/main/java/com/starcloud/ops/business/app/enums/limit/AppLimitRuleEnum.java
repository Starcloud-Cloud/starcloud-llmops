package com.starcloud.ops.business.app.enums.limit;

import com.starcloud.ops.business.app.api.limit.dto.AppLimitRuleDTO;
import com.starcloud.ops.business.app.enums.RecommendAppEnum;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-28
 */
@Getter
public enum AppLimitRuleEnum implements IEnumable<Integer> {

    /**
     * 默认使应用频率限流配置
     */
    APP_LIMIT_RULE(1, "默认使用频率限流配置") {
        @Override
        public AppLimitRuleDTO defaultRule() {
            AppLimitRuleDTO rule = new AppLimitRuleDTO();
            rule.setCode(name());
            rule.setEnable(Boolean.FALSE);
            rule.setLimitBy(AppLimitByEnum.APP.name());
            rule.setOrder(getCode());
            rule.setThreshold(60);
            rule.setTimeInterval(60L);
            rule.setTimeUnit("SECONDS");
            rule.setMessage("当前访问用户过多，请稍后再试！");
            return rule;
        }

        @Override
        public AppLimitRuleDTO defaultSystemRule() {
            AppLimitRuleDTO rule = new AppLimitRuleDTO();
            rule.setCode(name());
            rule.setEnable(Boolean.TRUE);
            rule.setLimitBy(AppLimitByEnum.APP.name());
            rule.setOrder(Integer.MAX_VALUE - 100);
            rule.setThreshold(120);
            rule.setTimeInterval(60L);
            rule.setExcludeApps(RATE_IGNORE_APPS);
            rule.setTimeUnit("SECONDS");
            rule.setMessage("当前访问用户过多，请稍后再试！");
            return rule;
        }
    },

    /**
     * 推荐应用限流规则
     */
    RECOMMEND_LIMIT_RULE(2, "默认广告限流配置") {
        @Override
        public AppLimitRuleDTO defaultRule() {
            return null;
        }

        @Override
        public AppLimitRuleDTO defaultSystemRule() {
            AppLimitRuleDTO rule = new AppLimitRuleDTO();
            rule.setCode(name());
            rule.setEnable(Boolean.TRUE);
            rule.setLimitBy(AppLimitByEnum.APP.name());
            rule.setOrder(Integer.MAX_VALUE - 100);
            rule.setThreshold(500);
            rule.setTimeInterval(60L);
            rule.setIncludeApps(DEFAULT_RECOMMEND_APPS);
            rule.setTimeUnit("SECONDS");
            rule.setMessage("当前访问用户过多，请稍后再试！");
            return rule;
        }
    },

    /**
     * 图片应用限流规则
     */
    IMAGE_LIMIT_RULE(3, "默认图片生产限流") {
        @Override
        public AppLimitRuleDTO defaultRule() {
            return null;
        }

        @Override
        public AppLimitRuleDTO defaultSystemRule() {
            AppLimitRuleDTO rule = new AppLimitRuleDTO();
            rule.setCode(name());
            rule.setEnable(Boolean.TRUE);
            rule.setLimitBy(AppLimitByEnum.APP.name());
            rule.setOrder(Integer.MAX_VALUE - 100);
            rule.setThreshold(500);
            rule.setTimeInterval(60L);
            rule.setIncludeApps(Collections.singletonList(RecommendAppEnum.GENERATE_IMAGE.name()));
            rule.setTimeUnit("SECONDS");
            rule.setMessage("当前访问用户过多，请稍后再试！");
            return rule;
        }
    },

    /**
     * 默认用户总量限流配置
     */
    USER_LIMIT_RULE(4, "默认用户使用频率限流配置") {
        @Override
        public AppLimitRuleDTO defaultRule() {
            AppLimitRuleDTO rule = new AppLimitRuleDTO();
            rule.setCode(name());
            rule.setEnable(Boolean.FALSE);
            rule.setLimitBy(AppLimitByEnum.USER.name());
            rule.setOrder(getCode());
            rule.setThreshold(60);
            rule.setTimeInterval(1L);
            rule.setTimeUnit("SECONDS");
            rule.setMessage("抱歉，您已经达到最大访问上限！");
            return rule;
        }

        @Override
        public AppLimitRuleDTO defaultSystemRule() {
            AppLimitRuleDTO rule = new AppLimitRuleDTO();
            rule.setCode(name());
            rule.setEnable(Boolean.TRUE);
            rule.setLimitBy(AppLimitByEnum.USER.name());
            rule.setOrder(Integer.MAX_VALUE - 10);
            rule.setThreshold(120);
            rule.setTimeInterval(1L);
            rule.setTimeUnit("SECONDS");
            rule.setMessage("抱歉，您已经达到最大访问上限！");
            return rule;
        }
    },

    /**
     * 默认广告限流配置
     */
    ADVERTISING_RULE(Integer.MAX_VALUE, "默认广告限流配置") {
        @Override
        public AppLimitRuleDTO defaultRule() {
            AppLimitRuleDTO config = new AppLimitRuleDTO();
            config.setCode(name());
            config.setEnable(Boolean.TRUE);
            config.setLimitBy(AppLimitByEnum.ADVERTISING.name());
            config.setOrder(getCode());
            config.setThreshold(20);
            config.setTimeInterval(1L);
            config.setTimeUnit("SECONDS");
            config.setMessage("魔法AI - 基于AI技术，轻松创建数字员工，赶快来魔法AI创建一个属于自己的数字员工吧。");
            return config;
        }

        @Override
        public AppLimitRuleDTO defaultSystemRule() {
            // 系统不配置广告限流
            return null;
        }
    },

    ;

    /**
     * 应用级别限流忽略的应用
     */
    private static final List<String> RATE_IGNORE_APPS = Arrays.asList(RecommendAppEnum.GENERATE_TEXT.name(), RecommendAppEnum.GENERATE_ARTICLE.name(), RecommendAppEnum.GENERATE_IMAGE.name(), RecommendAppEnum.CHAT_ROBOT.name());

    /**
     * 推荐应用限流
     */
    private static final List<String> DEFAULT_RECOMMEND_APPS = Arrays.asList(RecommendAppEnum.GENERATE_TEXT.name(), RecommendAppEnum.GENERATE_ARTICLE.name(), RecommendAppEnum.CHAT_ROBOT.name());

    /**
     * 获取限流默认配置
     *
     * @return 默认限流配置
     */
    public abstract AppLimitRuleDTO defaultRule();

    /**
     * 获取系统默认限流配置，最后的兜底
     *
     * @return 默认限流配置
     */
    public abstract AppLimitRuleDTO defaultSystemRule();

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
    AppLimitRuleEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 获取系统兜底限流配置
     *
     * @return 系统兜底限流配置
     */
    public static List<AppLimitRuleDTO> defaultSystemLimitRuleList() {
        return Arrays.stream(values()).map(AppLimitRuleEnum::defaultSystemRule)
                .filter(Objects::nonNull)
                .filter(AppLimitRuleDTO::getEnable)
                .collect(Collectors.toList());
    }
}
