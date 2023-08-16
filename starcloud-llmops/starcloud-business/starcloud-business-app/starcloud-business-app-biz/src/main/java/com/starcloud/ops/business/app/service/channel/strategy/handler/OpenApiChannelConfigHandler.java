package com.starcloud.ops.business.app.service.channel.strategy.handler;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.channel.dto.OpenApiChannelConfigDTO;
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
 * @since 2023-08-02
 */
@Component
@AppPublishChannelConfigType(AppPublishChannelEnum.OPEN_API)
public class OpenApiChannelConfigHandler extends AppPublishChannelConfigTemplate<OpenApiChannelConfigDTO> {

    /**
     * 校验渠道配置信息
     *
     * @param configUid 渠道配置 UID
     * @param config    渠道配置信息
     */
    @Override
    public void validate(String configUid, OpenApiChannelConfigDTO config) {
        if (Objects.nonNull(config) && StringUtils.isBlank(config.getApiKey())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_CHANNEL_CONFIG_API_KEY_IS_REQUIRED);
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
    public OpenApiChannelConfigDTO handlerConfig(String configUid, OpenApiChannelConfigDTO config) {
        if (Objects.isNull(config)) {
            config = new OpenApiChannelConfigDTO();
            // 生成 apiKey
            config.setApiKey(generateApiKey(configUid));
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
    public OpenApiChannelConfigDTO deserializeConfig(String config) {
        return JSONUtil.toBean(config, OpenApiChannelConfigDTO.class);
    }

    /**
     * 生成 apiKey
     *
     * @return apiKey
     */
    private String generateApiKey(String configUid) {
        // 生成 apiKey
        return "mf-" + configUid;
    }
}
