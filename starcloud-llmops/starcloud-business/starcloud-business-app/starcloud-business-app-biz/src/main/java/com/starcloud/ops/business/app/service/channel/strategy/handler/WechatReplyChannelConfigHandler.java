package com.starcloud.ops.business.app.service.channel.strategy.handler;

import com.starcloud.ops.business.app.api.channel.dto.WeChatReplyChannelConfigDTO;
import com.starcloud.ops.business.app.enums.channel.AppPublishChannelEnum;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigTemplate;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigType;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AppPublishChannelConfigType(AppPublishChannelEnum.WX_MP_REPLY)
public class WechatReplyChannelConfigHandler extends AppPublishChannelConfigTemplate<WeChatReplyChannelConfigDTO> {
    @Override
    protected void validate(String configUid, WeChatReplyChannelConfigDTO config) {

    }

    @Override
    protected WeChatReplyChannelConfigDTO handlerConfig(String configUid, WeChatReplyChannelConfigDTO config) {
        if (Objects.isNull(config)) {
            config = new WeChatReplyChannelConfigDTO();
        }
        return config;
    }

    @Override
    public WeChatReplyChannelConfigDTO deserializeConfig(String config) {
        return new WeChatReplyChannelConfigDTO();
    }
}
