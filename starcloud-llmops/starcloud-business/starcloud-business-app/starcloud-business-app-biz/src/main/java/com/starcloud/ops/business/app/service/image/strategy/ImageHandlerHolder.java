package com.starcloud.ops.business.app.service.image.strategy;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.image.strategy.handler.BaseImageHandler;
import com.starcloud.ops.business.app.api.AppValidate;
import lombok.extern.slf4j.Slf4j;
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
 * @since 2023-09-22
 */
@SuppressWarnings("all")
@Slf4j
@Component
public class ImageHandlerHolder implements ApplicationContextAware {

    /**
     * 发布渠道配置处理器映射, 存放所有发布渠道配置处理器
     */
    private static final Map<String, BaseImageHandler> IMAGE_HANDLER_MAP = new ConcurrentHashMap<>(8);

    /**
     * 根据 type 值获取对应的发布渠道配置处理器
     *
     * @param scene 发布渠道类型
     * @return 发布渠道配置处理器
     */
    public BaseImageHandler getHandler(String scene) {
        AppValidate.notBlank(scene, ErrorCodeConstants.EXECUTE_SCENE_REQUIRED);
        if (!AppSceneEnum.SUPPORT_IMAGE_SCENE.contains(scene)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_SCENE_UNSUPPORTED);
        }
        if (IMAGE_HANDLER_MAP.containsKey(scene)) {
            return IMAGE_HANDLER_MAP.get(scene);
        }
        throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_SCENE_UNSUPPORTED);
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
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 清空渠道配置映射
        IMAGE_HANDLER_MAP.clear();
        // 获取所有翻译器
        Map<String, BaseImageHandler> imageHandlerMap = applicationContext.getBeansOfType(BaseImageHandler.class);
        // 遍历所有翻译器
        for (Map.Entry<String, BaseImageHandler> imageHandler : imageHandlerMap.entrySet()) {
            ImageScene annotation = AnnotationUtils.findAnnotation(imageHandler.getValue().getClass(), ImageScene.class);
            if (annotation == null) {
                log.warn("图片处理器[{}]未配置图片处理器场景", imageHandler.getKey());
                continue;
            }
            AppSceneEnum value = annotation.value();
            if (!IMAGE_HANDLER_MAP.containsKey(value.name())) {
                IMAGE_HANDLER_MAP.put(value.name(), imageHandler.getValue());
            }
        }
        log.info("初始化图片生成配置处理器完成: IMAGE_HANDLER_MAP: {}", IMAGE_HANDLER_MAP);
    }
}
