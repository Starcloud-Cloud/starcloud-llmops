package com.starcloud.ops.business.app.domain.channel;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.channel.dto.JsIframeChannelConfigDTO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.publish.AppPublishChannelEnum;
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
     * 基本校验
     *
     * @param config 配置
     */
    @Override
    public void validate(JsIframeChannelConfigDTO config) {
        if (Objects.nonNull(config) && StringUtils.isBlank(config.getSlug())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_CHANNEL_CONFIG_JS_IFRAME_SLUG_IS_REQUIRED);
        }
    }

    /**
     * 配置处理
     *
     * @param config config
     */
    @Override
    public JsIframeChannelConfigDTO handlerConfig(JsIframeChannelConfigDTO config) {
        if (Objects.isNull(config)) {
            config = new JsIframeChannelConfigDTO();
            // 生成 slug
            config.setSlug(generateSlug());
        }
        return config;
    }

    /**
     * 反序列化配置
     *
     * @param config 配置
     * @return 配置
     */
    @Override
    public JsIframeChannelConfigDTO deserializeConfig(String config) {
        return JSONUtil.toBean(config, JsIframeChannelConfigDTO.class);
    }

    /**
     * 生成 slug
     *
     * @return slug
     */
    private String generateSlug() {
        // 生成 slug
        return IdUtil.fastSimpleUUID();
    }
}
