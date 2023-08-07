package com.starcloud.ops.business.app.service.channel.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.channel.dto.ShareChannelConfigDTO;
import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelReqVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.convert.channel.AppPublishChannelConverter;
import com.starcloud.ops.business.app.dal.databoject.channel.AppPublishChannelDO;
import com.starcloud.ops.business.app.dal.mysql.channel.AppPublishChannelMapper;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigFactory;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigTemplate;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.channel.AppPublishChannelEnum;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
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
        // 特殊处理 1.分享链接 2.js iframe 3.开放API，如果不存在则创建一个新的发布渠道
        AppValidate.notNull(request.getType(), ErrorCodeConstants.APP_PUBLISH_CHANNEL_TYPE_NOT_NULL);
        List<Integer> types = Arrays.asList(AppPublishChannelEnum.SHARE_LINK.getCode(), AppPublishChannelEnum.JS_IFRAME.getCode(), AppPublishChannelEnum.OPEN_API.getCode());
        if (types.contains(request.getType())) {
            if (Objects.isNull(request.getStatus())) {
                request.setStatus(StateEnum.ENABLE.getCode());
            }
            if (StringUtils.isBlank(request.getUid())) {
                return create(request);
            }
            AppPublishChannelDO appPublishChannel = appPublishChannelMapper.get(request.getUid(), Boolean.TRUE);
            if (Objects.isNull(appPublishChannel)) {
                return create(request);
            }
        }
        // 修改状态
        AppValidate.notBlank(request.getUid(), ErrorCodeConstants.APP_CHANNEL_UID_IS_REQUIRED);
        AppPublishChannelDO appPublishChannel = appPublishChannelMapper.get(request.getUid(), Boolean.TRUE);
        AppValidate.notNull(appPublishChannel, ErrorCodeConstants.APP_CHANNEL_NOT_EXIST, request.getUid());
        appPublishChannel.setStatus(request.getStatus());
        appPublishChannelMapper.updateById(appPublishChannel);
        return AppPublishChannelConverter.INSTANCE.convert(appPublishChannel);
    }

    /**
     * 重置分享链接唯一标识
     *
     * @param uid 应用发布渠道uid
     * @return {@link String}
     */
    @Override
    public String resetShareSlug(String uid) {
        AppValidate.notBlank(uid, ErrorCodeConstants.APP_CHANNEL_UID_IS_REQUIRED);
        AppPublishChannelDO appPublishChannel = appPublishChannelMapper.get(uid, Boolean.TRUE);
        AppValidate.notNull(appPublishChannel, ErrorCodeConstants.APP_CHANNEL_NOT_EXIST, uid);

        ShareChannelConfigDTO shareChannelConfigDTO = new ShareChannelConfigDTO();
        String slug = IdUtil.fastSimpleUUID();
        shareChannelConfigDTO.setSlug(slug);
        appPublishChannel.setConfig(JSON.toJSONString(shareChannelConfigDTO));
        appPublishChannelMapper.updateById(appPublishChannel);
        return slug;
    }

    /**
     * 根据 appUid 批量修改渠道的 publishUid
     *
     * @param appUid     应用 Uid
     * @param publishUid 发布 Uid
     */
    @Override
    public void updatePublishUidByAppUid(String appUid, String publishUid) {
        List<AppPublishChannelDO> publishChannelList = appPublishChannelMapper.listByAppUid(appUid);
        if (CollectionUtil.isEmpty(publishChannelList)) {
            return;
        }
        List<Long> idList = publishChannelList.stream().map(AppPublishChannelDO::getId).collect(Collectors.toList());
        for (Long id : idList) {
            AppPublishChannelDO appPublishChannel = new AppPublishChannelDO();
            appPublishChannel.setId(id);
            appPublishChannel.setPublishUid(publishUid);
            appPublishChannelMapper.updateById(appPublishChannel);
        }
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
