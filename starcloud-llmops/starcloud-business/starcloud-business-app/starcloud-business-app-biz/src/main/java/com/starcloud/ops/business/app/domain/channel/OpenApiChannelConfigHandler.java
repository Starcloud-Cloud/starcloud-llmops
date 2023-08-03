package com.starcloud.ops.business.app.domain.channel;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.channel.dto.OpenApiChannelConfigDTO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.publish.AppPublishChannelEnum;
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
     * 基本校验
     *
     * @param config 配置
     */
    @Override
    public void validate(OpenApiChannelConfigDTO config) {
        if (Objects.nonNull(config) && StringUtils.isBlank(config.getApiKey())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_CHANNEL_CONFIG_API_KEY_IS_REQUIRED);
        }
    }

    /**
     * 配置处理
     *
     * @param config config
     */
    @Override
    public OpenApiChannelConfigDTO handlerConfig(OpenApiChannelConfigDTO config) {
        if (Objects.isNull(config)) {
            config = new OpenApiChannelConfigDTO();
            // 生成 apiKey
            config.setApiKey(generateApiKey());
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
    public OpenApiChannelConfigDTO deserializeConfig(String config) {
        return JSONUtil.toBean(config, OpenApiChannelConfigDTO.class);
    }

    /**
     * 生成 apiKey
     *
     * @return apiKey
     */
    private String generateApiKey() {
        // 生成 apiKey
        return "mf-" + IdUtil.simpleUUID();
    }
}
