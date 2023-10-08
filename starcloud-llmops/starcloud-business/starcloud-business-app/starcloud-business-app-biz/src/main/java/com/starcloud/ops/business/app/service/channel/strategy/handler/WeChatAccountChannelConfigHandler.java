package com.starcloud.ops.business.app.service.channel.strategy.handler;


import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.channel.dto.WeChatAccountChannelConfigDTO;
import com.starcloud.ops.business.app.api.channel.dto.WecomGroupChannelConfigDTO;
import com.starcloud.ops.business.app.enums.channel.AppPublishChannelEnum;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigTemplate;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigType;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AppPublishChannelConfigType(AppPublishChannelEnum.WX_MP)
public class WeChatAccountChannelConfigHandler extends AppPublishChannelConfigTemplate<WeChatAccountChannelConfigDTO> {
    @Override
    protected void validate(String configUid, WeChatAccountChannelConfigDTO config) {

    }

    @Override
    protected WeChatAccountChannelConfigDTO handlerConfig(String configUid, WeChatAccountChannelConfigDTO config) {
        if (Objects.isNull(config)) {
            config = new WeChatAccountChannelConfigDTO();
            // 生成 群备注
        }
        return config;
    }

    @Override
    public WeChatAccountChannelConfigDTO deserializeConfig(String config) {
        return JSONUtil.toBean(config, WeChatAccountChannelConfigDTO.class);
    }
}
