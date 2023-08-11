package com.starcloud.ops.business.app.service.channel.strategy.handler;


import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.channel.dto.JsIframeChannelConfigDTO;
import com.starcloud.ops.business.app.api.channel.dto.WecomGroupChannelConfigDTO;
import com.starcloud.ops.business.app.enums.channel.AppPublishChannelEnum;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigTemplate;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigType;
import org.springframework.stereotype.Component;

@Component
@AppPublishChannelConfigType(AppPublishChannelEnum.WX_WORK)
public class WecomGroupChannelConfigHandler extends AppPublishChannelConfigTemplate<WecomGroupChannelConfigDTO> {


    @Override
    protected void validate(WecomGroupChannelConfigDTO config) {

    }

    @Override
    protected WecomGroupChannelConfigDTO handlerConfig(WecomGroupChannelConfigDTO config) {
        return config;
    }

    @Override
    public WecomGroupChannelConfigDTO deserializeConfig(String config) {
        return JSONUtil.toBean(config, WecomGroupChannelConfigDTO.class);
    }
}
