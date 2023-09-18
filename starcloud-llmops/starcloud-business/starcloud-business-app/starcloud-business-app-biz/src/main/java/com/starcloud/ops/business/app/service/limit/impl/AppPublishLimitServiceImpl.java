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
import com.starcloud.ops.business.app.service.limit.AppPublishLimitService;
import com.starcloud.ops.business.app.validate.AppValidate;
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
        AppValidate.notNull(appPublishLimit, ErrorCodeConstants.APP_PUBLISH_LIMIT_NOT_EXISTS_UID, uid);
        return AppPublishLimitConvert.INSTANCE.convertResponse(appPublishLimit);
    }

    /**
     * 根据查询条件查询限流信息, 如果不存在则返回默认值
     *
     * @param query 查询条件
     * @return 应用发布限流信息
     */
    @Override
    public AppPublishLimitRespVO get(AppPublishLimitQuery query) {

        // 缓存中取出来
        AppPublishLimitDO appPublishLimitDO = appPublishLimitRedisMapper.get(query.getAppUid());
        if (Objects.nonNull(appPublishLimitDO)) {
            return AppPublishLimitConvert.INSTANCE.convertResponse(appPublishLimitDO);
        }

        // 查询 DB
        AppPublishLimitDO appPublishLimit = appPublishLimitMapper.get(query);
        AppValidate.notNull(appPublishLimit, ErrorCodeConstants.APP_PUBLISH_LIMIT_EXISTS);

        // 重新存入
        appPublishLimitRedisMapper.put(appPublishLimit);
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
        AppValidate.isFalse(countByAppUid > 0, ErrorCodeConstants.APP_PUBLISH_LIMIT_EXISTS);
        long countByPublishUid = appPublishLimitMapper.countByPublishUid(request.getPublishUid());
        AppValidate.isFalse(countByPublishUid > 0, ErrorCodeConstants.APP_PUBLISH_LIMIT_EXISTS);

        AppPublishLimitDO appPublishLimit = AppPublishLimitConvert.INSTANCE.convert(request);
        appPublishLimit.setUid(IdUtil.fastSimpleUUID());
        appPublishLimitMapper.create(appPublishLimit);

        // 重新存入
        appPublishLimitRedisMapper.put(appPublishLimit);
    }

    /**
     * 更新应用发布限流信息
     *
     * @param request 应用发布限流信息
     */
    @Override
    public void modify(AppPublishLimitModifyReqVO request) {
        AppPublishLimitDO appPublishLimit = appPublishLimitMapper.get(request.getUid());
        AppValidate.notNull(appPublishLimit, ErrorCodeConstants.APP_PUBLISH_LIMIT_EXISTS);

        AppPublishLimitDO modifyAppPublishLimit = AppPublishLimitConvert.INSTANCE.convertModify(request);
        modifyAppPublishLimit.setId(appPublishLimit.getId());
        modifyAppPublishLimit.setAppUid(appPublishLimit.getAppUid());
        modifyAppPublishLimit.setPublishUid(appPublishLimit.getPublishUid());
        modifyAppPublishLimit.setChannelUid(null);
        modifyAppPublishLimit.setDeleted(Boolean.FALSE);
        appPublishLimitMapper.modify(modifyAppPublishLimit);

        // 重新存入
        appPublishLimitRedisMapper.put(appPublishLimitMapper.get(request.getUid()));
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
        List<AppPublishLimitDO> collect = appPublishLimitMapper.listByAppUid(appUid);

        appPublishLimitRedisMapper.putAll(collect);
    }

    /**
     * 删除应用发布限流信息
     *
     * @param uid 应用 uid
     */
    @Override
    public void delete(String uid) {
        AppPublishLimitDO appPublishLimitDO = appPublishLimitMapper.get(uid);
        AppValidate.notNull(appPublishLimitDO, ErrorCodeConstants.APP_PUBLISH_LIMIT_NOT_EXISTS_UID);
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
        appPublishLimitRedisMapper.deleteByAppUidList(list.stream().map(AppPublishLimitDO::getAppUid).collect(Collectors.toList()));
    }
}
