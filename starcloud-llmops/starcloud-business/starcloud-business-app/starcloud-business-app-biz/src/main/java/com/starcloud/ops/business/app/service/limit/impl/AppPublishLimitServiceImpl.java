package com.starcloud.ops.business.app.service.limit.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitModifyReqVO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitQuery;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitReqVO;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;
import com.starcloud.ops.business.app.convert.limit.AppPublishLimitConvert;
import com.starcloud.ops.business.app.dal.databoject.limit.AppPublishLimitDO;
import com.starcloud.ops.business.app.dal.mysql.limit.AppPublishLimitMapper;
import com.starcloud.ops.business.app.dal.redis.limit.AppPublishLimitRedisMapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.limit.AppLimitConfigEnum;
import com.starcloud.ops.business.app.service.limit.AppPublishLimitService;
import com.starcloud.ops.business.app.api.AppValidate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 应用发布限流服务实现
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-25
 */
@Service
public class AppPublishLimitServiceImpl implements AppPublishLimitService {

    @Resource
    private AppPublishLimitMapper appPublishLimitMapper;

    @Resource
    private AppPublishLimitRedisMapper appPublishLimitRedisMapper;

    /**
     * 根据 uid 获取应用发布限流信息
     *
     * @param uid 应用 uid
     * @return 应用发布限流信息
     */
    @Override
    public AppPublishLimitRespVO get(String uid) {
        AppPublishLimitDO appPublishLimit = appPublishLimitMapper.get(uid);
        AppValidate.notNull(appPublishLimit, ErrorCodeConstants.LIMIT_NON_EXISTENT, uid);
        return AppPublishLimitConvert.INSTANCE.convertResponse(appPublishLimit);
    }

    /**
     * 根据查询条件查询限流信息, 如果不存在则返回默认值
     *
     * @param query 查询条件
     * @return 应用发布限流信息
     */
    @Override
    public AppPublishLimitRespVO defaultIfNull(AppPublishLimitQuery query) {

        // 缓存中取出来
        AppPublishLimitDO appPublishLimitDO = appPublishLimitRedisMapper.get(query.getAppUid());
        if (appPublishLimitDO != null) {
            return AppPublishLimitConvert.INSTANCE.convertResponse(appPublishLimitDO);
        }

        // 查询 DB
        AppPublishLimitDO appPublishLimit = appPublishLimitMapper.get(query);
        if (Objects.isNull(appPublishLimit)) {
            return defaultLimit(query);
        }
        // 重新存入
        appPublishLimitRedisMapper.set(appPublishLimit);
        return AppPublishLimitConvert.INSTANCE.convertResponse(appPublishLimit);
    }

    /**
     * 创建应用发布限流信息
     *
     * @param request 应用发布限流信息
     */
    @Override
    public void create(AppPublishLimitReqVO request) {
        long countByAppUid = appPublishLimitMapper.countByAppUid(request.getAppUid());
        AppValidate.isFalse(countByAppUid > 0, ErrorCodeConstants.LIMIT_ALREADY_EXISTENT);
        long countByPublishUid = appPublishLimitMapper.countByPublishUid(request.getPublishUid());
        AppValidate.isFalse(countByPublishUid > 0, ErrorCodeConstants.LIMIT_ALREADY_EXISTENT);

        AppPublishLimitDO appPublishLimit = AppPublishLimitConvert.INSTANCE.convert(request);
        appPublishLimit.setUid(IdUtil.fastSimpleUUID());
        appPublishLimitMapper.create(appPublishLimit);

        // 重新存入
        appPublishLimitRedisMapper.set(appPublishLimit);
    }

    /**
     * 更新应用发布限流信息
     *
     * @param request 应用发布限流信息
     */
    @Override
    public void modify(AppPublishLimitModifyReqVO request) {
        AppPublishLimitDO appPublishLimit = appPublishLimitMapper.get(request.getUid());
        AppValidate.notNull(appPublishLimit, ErrorCodeConstants.LIMIT_ALREADY_EXISTENT);

        AppPublishLimitDO modifyAppPublishLimit = AppPublishLimitConvert.INSTANCE.convertModify(request);
        modifyAppPublishLimit.setId(appPublishLimit.getId());
        modifyAppPublishLimit.setAppUid(appPublishLimit.getAppUid());
        modifyAppPublishLimit.setPublishUid(appPublishLimit.getPublishUid());
        modifyAppPublishLimit.setChannelUid(null);
        modifyAppPublishLimit.setDeleted(Boolean.FALSE);
        appPublishLimitMapper.modify(modifyAppPublishLimit);

        // 重新存入
        appPublishLimitRedisMapper.set(appPublishLimitMapper.get(request.getUid()));
    }

    /**
     * 根据 appUid 更新发布 uid
     *
     * @param appUid     应用 UID
     * @param publishUid 发布 UID
     */
    @Override
    public void updatePublishUidByAppUid(String appUid, String publishUid) {
        List<AppPublishLimitDO> list = appPublishLimitMapper.listByAppUid(appUid);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<Long> idList = list.stream().map(AppPublishLimitDO::getId).collect(Collectors.toList());
        appPublishLimitMapper.updatePublishUidByIdList(idList, publishUid);
        for (AppPublishLimitDO appPublishLimitDO : list) {
            appPublishLimitRedisMapper.set(appPublishLimitMapper.get(appPublishLimitDO.getUid()));
        }

    }

    /**
     * 删除应用发布限流信息
     *
     * @param uid 应用 uid
     */
    @Override
    public void delete(String uid) {
        AppPublishLimitDO appPublishLimitDO = appPublishLimitMapper.get(uid);
        AppValidate.notNull(appPublishLimitDO, ErrorCodeConstants.LIMIT_NON_EXISTENT);
        appPublishLimitMapper.delete(uid);
        appPublishLimitRedisMapper.delete(appPublishLimitDO.getAppUid());
    }

    /**
     * 根据 appUid 删除应用发布限流信息
     *
     * @param appUid 应用 uid
     */
    @Override
    public void deleteByAppUid(String appUid) {
        appPublishLimitMapper.deleteByAppUid(appUid);
        appPublishLimitRedisMapper.delete(appUid);
    }

    /**
     * 根据 publishUid 删除应用发布限流信息
     *
     * @param publishUid 发布 uid
     */
    @Override
    public void deleteByPublishUid(String publishUid) {
        appPublishLimitMapper.deleteByPublishUid(publishUid);
        List<AppPublishLimitDO> list = appPublishLimitMapper.listByPublishUid(publishUid);
        for (AppPublishLimitDO appPublishLimitDO : list) {
            appPublishLimitRedisMapper.delete(appPublishLimitDO.getAppUid());
        }
    }

    /**
     * 获取默认限流配置
     *
     * @return 默认限流配置
     */
    private AppPublishLimitRespVO defaultLimit(AppPublishLimitQuery query) {
        AppPublishLimitRespVO response = new AppPublishLimitRespVO();
        response.setAppUid(query.getAppUid());
        response.setPublishUid(query.getPublishUid());
        response.setChannelUid(query.getChannelUid());
        response.setRateConfig(AppLimitConfigEnum.RATE.getDefaultConfig());
        response.setUserRateConfig(AppLimitConfigEnum.USER_RATE.getDefaultConfig());
        response.setAdvertisingConfig(AppLimitConfigEnum.ADVERTISING.getDefaultConfig());
        return response;
    }
}
