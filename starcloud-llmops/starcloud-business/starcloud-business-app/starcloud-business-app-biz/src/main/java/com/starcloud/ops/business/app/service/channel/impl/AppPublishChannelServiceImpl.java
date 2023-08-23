package com.starcloud.ops.business.app.service.channel.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.base.vo.request.StatusRequest;
import com.starcloud.ops.business.app.api.channel.dto.ShareChannelConfigDTO;
import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelModifyReqVO;
import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelReqVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.channel.AppPublishChannelConverter;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.channel.AppPublishChannelDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.channel.AppPublishChannelMapper;
import com.starcloud.ops.business.app.dal.mysql.publish.AppPublishMapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.channel.AppPublishChannelEnum;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigFactory;
import com.starcloud.ops.business.app.service.channel.strategy.AppPublishChannelConfigTemplate;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private AppMapper appMapper;

    @Resource
    private AppPublishMapper appPublishMapper;

    @Resource
    private AppPublishChannelMapper appPublishChannelMapper;

    @Resource
    private AppPublishChannelConfigFactory appPublishChannelConfigFactory;

    /**
     * 根据应用 UID 获取发布渠道列表
     *
     * @param appUid 应用uid
     * @return 发布渠道列表
     */
    @Override
    public List<AppPublishChannelRespVO> listByAppUid(String appUid) {
        List<AppPublishChannelDO> publishChannelList = appPublishChannelMapper.listByAppUid(appUid);
        return CollectionUtil.emptyIfNull(publishChannelList).stream().map(AppPublishChannelConverter.INSTANCE::convert).collect(Collectors.toList());
    }

    /**
     * 根据应用发布记录 UID 获取发布渠道列表
     *
     * @param appPublishUid 应用发布记录 UID
     * @return 发布渠道列表
     */
    @Override
    public List<AppPublishChannelRespVO> listByAppPublishUid(String appPublishUid) {
        List<AppPublishChannelDO> publishChannelList = appPublishChannelMapper.listByPublishUid(appPublishUid);
        return CollectionUtil.emptyIfNull(publishChannelList).stream().map(AppPublishChannelConverter.INSTANCE::convert).collect(Collectors.toList());
    }

    /**
     * 根据应用发布记录 UID 获取发布渠道 Map
     *
     * @param appPublishUid 应用发布记录 UID
     * @return 分组后的发布渠道 Map
     */
    @Override
    public Map<Integer, List<AppPublishChannelRespVO>> mapByAppPublishUidGroupByType(String appPublishUid) {
        List<AppPublishChannelDO> publishChannelList = appPublishChannelMapper.listByPublishUid(appPublishUid);

        // 获取分类分组 Map，空集合填充
        Map<Integer, List<AppPublishChannelRespVO>> groupedMap = IEnumable.values(AppPublishChannelEnum.class).stream()
                .collect(Collectors.toMap(AppPublishChannelEnum::getCode, item -> new ArrayList<>()));

        // 填充数据
        for (AppPublishChannelDO channel : publishChannelList) {
            Integer typeCode = channel.getType();
            List<AppPublishChannelRespVO> groupedList = groupedMap.get(typeCode);
            if (groupedList != null) {
                groupedList.add(AppPublishChannelConverter.INSTANCE.convert(channel));
            }
        }

        return groupedMap;
    }

    /**
     * 根据 发布渠道 UID 获取发布渠道详情
     *
     * @param uid uid
     * @return 发布渠道详情
     */
    @Override
    public AppPublishChannelRespVO get(String uid) {
        AppPublishChannelDO appPublishChannel = appPublishChannelMapper.get(uid, Boolean.FALSE);
        return AppPublishChannelConverter.INSTANCE.convert(appPublishChannel);
    }

    /**
     * 根据发布媒介 UID 查找发布渠道详情
     *
     * @param mediumUid 发布媒介 UID
     * @return 发布渠道详情
     */
    @Override
    public AppPublishChannelRespVO getByMediumUid(String mediumUid) {
        AppPublishChannelDO byMediumUid = appPublishChannelMapper.getByMediumUid(mediumUid);
        return AppPublishChannelConverter.INSTANCE.convert(byMediumUid);
    }


    @Override
    public AppPublishChannelRespVO getAllByMediumUid(String mediumUid) {
        AppPublishChannelDO byMediumUid = appPublishChannelMapper.getAllByMediumUid(mediumUid);
        if (byMediumUid == null) {
            return null;
        }
        return AppPublishChannelConverter.INSTANCE.convert(byMediumUid);
    }

    /**
     * 根据发布媒介 UID 查找应用信息
     *
     * @param mediumUid 发布媒介 UID
     * @return 应用信息
     */
    @Override
    public AppRespVO getAppByMediumUid(String mediumUid) {
        // 查询发布渠道
        AppPublishChannelDO appPublishChannel = appPublishChannelMapper.getByMediumUid(mediumUid);
        AppValidate.notNull(appPublishChannel, ErrorCodeConstants.APP_CHANNEL_NOT_EXIST, mediumUid);

        // 查询应用发布记录
        AppPublishDO appPublish = appPublishMapper.get(appPublishChannel.getPublishUid(), Boolean.FALSE);
        AppValidate.notNull(appPublish, ErrorCodeConstants.APP_PUBLISH_NOT_EXISTS_UID, appPublishChannel.getPublishUid());

        if (StringUtils.isBlank(appPublish.getAppInfo())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_NO_EXISTS_UID, appPublish.getAppUid());
        }

        AppDO appDO = JSONUtil.toBean(appPublish.getAppInfo(), AppDO.class);
        if (Objects.isNull(appDO)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_NO_EXISTS_UID, appPublish.getAppUid());
        }

        return AppConvert.INSTANCE.convertResponse(appDO);
    }

    /**
     * 创建发布渠道
     *
     * @param request 请求参数
     * @return 创建的发布渠道详情
     */
    @Override
    @SuppressWarnings("all")
    public void create(AppPublishChannelReqVO request) {
        // 校验应用是否存在
        AppDO app = appMapper.get(request.getAppUid(), Boolean.TRUE);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NO_EXISTS_UID, request.getAppUid());

        // 校验应用发布信息是否存在
        AppPublishDO appPublish = appPublishMapper.get(request.getPublishUid(), Boolean.TRUE);
        AppValidate.notNull(appPublish, ErrorCodeConstants.APP_PUBLISH_NOT_EXISTS_UID, request.getPublishUid());

        // 生成配置信息唯一标识
        String configUid = request.getMediumUid();
        // 如果为空，则生成新的配置 UID
        if (StringUtils.isBlank(configUid)) {
            if (AppPublishChannelEnum.OPEN_API.getCode().equals(request.getType())) {
                configUid = generateOpenApiKey();
            }
            if (AppPublishChannelEnum.WX_WORK.getCode().equals(request.getType())) {
                configUid = generateBindUid();
            } else {
                configUid = generateConfigUid();
            }
        }

        // 处理配置信息
        request.setMediumUid(configUid);
        AppPublishChannelConfigTemplate handler = appPublishChannelConfigFactory.getHandler(request.getType());
        request.setConfig(handler.handler(configUid, request.getConfig()));


        // 创建发布渠道
        AppPublishChannelDO appPublishChannel = AppPublishChannelConverter.INSTANCE.convert(request);
        appPublishChannelMapper.insert(appPublishChannel);
    }

    /**
     * 修改发布渠道
     *
     * @param request 请求参数
     * @return 修改后的发布渠道详情
     */
    @Override
    @SuppressWarnings("all")
    public void modify(AppPublishChannelModifyReqVO request) {
        // 校验发布渠道是否存在
        AppPublishChannelDO appPublishChannel = appPublishChannelMapper.get(request.getUid(), Boolean.TRUE);
        AppValidate.notNull(appPublishChannel, ErrorCodeConstants.APP_CHANNEL_NOT_EXIST, request.getUid());

        // 处理配置信息
        AppPublishChannelConfigTemplate handler = appPublishChannelConfigFactory.getHandler(appPublishChannel.getType());
        request.setConfig(handler.handler(appPublishChannel.getMediumUid(), request.getConfig()));

        // 构建修改发布渠道对象
        AppPublishChannelDO updateAppPublishChannel = new AppPublishChannelDO();
        updateAppPublishChannel.setName(request.getName());
        updateAppPublishChannel.setStatus(request.getStatus());
        updateAppPublishChannel.setConfig(JSON.toJSONString(request.getConfig()));
        updateAppPublishChannel.setDescription(request.getDescription());
        updateAppPublishChannel.setDeleted(Boolean.FALSE);
        updateAppPublishChannel.setId(appPublishChannel.getId());
        updateAppPublishChannel.setUpdater(request.getUpdater());

        // 修改发布渠道
        appPublishChannelMapper.updateById(updateAppPublishChannel);
    }

    /**
     * 重置分享链接唯一标识
     *
     * @param uid 应用发布渠道 UID
     * @return 重置后的分享链接唯一标识
     */
    @Override
    public String resetShareSlug(String uid) {
        // 基础校验
        AppValidate.notBlank(uid, ErrorCodeConstants.APP_CHANNEL_UID_IS_REQUIRED);
        AppPublishChannelDO appPublishChannel = appPublishChannelMapper.get(uid, Boolean.TRUE);
        AppValidate.notNull(appPublishChannel, ErrorCodeConstants.APP_CHANNEL_NOT_EXIST, uid);

        // 生成分享链接唯一标识
        String configUid = generateConfigUid();
        AppPublishChannelDO updateAppPublishChannel = new AppPublishChannelDO();
        updateAppPublishChannel.setId(appPublishChannel.getId());
        updateAppPublishChannel.setMediumUid(configUid);
        // 处理配置信息
        ShareChannelConfigDTO shareChannelConfig = new ShareChannelConfigDTO();
        shareChannelConfig.setSlug(configUid);
        updateAppPublishChannel.setConfig(JSON.toJSONString(shareChannelConfig));

        // 修改发布渠道信息
        appPublishChannelMapper.updateById(updateAppPublishChannel);
        return configUid;
    }

    /**
     * 修改发布渠道状态
     *
     * @param request 请求参数
     * @return 修改状态后的发布渠道详情
     */
    @Override
    @SuppressWarnings("all")
    public void operate(StatusRequest request) {
        // 校验发布渠道是否存在
        AppPublishChannelDO appPublishChannel = appPublishChannelMapper.get(request.getUid(), Boolean.TRUE);
        AppValidate.notNull(appPublishChannel, ErrorCodeConstants.APP_CHANNEL_NOT_EXIST, request.getUid());

        // 修改发布渠道状态
        AppPublishChannelDO updateAppPublishChannel = new AppPublishChannelDO();
        updateAppPublishChannel.setId(appPublishChannel.getId());
        updateAppPublishChannel.setStatus(request.getStatus());
        updateAppPublishChannel.setDeleted(Boolean.FALSE);
        appPublishChannelMapper.updateById(updateAppPublishChannel);
    }

    /**
     * 根据应用 UID 批量修改发布渠道的发布 UID
     *
     * @param appUid     应用 UID
     * @param publishUid 发布 UID
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
     * 根据发布 UID 删除应用发布渠道
     *
     * @param uid 发布 UID
     */
    @Override
    public void delete(String uid) {
        AppPublishChannelDO appPublishChannel = appPublishChannelMapper.get(uid, Boolean.TRUE);
        AppValidate.notNull(appPublishChannel, ErrorCodeConstants.APP_CHANNEL_NOT_EXIST, uid);
        appPublishChannelMapper.deleteById(appPublishChannel.getId());
    }

    /**
     * 根据应用发布记录 UID 删除该发布记录下的所有发布渠道
     *
     * @param publishUid 应用 UID
     */
    @Override
    public void deleteByAppPublishUid(String publishUid) {
        List<AppPublishChannelDO> publishChannelList = appPublishChannelMapper.listByPublishUid(publishUid);
        if (CollectionUtil.isEmpty(publishChannelList)) {
            return;
        }
        List<Long> idList = publishChannelList.stream().map(AppPublishChannelDO::getId).collect(Collectors.toList());
        appPublishChannelMapper.deleteBatchIds(idList);
    }

    /**
     * 根据应用 UID 删除所有发布渠道
     *
     * @param appUid 应用 UID
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

    /**
     * 生成 OpenApi 密钥唯一标识
     *
     * @return 密钥 uid
     */
    private String generateOpenApiKey() {
        return generateConfigUid(32);
    }

    /**
     * 生成配置信息唯一标识
     *
     * @return 配置信息唯一标识
     */
    private String generateConfigUid() {
        return generateConfigUid(9);
    }

    private String generateBindUid() {
        int i = RandomUtil.randomInt(100000, 1000000);
        return "bd" + i;
    }

    /**
     * 生成配置信息唯一标识
     *
     * @param number 随机数位数
     * @return 配置信息唯一标识
     */
    private String generateConfigUid(int number) {
        String uid = generateRandom(number);
        long count = appPublishChannelMapper.countByMediumUid(uid);
        while (count > 0) {
            uid = generateRandom(number);
            count = appPublishChannelMapper.countByMediumUid(uid);
        }
        return uid;
    }

    /**
     * 随机生成配置信息唯一标识
     *
     * @return 配置信息唯一标识
     */
    private String generateRandom(final int count) {
        return RandomStringUtils.random(count, Boolean.TRUE, Boolean.TRUE);
    }

}
