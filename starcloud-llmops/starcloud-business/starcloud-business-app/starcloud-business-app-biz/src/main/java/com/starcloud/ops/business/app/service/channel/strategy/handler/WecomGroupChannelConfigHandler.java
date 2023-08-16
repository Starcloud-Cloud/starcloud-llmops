package com.starcloud.ops.business.app.service.channel.strategy.handler;

import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.channel.dto.WecomGroupChannelConfigDTO;
import com.starcloud.ops.business.app.enums.channel.AppPublishChannelEnum;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigTemplate;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigType;
import org.springframework.stereotype.Component;

/**
 * @author fanmiao
 * @version 1.0.0
 * @since 2023-08-02
 */
@Component
@AppPublishChannelConfigType(AppPublishChannelEnum.WX_WORK)
public class WecomGroupChannelConfigHandler extends AppPublishChannelConfigTemplate<WecomGroupChannelConfigDTO> {


    /**
     * 校验渠道配置信息
     *
     * @param configUid 渠道配置 UID
     * @param config    渠道配置信息
     */
    @Override
    protected void validate(String configUid, WecomGroupChannelConfigDTO config) {

    }

    /**
     * 处理渠道配置信息
     *
     * @param configUid 渠道配置 UID
     * @param config    渠道配置信息
     * @return 渠道配置信息
     */
    @Override
    protected WecomGroupChannelConfigDTO handlerConfig(String configUid, WecomGroupChannelConfigDTO config) {
        return config;
    }

    /**
     * 反序列化渠道配置信息
     *
     * @param config 渠道配置信息
     * @return 渠道配置信息
     */
    @Override
    public WecomGroupChannelConfigDTO deserializeConfig(String config) {
        return JSONUtil.toBean(config, WecomGroupChannelConfigDTO.class);
    }
}
