package com.starcloud.ops.business.app.convert.publish;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishLatestRespVO;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishRespVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.publish.AppPublishAuditEnum;
import com.starcloud.ops.business.app.util.AppUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

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
        appPublishDO.setModel(appDO.getModel());
        appPublishDO.setVersion(AppConstants.DEFAULT_VERSION);
        appPublishDO.setCategories(appDO.getCategories());
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
        AppPublishRespVO appPublishResponse = new AppPublishRespVO();
        appPublishResponse.setUid(appPublish.getUid());
        appPublishResponse.setAppUid(appPublish.getAppUid());
        appPublishResponse.setMarketUid(appPublish.getMarketUid());
        appPublishResponse.setName(appPublish.getName());
        appPublishResponse.setModel(appPublish.getModel());
        appPublishResponse.setVersion(appPublish.getVersion());
        appPublishResponse.setCategories(AppUtils.split(appPublish.getCategories()));
        appPublishResponse.setLanguage(appPublish.getLanguage());
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
            latestResponse.setModel(app.getModel());
            latestResponse.setDescription(app.getDescription());
            latestResponse.setAppLastUpdateTime(app.getUpdateTime());
        }
        return latestResponse;
    }
}
