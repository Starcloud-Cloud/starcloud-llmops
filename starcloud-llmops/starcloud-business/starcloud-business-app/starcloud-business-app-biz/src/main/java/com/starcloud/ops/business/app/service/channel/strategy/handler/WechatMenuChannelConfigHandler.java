package com.starcloud.ops.business.app.service.channel.strategy.handler;

import com.starcloud.ops.business.app.api.channel.dto.WeChatMenuChannelConfigDTO;
import com.starcloud.ops.business.app.enums.channel.AppPublishChannelEnum;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigTemplate;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigType;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AppPublishChannelConfigType(AppPublishChannelEnum.WX_MP_MENU)
public class WechatMenuChannelConfigHandler extends AppPublishChannelConfigTemplate<WeChatMenuChannelConfigDTO> {
    @Override
    protected void validate(String configUid, WeChatMenuChannelConfigDTO config) {

    }

    @Override
    protected WeChatMenuChannelConfigDTO handlerConfig(String configUid, WeChatMenuChannelConfigDTO config) {
        if (Objects.isNull(config)) {
            config = new WeChatMenuChannelConfigDTO();
        }
        return config;
    }

    @Override
    public WeChatMenuChannelConfigDTO deserializeConfig(String config) {
        return new WeChatMenuChannelConfigDTO();
    }
}
