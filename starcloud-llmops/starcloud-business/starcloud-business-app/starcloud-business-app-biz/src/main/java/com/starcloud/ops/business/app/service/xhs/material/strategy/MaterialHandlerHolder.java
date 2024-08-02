package com.starcloud.ops.business.app.service.xhs.material.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.service.xhs.material.strategy.handler.AbstractMaterialHandler;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.service.xhs.material.strategy.handler.DefaultMaterialHandler;
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
public class MaterialHandlerHolder implements ApplicationContextAware {

    /**
     * 资料库处理器映射。key 为发布渠道类型，value 为对应的发布渠道配置处理器
     */
    private static final Map<String, AbstractMaterialHandler> MATERIAL_HANDLER_MAP = new ConcurrentHashMap<>(8);

    
    private static final AbstractMaterialHandler DEFAULT_MATERIAL_HANDLER = SpringUtil.getBean(DefaultMaterialHandler.class);
    
    /**
     * 根据 type 值获取对应的发布渠道配置处理器
     *
     * @param type 发布渠道类型
     * @return 发布渠道配置处理器
     */
    public AbstractMaterialHandler getHandler(String type) {

        return MATERIAL_HANDLER_MAP.getOrDefault(type, DEFAULT_MATERIAL_HANDLER);

//        AppValidate.notBlank(type, CreativeErrorCodeConstants.MATERIAL_TYPE_NOT_EXIST);
//        MaterialTypeEnum materialType = MaterialTypeEnum.of(type);
//
//        if (materialType == null) {
//            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.MATERIAL_TYPE_NOT_EXIST, type);
//        }
//
//        if (MATERIAL_HANDLER_MAP.containsKey(type)) {
//            return MATERIAL_HANDLER_MAP.getOrDefault(type, DEFAULT_MATERIAL_HANDLER);
//        }
//
//        throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.MATERIAL_TYPE_NOT_EXIST, type);
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
        MATERIAL_HANDLER_MAP.clear();
        // 获取所有资料库处理器
        Map<String, AbstractMaterialHandler> matterialHandlerMap = applicationContext.getBeansOfType(AbstractMaterialHandler.class);
        // 遍历资料库处理器，初始化渠道配置映射
        for (Map.Entry<String, AbstractMaterialHandler> matterialHandler : matterialHandlerMap.entrySet()) {
            MaterialType annotation = AnnotationUtils.findAnnotation(matterialHandler.getValue().getClass(), MaterialType.class);
            if (annotation == null) {
                log.warn("资料库处理器[{}]未配置资料库类型！", matterialHandler.getKey());
                continue;
            }
            String typeCode = annotation.value();
            if (!MATERIAL_HANDLER_MAP.containsKey(typeCode)) {
                MATERIAL_HANDLER_MAP.put(typeCode, matterialHandler.getValue());
            }
        }
        log.info("初始化资料库处理器完成: MATERIAL_HANDLER_MAP: {}", MATERIAL_HANDLER_MAP);
    }
}
