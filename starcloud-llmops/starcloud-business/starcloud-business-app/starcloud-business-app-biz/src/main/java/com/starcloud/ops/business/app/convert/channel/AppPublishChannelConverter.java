package com.starcloud.ops.business.app.convert.channel;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelReqVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.dal.databoject.channel.AppPublishChannelDO;
import com.starcloud.ops.business.app.domain.channel.AppPublishChannelConfigFactory;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-02
 */
@Mapper
public interface AppPublishChannelConverter {

    AppPublishChannelConverter INSTANCE = Mappers.getMapper(AppPublishChannelConverter.class);

    /**
     * 转换
     *
     * @param request 请求
     * @return {@link AppPublishChannelDO}
     */
    default AppPublishChannelDO convert(AppPublishChannelReqVO request) {
        AppPublishChannelDO appPublishChannel = new AppPublishChannelDO();
        appPublishChannel.setUid(StringUtils.isBlank(request.getUid()) ? IdUtil.fastSimpleUUID() : request.getUid());
        appPublishChannel.setAppUid(request.getAppUid());
        appPublishChannel.setPublishUid(request.getPublishUid());
        appPublishChannel.setType(request.getType());
        appPublishChannel.setConfig(JSONUtil.toJsonStr(request.getConfig()));
        appPublishChannel.setStatus(Objects.isNull(request.getStatus()) ? StateEnum.DISABLE.getCode() : request.getStatus());
        return appPublishChannel;
    }

    /**
     * 转换
     *
     * @param appPublishChannel {@link AppPublishChannelDO}
     * @return {@link AppPublishChannelRespVO}
     */
    default AppPublishChannelRespVO convert(AppPublishChannelDO appPublishChannel) {
        AppPublishChannelRespVO response = new AppPublishChannelRespVO();
        response.setUid(appPublishChannel.getUid());
        response.setAppUid(appPublishChannel.getAppUid());
        response.setPublishUid(appPublishChannel.getPublishUid());
        response.setType(appPublishChannel.getType());
        AppPublishChannelConfigFactory factory = SpringUtil.getBean(AppPublishChannelConfigFactory.class);
        response.setConfig(factory.getHandler(appPublishChannel.getType()).deserializeConfig(appPublishChannel.getConfig()));
        response.setStatus(appPublishChannel.getStatus());
        response.setDescription(appPublishChannel.getDescription());
        response.setCreateTime(appPublishChannel.getCreateTime());
        response.setUpdateTime(appPublishChannel.getUpdateTime());
        return response;
    }

}
