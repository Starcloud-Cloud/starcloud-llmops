package com.starcloud.ops.business.app.convert.limit;

import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.limit.dto.LimitConfigDTO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitModifyReqVO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitReqVO;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;
import com.starcloud.ops.business.app.dal.databoject.limit.AppPublishLimitDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-25
 */
@Mapper
public interface AppPublishLimitConvert {

    AppPublishLimitConvert INSTANCE = Mappers.getMapper(AppPublishLimitConvert.class);

    /**
     * 转换
     *
     * @param request 请求
     * @return 数据对象
     */
    default AppPublishLimitDO convert(AppPublishLimitReqVO request) {
        AppPublishLimitDO appPublishLimit = new AppPublishLimitDO();
        appPublishLimit.setAppUid(request.getAppUid());
        appPublishLimit.setPublishUid(request.getPublishUid());
        appPublishLimit.setChannelUid(request.getChannelUid());
        appPublishLimit.setQuotaEnable(request.getQuotaEnable());
        appPublishLimit.setQuotaConfig(JSONUtil.toJsonStr(request.getQuotaConfig()));
        appPublishLimit.setRateEnable(request.getRateEnable());
        appPublishLimit.setRateConfig(JSONUtil.toJsonStr(request.getRateConfig()));
        appPublishLimit.setUserQuotaEnable(request.getUserQuotaEnable());
        appPublishLimit.setUserQuotaConfig(JSONUtil.toJsonStr(request.getUserQuotaConfig()));
        appPublishLimit.setAdvertisingEnable(request.getAdvertisingEnable());
        appPublishLimit.setAdvertisingConfig(JSONUtil.toJsonStr(request.getAdvertisingConfig()));
        appPublishLimit.setDeleted(Boolean.FALSE);
        return appPublishLimit;
    }

    /**
     * 转换
     *
     * @param request 请求
     * @return 数据对象
     */
    default AppPublishLimitDO convertModify(AppPublishLimitModifyReqVO request) {
        AppPublishLimitDO appPublishLimit = convert(request);
        appPublishLimit.setUid(request.getUid());
        return appPublishLimit;
    }

    /**
     * 转换
     *
     * @param appPublishLimit 数据对象
     * @return 响应
     */
    default AppPublishLimitRespVO convert(AppPublishLimitDO appPublishLimit) {
        AppPublishLimitRespVO appPublishLimitResponse = new AppPublishLimitRespVO();
        appPublishLimitResponse.setUid(appPublishLimit.getUid());
        appPublishLimitResponse.setAppUid(appPublishLimit.getAppUid());
        appPublishLimitResponse.setPublishUid(appPublishLimit.getPublishUid());
        appPublishLimitResponse.setChannelUid(appPublishLimit.getChannelUid());
        appPublishLimitResponse.setQuotaEnable(appPublishLimit.getQuotaEnable());
        appPublishLimitResponse.setQuotaConfig(JSONUtil.toBean(appPublishLimit.getQuotaConfig(), LimitConfigDTO.class));
        appPublishLimitResponse.setRateEnable(appPublishLimit.getRateEnable());
        appPublishLimitResponse.setRateConfig(JSONUtil.toBean(appPublishLimit.getRateConfig(), LimitConfigDTO.class));
        appPublishLimitResponse.setUserQuotaEnable(appPublishLimit.getUserQuotaEnable());
        appPublishLimitResponse.setUserQuotaConfig(JSONUtil.toBean(appPublishLimit.getUserQuotaConfig(), LimitConfigDTO.class));
        appPublishLimitResponse.setAdvertisingEnable(appPublishLimit.getAdvertisingEnable());
        appPublishLimitResponse.setAdvertisingConfig(JSONUtil.toBean(appPublishLimit.getAdvertisingConfig(), LimitConfigDTO.class));
        appPublishLimitResponse.setCreator(appPublishLimit.getCreator());
        appPublishLimitResponse.setUpdater(appPublishLimit.getUpdater());
        appPublishLimitResponse.setCreateTime(appPublishLimit.getCreateTime());
        appPublishLimitResponse.setUpdateTime(appPublishLimit.getUpdateTime());
        return appPublishLimitResponse;
    }

}
