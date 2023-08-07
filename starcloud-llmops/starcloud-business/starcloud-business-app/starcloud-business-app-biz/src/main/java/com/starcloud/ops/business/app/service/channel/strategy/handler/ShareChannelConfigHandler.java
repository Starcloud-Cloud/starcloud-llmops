package com.starcloud.ops.business.app.service.channel.strategy.handler;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.channel.dto.ShareChannelConfigDTO;
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
@AppPublishChannelConfigType(AppPublishChannelEnum.SHARE_LINK)
public class ShareChannelConfigHandler extends AppPublishChannelConfigTemplate<ShareChannelConfigDTO> {

    /**
     * 基本校验
     *
     * @param config 配置
     */
    @Override
    public void validate(ShareChannelConfigDTO config) {
        if (Objects.nonNull(config) && StringUtils.isBlank(config.getSlug())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_CHANNEL_CONFIG_SHARE_LINK_IS_REQUIRED);
        }
    }

    /**
     * 配置处理
     *
     * @param config config
     */
    @Override
    public ShareChannelConfigDTO handlerConfig(ShareChannelConfigDTO config) {
        if (Objects.isNull(config)) {
            config = new ShareChannelConfigDTO();
            // 生成分享链接
            config.setSlug(generateShareSlug());
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
    public ShareChannelConfigDTO deserializeConfig(String config) {
        return JSONUtil.toBean(config, ShareChannelConfigDTO.class);
    }

    /**
     * 生成分享链接
     *
     * @return 分享链接
     */
    private String generateShareSlug() {
        // 生成分享链接唯一标识
        return IdUtil.fastSimpleUUID();
    }
}
