package com.starcloud.ops.business.app.convert.channel;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelReqVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.dal.databoject.channel.AppPublishChannelDO;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigFactory;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigTemplate;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-02
 */
@Mapper
public interface AppPublishChannelConvert {

    AppPublishChannelConvert INSTANCE = Mappers.getMapper(AppPublishChannelConvert.class);

    /**
     * 转换
     *
     * @param request 请求
     * @return {@link AppPublishChannelDO}
     */
    default AppPublishChannelDO convert(AppPublishChannelReqVO request) {
        AppPublishChannelDO appPublishChannel = new AppPublishChannelDO();
        appPublishChannel.setUid(IdUtil.fastSimpleUUID());
        appPublishChannel.setAppUid(request.getAppUid());
        appPublishChannel.setPublishUid(request.getPublishUid());
        appPublishChannel.setName(request.getName());
        appPublishChannel.setType(request.getType());
        appPublishChannel.setMediumUid(request.getMediumUid());
        appPublishChannel.setConfig(JsonUtils.toJsonString(request.getConfig()));
        appPublishChannel.setStatus(Objects.isNull(request.getStatus()) ? StateEnum.DISABLE.getCode() : request.getStatus());
        appPublishChannel.setDescription(request.getDescription());
        appPublishChannel.setDeleted(Boolean.FALSE);
        return appPublishChannel;
    }

    /**
     * 转换
     *
     * @param appPublishChannel {@link AppPublishChannelDO}
     * @return {@link AppPublishChannelRespVO}
     */
    @SuppressWarnings("all")
    default AppPublishChannelRespVO convert(AppPublishChannelDO appPublishChannel) {
        AppPublishChannelRespVO response = new AppPublishChannelRespVO();
        response.setUid(appPublishChannel.getUid());
        response.setAppUid(appPublishChannel.getAppUid());
        response.setPublishUid(appPublishChannel.getPublishUid());
        response.setName(appPublishChannel.getName());
        response.setType(appPublishChannel.getType());
        response.setMediumUid(appPublishChannel.getMediumUid());
        AppPublishChannelConfigFactory factory = SpringUtil.getBean(AppPublishChannelConfigFactory.class);
        AppPublishChannelConfigTemplate handler = factory.getHandler(appPublishChannel.getType());
        response.setConfig(handler.deserializeConfig(appPublishChannel.getConfig()));
        response.setStatus(appPublishChannel.getStatus());
        response.setDescription(appPublishChannel.getDescription());
        response.setCreator(appPublishChannel.getCreator());
        response.setCreateTime(appPublishChannel.getCreateTime());
        response.setUpdateTime(appPublishChannel.getUpdateTime());
        return response;
    }

    List<AppPublishChannelRespVO> convert(List<AppPublishChannelDO> appPublishChannelList);

}
