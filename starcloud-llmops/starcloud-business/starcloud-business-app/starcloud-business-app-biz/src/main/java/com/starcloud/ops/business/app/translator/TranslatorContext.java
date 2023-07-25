package com.starcloud.ops.business.app.translator;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
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
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-24
 */
@Slf4j
@Component
public class TranslatorContext implements ApplicationContextAware {

    /**
     * 翻译器映射, 存放所有翻译器
     */
    private static final Map<Integer, Translator> TRANSLATOR_MAP = new ConcurrentHashMap<>(4);

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
        // 清空翻译器映射
        TRANSLATOR_MAP.clear();
        // 获取所有翻译器
        Map<String, Translator> translatorMap = applicationContext.getBeansOfType(Translator.class);
        // 遍历所有翻译器
        for (Map.Entry<String, Translator> translatorEntry : translatorMap.entrySet()) {
            TranslatorType annotation = AnnotationUtils.findAnnotation(translatorEntry.getValue().getClass(), TranslatorType.class);
            if (annotation == null) {
                log.warn("翻译器[{}]未配置翻译器类型", translatorEntry.getKey());
                continue;
            }
            TranslatorTypeEnum value = annotation.value();
            if (!TRANSLATOR_MAP.containsKey(value.getCode())) {
                TRANSLATOR_MAP.put(value.getCode(), translatorEntry.getValue());
            }
        }
    }

    /**
     * 获取翻译器
     *
     * @param type 翻译器类型的 code 值
     * @return 翻译器
     */
    public Translator getTranslator(Integer type) {
        if (type == null) {
            throw ServiceExceptionUtil.exception(new ErrorCode(320000, "翻译器类型不能为空"));
        }
        if (!TRANSLATOR_MAP.containsKey(type)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(320000, "翻译器类型不存在"));
        }
        return TRANSLATOR_MAP.get(type);
    }

}
