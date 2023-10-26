package com.starcloud.ops.business.app.service.channel.strategy.handler;

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
     * 校验渠道配置信息
     *
     * @param configUid 渠道配置 UID
     * @param config    渠道配置信息
     */
    @Override
    public void validate(String configUid, ShareChannelConfigDTO config) {
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
    public ShareChannelConfigDTO handlerConfig(String configUid, ShareChannelConfigDTO config) {
        if (Objects.isNull(config)) {
            config = new ShareChannelConfigDTO();
            // 生成分享链接
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
    public ShareChannelConfigDTO deserializeConfig(String config) {
        return JSONUtil.toBean(config, ShareChannelConfigDTO.class);
    }

}
