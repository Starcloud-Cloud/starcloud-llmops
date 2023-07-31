package com.starcloud.ops.business.app.convert.publish;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishRespVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.publish.AppPublishAuditEnum;
import com.starcloud.ops.business.app.util.app.AppUtils;
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
        return appPublishDO;
    }

    /**
     * 转换应用发布响应
     *
     * @param appPublishDO 应用发布 DO
     * @return 应用发布响应
     */
    default AppPublishRespVO convert(AppPublishDO appPublishDO) {
        AppPublishRespVO appPublishResponse = new AppPublishRespVO();
        appPublishResponse.setUid(appPublishDO.getUid());
        appPublishResponse.setAppUid(appPublishDO.getAppUid());
        appPublishResponse.setMarketUid(appPublishDO.getMarketUid());
        appPublishResponse.setName(appPublishDO.getName());
        appPublishResponse.setModel(appPublishDO.getModel());
        appPublishResponse.setVersion(appPublishDO.getVersion());
        appPublishResponse.setCategories(AppUtils.split(appPublishDO.getCategories()));
        appPublishResponse.setLanguage(appPublishDO.getLanguage());
        String appInfo = appPublishDO.getAppInfo();
        if (StringUtils.isNotBlank(appInfo)) {
            AppDO appDO = JSONUtil.toBean(appInfo, AppDO.class);
            appPublishResponse.setAppInfo(AppConvert.INSTANCE.convertResponse(appDO));
        }
        appPublishResponse.setDescription(appPublishDO.getDescription());
        appPublishResponse.setAudit(appPublishDO.getAudit());
        appPublishResponse.setCreateTime(appPublishDO.getCreateTime());
        appPublishResponse.setUpdateTime(appPublishDO.getUpdateTime());
        return appPublishResponse;
    }
}
