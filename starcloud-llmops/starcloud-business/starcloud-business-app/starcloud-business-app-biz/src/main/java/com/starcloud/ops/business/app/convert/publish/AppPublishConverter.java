package com.starcloud.ops.business.app.convert.publish;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishLatestRespVO;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishRespVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.publish.AppPublishAuditEnum;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-25
 */
@Mapper
public interface AppPublishConverter {

    AppPublishConverter INSTANCE = Mappers.getMapper(AppPublishConverter.class);

    /**
     * 转换应用发布 DO
     *
     * @param appDO 应用 DO
     * @return 应用发布 DO
     */
    default AppPublishDO convert(AppDO appDO) {
        AppPublishDO appPublishDO = new AppPublishDO();
        appPublishDO.setUid(IdUtil.fastSimpleUUID());
        appPublishDO.setAppUid(appDO.getUid());
        appPublishDO.setName(appDO.getName());
        appPublishDO.setType(appDO.getType());
        appPublishDO.setModel(appDO.getModel());
        appPublishDO.setVersion(AppConstants.DEFAULT_VERSION);
        appPublishDO.setSort(appDO.getSort());
        appPublishDO.setCategory(appDO.getCategory());
        appPublishDO.setAppInfo(JSONUtil.toJsonStr(appDO));
        appPublishDO.setDescription(appDO.getDescription());
        appPublishDO.setAudit(AppPublishAuditEnum.UN_PUBLISH.getCode());
        appPublishDO.setDeleted(Boolean.FALSE);
        appPublishDO.setLanguage(AppUtils.detectLanguage(appDO.getName()));
        return appPublishDO;
    }

    /**
     * 转换应用发布响应
     *
     * @param appPublish 应用发布 DO
     * @return 应用发布响应
     */
    default AppPublishRespVO convert(AppPublishDO appPublish) {
        return convert(appPublish, Boolean.FALSE);
    }

    /**
     * 转换应用发布响应
     *
     * @param appPublish 应用发布 DO
     * @return 应用发布响应
     */
    default AppPublishRespVO convert(AppPublishDO appPublish, boolean isNeedSubmitterUser) {
        AppPublishRespVO appPublishResponse = new AppPublishRespVO();
        appPublishResponse.setUid(appPublish.getUid());
        appPublishResponse.setAppUid(appPublish.getAppUid());
        appPublishResponse.setMarketUid(appPublish.getMarketUid());
        appPublishResponse.setName(appPublish.getName());
        appPublishResponse.setType(appPublish.getType());
        appPublishResponse.setModel(appPublish.getModel());
        appPublishResponse.setVersion(appPublish.getVersion());
        appPublishResponse.setCategory(appPublish.getCategory());
        appPublishResponse.setLanguage(appPublish.getLanguage());
        appPublishResponse.setSort(appPublish.getSort());
        appPublishResponse.setUserId(appPublish.getUserId());
        if (isNeedSubmitterUser) {
            appPublishResponse.setSubmitterUser(UserUtils.getUsername(String.valueOf(appPublish.getUserId())));
        }
        String appInfo = appPublish.getAppInfo();
        if (StringUtils.isNotBlank(appInfo)) {
            AppDO appDO = JSONUtil.toBean(appInfo, AppDO.class);
            appPublishResponse.setAppInfo(AppConvert.INSTANCE.convertResponse(appDO));
        }
        appPublishResponse.setDescription(appPublish.getDescription());
        appPublishResponse.setAudit(appPublish.getAudit());
        appPublishResponse.setCreateTime(appPublish.getCreateTime());
        appPublishResponse.setUpdateTime(appPublish.getUpdateTime());
        return appPublishResponse;
    }

    /**
     * 分页转换
     *
     * @param page 分页
     * @return 分页响应
     */
    default PageResp<AppPublishRespVO> convert(Page<AppPublishDO> page) {
        List<AppPublishDO> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return PageResp.of(Collections.emptyList(), 0L, page.getCurrent(), page.getSize());
        }

        // 获取用户昵称
         List<Long> userIds = records.stream().map(AppPublishDO::getUserId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> userMaps = UserUtils.getUserNicknameMapByIds(userIds);

        // 转换
        List<AppPublishRespVO> collect = records.stream().map(this::convert).peek(item -> {
            if (userMaps.containsKey(item.getUserId())) {
                item.setSubmitterUser(userMaps.get(item.getUserId()));
            }
        }).collect(Collectors.toList());
        return PageResp.of(collect, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 转换应用发布最新版本响应
     *
     * @param appPublish 应用发布 DO
     * @return 应用发布最新版本响应
     */
    default AppPublishLatestRespVO convertLatest(AppPublishDO appPublish) {
        AppPublishLatestRespVO latestResponse = new AppPublishLatestRespVO();
        latestResponse.setUid(appPublish.getUid());
        latestResponse.setAppUid(appPublish.getAppUid());
        latestResponse.setName(appPublish.getName());
        latestResponse.setType(appPublish.getType());
        latestResponse.setModel(appPublish.getModel());
        latestResponse.setVersion(appPublish.getVersion());
        latestResponse.setAudit(appPublish.getAudit());
        latestResponse.setDescription(appPublish.getDescription());
        latestResponse.setCreateTime(appPublish.getCreateTime());
        latestResponse.setUpdateTime(appPublish.getUpdateTime());
        latestResponse.setIsFirstCreatePublishRecord(Boolean.FALSE);
        return latestResponse;
    }

    /**
     * 转换应用发布最新版本响应
     *
     * @param appUid 应用 UID
     * @param app    应用最后
     * @return 应用发布最新版本响应
     */
    default AppPublishLatestRespVO convertDefaultUnpublishedLatest(String appUid, AppDO app) {
        AppPublishLatestRespVO latestResponse = new AppPublishLatestRespVO();
        latestResponse.setAppUid(appUid);
        latestResponse.setAudit(AppPublishAuditEnum.UN_PUBLISH.getCode());
        latestResponse.setAuditTag(AppPublishAuditEnum.UN_PUBLISH.getCode());
        latestResponse.setNeedUpdate(Boolean.TRUE);
        latestResponse.setShowPublish(Boolean.TRUE);
        latestResponse.setEnablePublish(Boolean.FALSE);
        latestResponse.setNeedTips(Boolean.TRUE);
        latestResponse.setIsFirstCreatePublishRecord(Boolean.TRUE);
        if (app != null) {
            latestResponse.setName(app.getName());
            latestResponse.setType(app.getType());
            latestResponse.setModel(app.getModel());
            latestResponse.setDescription(app.getDescription());
            latestResponse.setAppLastUpdateTime(app.getUpdateTime());
        }
        return latestResponse;
    }
}
