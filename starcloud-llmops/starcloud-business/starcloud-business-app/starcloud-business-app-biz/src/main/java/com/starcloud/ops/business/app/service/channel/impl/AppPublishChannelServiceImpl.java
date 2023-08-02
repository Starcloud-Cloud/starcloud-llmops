package com.starcloud.ops.business.app.service.channel.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelReqVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.convert.channel.AppPublishChannelConverter;
import com.starcloud.ops.business.app.dal.databoject.channel.AppPublishChannelDO;
import com.starcloud.ops.business.app.dal.mysql.channel.AppPublishChannelMapper;
import com.starcloud.ops.business.app.domain.channel.AppPublishChannelConfigFactory;
import com.starcloud.ops.business.app.domain.channel.AppPublishChannelConfigTemplate;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 应用发布渠道服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-02
 */
@Slf4j
@Service
public class AppPublishChannelServiceImpl implements AppPublishChannelService {

    @Resource
    private AppPublishChannelMapper appPublishChannelMapper;

    @Resource
    private AppPublishChannelConfigFactory appPublishChannelConfigFactory;

    /**
     * 根据应用uid获取发布渠道列表
     *
     * @param appUid 应用uid
     * @return {@link List<AppPublishChannelRespVO>}
     */
    @Override
    public List<AppPublishChannelRespVO> listByAppUid(String appUid) {
        List<AppPublishChannelDO> publishChannelList = appPublishChannelMapper.listByAppUid(appUid);
        return CollectionUtil.emptyIfNull(publishChannelList).stream().map(AppPublishChannelConverter.INSTANCE::convert).collect(Collectors.toList());
    }

    /**
     * 根据 Uid 获取发布渠道详情
     *
     * @param uid uid
     * @return {@link AppPublishChannelRespVO}
     */
    @Override
    public AppPublishChannelRespVO get(String uid) {
        AppPublishChannelDO appPublishChannel = appPublishChannelMapper.get(uid, Boolean.FALSE);
        return AppPublishChannelConverter.INSTANCE.convert(appPublishChannel);
    }

    /**
     * 创建发布渠道
     *
     * @param request {@link AppPublishChannelReqVO}
     * @return {@link AppPublishChannelRespVO}
     */
    @Override
    @SuppressWarnings("all")
    public AppPublishChannelRespVO create(AppPublishChannelReqVO request) {
        // 处理配置信息
        AppPublishChannelConfigTemplate handler = appPublishChannelConfigFactory.getHandler(request.getType());
        request.setConfig(handler.handler(request.getConfig()));

        AppPublishChannelDO appPublishChannel = AppPublishChannelConverter.INSTANCE.convert(request);
        appPublishChannelMapper.insert(appPublishChannel);
        return AppPublishChannelConverter.INSTANCE.convert(appPublishChannel);
    }

    /**
     * 修改发布渠道
     *
     * @param request {@link AppPublishChannelReqVO}
     * @return {@link AppPublishChannelRespVO}
     */
    @Override
    @SuppressWarnings("all")
    public AppPublishChannelRespVO modify(AppPublishChannelReqVO request) {
        AppValidate.notBlank(request.getUid(), ErrorCodeConstants.APP_CHANNEL_UID_IS_REQUIRED);
        AppPublishChannelDO appPublishChannel = appPublishChannelMapper.get(request.getUid(), Boolean.TRUE);
        AppValidate.notNull(appPublishChannel, ErrorCodeConstants.APP_CHANNEL_NOT_EXIST, request.getAppUid());

        // 处理配置信息
        AppPublishChannelConfigTemplate handler = appPublishChannelConfigFactory.getHandler(request.getType());
        request.setConfig(handler.handler(request.getConfig()));

        AppPublishChannelDO updateAppPublishChannel = AppPublishChannelConverter.INSTANCE.convert(request);
        updateAppPublishChannel.setId(appPublishChannel.getId());
        appPublishChannelMapper.updateById(updateAppPublishChannel);
        return AppPublishChannelConverter.INSTANCE.convert(updateAppPublishChannel);
    }

    /**
     * 修改发布渠道状态, 存在修改状态，不存在创建一个新发布渠道
     *
     * @param request {@link AppPublishChannelReqVO}
     * @return {@link AppPublishChannelRespVO}
     */
    @Override
    public AppPublishChannelRespVO changeStatus(AppPublishChannelReqVO request) {
        AppValidate.notBlank(request.getUid(), ErrorCodeConstants.APP_CHANNEL_UID_IS_REQUIRED);
        AppPublishChannelDO appPublishChannel = appPublishChannelMapper.get(request.getUid(), Boolean.TRUE);
        if (Objects.isNull(appPublishChannel)) {
            return create(request);
        }

        AppPublishChannelDO updateAppPublishChannel = AppPublishChannelConverter.INSTANCE.convert(request);
        updateAppPublishChannel.setId(appPublishChannel.getId());
        appPublishChannelMapper.updateById(updateAppPublishChannel);
        return AppPublishChannelConverter.INSTANCE.convert(updateAppPublishChannel);
    }

    /**
     * 根据应用uid删除应用发布渠道记录
     *
     * @param appUid 应用uid
     */
    @Override
    public void deleteByAppUid(String appUid) {
        List<AppPublishChannelDO> publishChannelList = appPublishChannelMapper.listByAppUid(appUid);
        if (CollectionUtil.isEmpty(publishChannelList)) {
            return;
        }
        List<Long> idList = publishChannelList.stream().map(AppPublishChannelDO::getId).collect(Collectors.toList());
        appPublishChannelMapper.deleteBatchIds(idList);
    }

}
