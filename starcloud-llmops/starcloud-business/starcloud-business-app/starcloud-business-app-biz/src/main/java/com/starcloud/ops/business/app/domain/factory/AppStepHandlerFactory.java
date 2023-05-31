package com.starcloud.ops.business.app.domain.factory;

import cn.hutool.core.lang.Assert;
import com.starcloud.ops.business.app.domain.handler.BaseStepHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取步骤处理器工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@Component
public class AppStepHandlerFactory implements ApplicationContextAware {

    /**
     * 步骤处理器缓存
     */
    private static final Map<String, BaseStepHandler> STEP_HANDLER_MAP = new ConcurrentHashMap<>(8);

    /**
     * 根据步骤类型获取对应的处理器
     *
     * @param type 步骤类型
     * @return 步骤处理器
     */
    public BaseStepHandler getHandler(String type) {
        Assert.notBlank(type, "The Step Type must not be blank");

        type = StringUtils.uncapitalize(type);
        if (STEP_HANDLER_MAP.containsKey(type)) {
            return STEP_HANDLER_MAP.get(type);
        }
        throw new IllegalArgumentException("The Step Handler is not exist. type: " + type);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        // 将所有的步骤处理器放入缓存
        Map<String, BaseStepHandler> stepHandlerMap = context.getBeansOfType(BaseStepHandler.class);
        STEP_HANDLER_MAP.putAll(stepHandlerMap);
        log.info("Load App Step Handle: ");
        STEP_HANDLER_MAP.forEach((key, value) -> log.info("key: {}, value: {}", key, value));
    }
}
