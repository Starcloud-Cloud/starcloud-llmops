package com.starcloud.ops.business.app.service.channel.strategy;

import com.starcloud.ops.business.app.api.channel.dto.BaseChannelConfigDTO;
import org.springframework.stereotype.Component;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-02
 */
@Component
public abstract class AppPublishChannelConfigTemplate<C extends BaseChannelConfigDTO> {

    /**
     * 基本校验
     *
     * @param config 配置
     */
    protected abstract void validate(C config);

    /**
     * 配置处理
     *
     * @param config config
     */
    protected abstract C handlerConfig(C config);

    /**
     * 反序列化配置
     *
     * @param config 配置
     * @return 配置
     */
    public abstract C deserializeConfig(String config);

    /**
     * 处理配置
     *
     * @param config 配置
     * @return 配置
     */
    public C handler(C config) {
        validate(config);
        return handlerConfig(config);
    }

}
