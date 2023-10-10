package com.starcloud.ops.business.app.service.channel.strategy.handler;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.channel.dto.JsIframeChannelConfigDTO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.channel.AppPublishChannelEnum;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigTemplate;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-27
 */
@Component
@AppPublishChannelConfigType(AppPublishChannelEnum.JS_IFRAME)
public class JsIframeChannelConfigHandler extends AppPublishChannelConfigTemplate<JsIframeChannelConfigDTO> {

    /**
     * 校验渠道配置信息
     *
     * @param configUid 渠道配置 UID
     * @param config    渠道配置信息
     */
    @Override
    public void validate(String configUid, JsIframeChannelConfigDTO config) {
        if (Objects.nonNull(config)) {
            // 校验 slug
            if (StringUtils.isBlank(config.getSlug())) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.CHANNEL_CONFIG_SLUG_REQUIRED);
            }
            // 校验 configUid 和 slug 是否一致
            if (!configUid.equals(config.getSlug())) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.CHANNEL_MEDIUM_UID_NE_SLUG);
            }
        }

    }

    /**
     * 处理渠道配置信息
     *
     * @param configUid 渠道配置 UID
     * @param config    渠道配置信息
     * @return 渠道配置信息
     */
    @Override
    public JsIframeChannelConfigDTO handlerConfig(String configUid, JsIframeChannelConfigDTO config) {
        if (Objects.isNull(config)) {
            config = new JsIframeChannelConfigDTO();
            // 生成 slug
            config.setSlug(configUid);
        }
        return config;
    }

    /**
     * 反序列化渠道配置信息
     *
     * @param config 渠道配置信息
     * @return 渠道配置信息
     */
    @Override
    public JsIframeChannelConfigDTO deserializeConfig(String config) {
        return JSONUtil.toBean(config, JsIframeChannelConfigDTO.class);
    }

}
