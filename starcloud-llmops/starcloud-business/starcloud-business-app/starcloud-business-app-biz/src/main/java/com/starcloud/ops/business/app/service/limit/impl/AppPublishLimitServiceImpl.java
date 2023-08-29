package com.starcloud.ops.business.app.service.limit.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitModifyReqVO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitOperateReqVO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitReqVO;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;
import com.starcloud.ops.business.app.convert.limit.AppPublishLimitConvert;
import com.starcloud.ops.business.app.dal.databoject.limit.AppPublishLimitDO;
import com.starcloud.ops.business.app.dal.mysql.limit.AppPublishLimitMapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.limit.LimitConfigEnum;
import com.starcloud.ops.business.app.service.limit.AppPublishLimitService;
import com.starcloud.ops.business.app.validate.AppValidate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
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

    /**
     * 根据 uid 获取应用发布限流信息
     *
     * @param uid 应用 uid
     * @return 应用发布限流信息
     */
    @Override
    public AppPublishLimitRespVO get(String uid) {
        AppPublishLimitDO appPublishLimit = appPublishLimitMapper.get(uid);
        AppValidate.notNull(appPublishLimit, ErrorCodeConstants.APP_PUBLISH_LIMIT_NOT_EXISTS_UID, uid);
        return AppPublishLimitConvert.INSTANCE.convert(appPublishLimit);
    }

    /**
     * 根据 publishUid 获取应用发布限流信息, 如果不存在则返回默认值
     *
     * @param publishUid 发布 uid
     * @return 应用发布限流信息
     */
    @Override
    public AppPublishLimitRespVO getDefaultIfNull(String publishUid) {
        List<AppPublishLimitDO> list = appPublishLimitMapper.listByPublishUid(publishUid);
        if (CollectionUtil.isEmpty(list)) {
            return getDefaultLimit(publishUid);
        }
        return AppPublishLimitConvert.INSTANCE.convert(list.get(0));
    }

    /**
     * 创建应用发布限流信息
     *
     * @param request 应用发布限流信息
     */
    @Override
    public void create(AppPublishLimitReqVO request) {
        AppPublishLimitDO appPublishLimit = AppPublishLimitConvert.INSTANCE.convert(request);
        if (StringUtils.isBlank(appPublishLimit.getUid())) {
            appPublishLimit.setUid(IdUtil.fastSimpleUUID());
        }
        if (ObjectUtil.isEmpty(appPublishLimit.getQuotaEnable())) {
            appPublishLimit.setQuotaEnable(Boolean.FALSE);
        }
        if (ObjectUtil.isEmpty(appPublishLimit.getRateEnable())) {
            appPublishLimit.setRateEnable(Boolean.FALSE);
        }
        if (ObjectUtil.isEmpty(appPublishLimit.getUserQuotaEnable())) {
            appPublishLimit.setUserQuotaEnable(Boolean.FALSE);
        }
        if (ObjectUtil.isEmpty(appPublishLimit.getAdvertisingEnable())) {
            appPublishLimit.setAdvertisingEnable(Boolean.FALSE);
        }
        appPublishLimitMapper.create(appPublishLimit);
    }

    /**
     * 更新应用发布限流信息
     *
     * @param request 应用发布限流信息
     */
    @Override
    public void modify(AppPublishLimitModifyReqVO request) {
        AppPublishLimitDO appPublishLimit = AppPublishLimitConvert.INSTANCE.convertModify(request);
        appPublishLimitMapper.modify(appPublishLimit);
    }

    /**
     * 操作应用发布限流信息
     *
     * @param request 应用发布限流信息
     */
    @Override
    public void operate(AppPublishLimitOperateReqVO request) {
        AppPublishLimitDO appPublishLimit = appPublishLimitMapper.get(request.getUid());
        AppValidate.notNull(appPublishLimit, ErrorCodeConstants.APP_PUBLISH_LIMIT_NOT_EXISTS_UID, request.getUid());

        AppPublishLimitDO updateLimit = new AppPublishLimitDO();
        updateLimit.setId(appPublishLimit.getId());
        if (LimitConfigEnum.DEFAULT_QUOTA_LIMIT.getOperate().equals(request.getOperate())) {
            updateLimit.setQuotaEnable(request.getEnable());
        } else if (LimitConfigEnum.DEFAULT_RATE_LIMIT.getOperate().equals(request.getOperate())) {
            updateLimit.setRateEnable(request.getEnable());
        } else if (LimitConfigEnum.DEFAULT_USER_QUOTA_LIMIT.getOperate().equals(request.getOperate())) {
            updateLimit.setUserQuotaEnable(request.getEnable());
        } else if (LimitConfigEnum.DEFAULT_ADVERTISING_LIMIT.getOperate().equals(request.getOperate())) {
            updateLimit.setAdvertisingEnable(request.getEnable());
        } else {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_LIMIT_OPERATE_NOT_SUPPORTED, request.getOperate());
        }
        this.appPublishLimitMapper.updateById(updateLimit);
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
    }

    /**
     * 删除应用发布限流信息
     *
     * @param uid 应用 uid
     */
    @Override
    public void delete(String uid) {
        appPublishLimitMapper.delete(uid);
    }

    /**
     * 根据 appUid 删除应用发布限流信息
     *
     * @param appUid 应用 uid
     */
    @Override
    public void deleteByAppUid(String appUid) {
        appPublishLimitMapper.deleteByAppUid(appUid);
    }

    /**
     * 根据 publishUid 删除应用发布限流信息
     *
     * @param publishUid 发布 uid
     */
    @Override
    public void deleteByPublishUid(String publishUid) {
        appPublishLimitMapper.deleteByPublishUid(publishUid);
    }

    /**
     * 获取默认限流配置
     *
     * @return 默认限流配置
     */
    private AppPublishLimitRespVO getDefaultLimit(String publishUid) {
        AppPublishLimitRespVO response = new AppPublishLimitRespVO();
        response.setPublishUid(publishUid);
        response.setQuotaEnable(LimitConfigEnum.DEFAULT_QUOTA_LIMIT.getEnable());
        response.setQuotaConfig(LimitConfigEnum.DEFAULT_QUOTA_LIMIT.getDefaultConfig());
        response.setRateEnable(LimitConfigEnum.DEFAULT_RATE_LIMIT.getEnable());
        response.setRateConfig(LimitConfigEnum.DEFAULT_RATE_LIMIT.getDefaultConfig());
        response.setUserQuotaEnable(LimitConfigEnum.DEFAULT_USER_QUOTA_LIMIT.getEnable());
        response.setUserQuotaConfig(LimitConfigEnum.DEFAULT_USER_QUOTA_LIMIT.getDefaultConfig());
        response.setAdvertisingEnable(LimitConfigEnum.DEFAULT_ADVERTISING_LIMIT.getEnable());
        response.setAdvertisingConfig(LimitConfigEnum.DEFAULT_ADVERTISING_LIMIT.getDefaultConfig());
        return response;
    }
}
