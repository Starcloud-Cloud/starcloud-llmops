package com.starcloud.ops.business.app.domain.channel;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.publish.AppPublishChannelEnum;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 根据 type 值获取对应的发布渠道配置处理器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-02
 */
@SuppressWarnings("all")
@Slf4j
@Component
public class AppPublishChannelConfigFactory implements ApplicationContextAware {

    /**
     * 发布渠道配置处理器映射, 存放所有发布渠道配置处理器
     */
    private static final Map<Integer, AppPublishChannelConfigTemplate> HANDLER_MAP = new ConcurrentHashMap<>(8);

    /**
     * 根据 type 值获取对应的发布渠道配置处理器
     *
     * @param type 发布渠道类型
     * @return 发布渠道配置处理器
     */
    public AppPublishChannelConfigTemplate getHandler(Integer type) {
        AppValidate.notNull(type, ErrorCodeConstants.APP_PUBLISH_CHANNEL_TYPE_NOT_NULL);
        if (!IEnumable.containsOfCode(type, AppPublishChannelEnum.class)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_CHANNEL_TYPE_NOT_SUPPORTED);
        }
        if (HANDLER_MAP.containsKey(type)) {
            return HANDLER_MAP.get(type);
        }
        throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_CHANNEL_TYPE_NOT_SUPPORTED);
    }

    /**
     * Set the ApplicationContext that this object runs in.
     * Normally this call will be used to initialize the object.
     * <p>Invoked after population of normal bean properties but before an init callback such
     * as {@link InitializingBean#afterPropertiesSet()}
     * or a custom init-method. Invoked after {@link ResourceLoaderAware#setResourceLoader},
     * {@link ApplicationEventPublisherAware#setApplicationEventPublisher} and
     * {@link MessageSourceAware}, if applicable.
     *
     * @param applicationContext the ApplicationContext object to be used by this object
     * @throws ApplicationContextException in case of context initialization errors
     * @throws BeansException              if thrown by application context methods
     * @see BeanInitializationException
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        // 清空渠道配置映射
        HANDLER_MAP.clear();
        // 获取所有翻译器
        Map<String, AppPublishChannelConfigTemplate> translatorMap = applicationContext.getBeansOfType(AppPublishChannelConfigTemplate.class);
        // 遍历所有翻译器
        for (Map.Entry<String, AppPublishChannelConfigTemplate> translatorEntry : translatorMap.entrySet()) {
            AppPublishChannelConfigType annotation = AnnotationUtils.findAnnotation(translatorEntry.getValue().getClass(), AppPublishChannelConfigType.class);
            if (annotation == null) {
                log.warn("翻译器[{}]未配置翻译器类型", translatorEntry.getKey());
                continue;
            }
            AppPublishChannelEnum value = annotation.value();
            if (!HANDLER_MAP.containsKey(value.getCode())) {
                HANDLER_MAP.put(value.getCode(), translatorEntry.getValue());
            }
        }
        log.info("初始化发布渠道配置处理器完成: HANDLER_MAP: {}", HANDLER_MAP);
    }
}
