package com.starcloud.ops.business.app.service.channel.strategy;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.channel.dto.BaseChannelConfigDTO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-02
 */
@Component
public abstract class AppPublishChannelConfigTemplate<C extends BaseChannelConfigDTO> {

    /**
     * 校验渠道配置信息
     *
     * @param configUid 渠道配置 UID
     * @param config    渠道配置信息
     */
    protected abstract void validate(String configUid, C config);

    /**
     * 处理渠道配置信息
     *
     * @param configUid 渠道配置 UID
     * @param config    渠道配置信息
     */
    protected abstract C handlerConfig(String configUid, C config);

    /**
     * 反序列化渠道配置信息
     *
     * @param config 渠道配置信息
     * @return 渠道配置信息
     */
    public abstract C deserializeConfig(String config);

    /**
     * 处理渠道配置信息
     *
     * @param configUid 渠道配置 UID
     * @param config    渠道配置信息
     * @return 渠道配置信息
     */
    public C handler(String configUid, C config) {
        if (StringUtils.isBlank(configUid)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CHANNEL_MEDIUM_UID_REQUIRED);
        }
        // 校验渠道配置信息
        validate(configUid, config);
        // 处理渠道配置信息
        return handlerConfig(configUid, config);
    }

}
