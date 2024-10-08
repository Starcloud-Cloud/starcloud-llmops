package com.starcloud.ops.business.app.convert.limit;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.api.limit.dto.AppLimitConfigDTO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppLimitConfigReqVO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitModifyReqVO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitReqVO;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;
import com.starcloud.ops.business.app.dal.databoject.limit.AppPublishLimitDO;
import com.starcloud.ops.business.app.enums.limit.AppLimitByEnum;
import com.starcloud.ops.business.app.enums.limit.AppLimitConfigEnum;
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
        // 应用使用率限流
        AppLimitConfigDTO rate = convertConfig(AppLimitConfigEnum.RATE.name(), AppLimitByEnum.APP.name(), request.getRateConfig());
        appPublishLimit.setRateConfig(JsonUtils.toJsonString(rate));
        // 用户使用率限流
        AppLimitConfigDTO userRate = convertConfig(AppLimitConfigEnum.USER_RATE.name(), AppLimitByEnum.USER.name(), request.getUserRateConfig());
        appPublishLimit.setUserRateConfig(JsonUtils.toJsonString(userRate));
        // 广告使用率
        AppLimitConfigDTO advertising = convertConfig(AppLimitConfigEnum.ADVERTISING.name(), AppLimitByEnum.ADVERTISING.name(), request.getAdvertisingConfig());
        appPublishLimit.setAdvertisingConfig(JsonUtils.toJsonString(advertising));
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
    default AppPublishLimitRespVO convertResponse(AppPublishLimitDO appPublishLimit) {
        AppPublishLimitRespVO appPublishLimitResponse = new AppPublishLimitRespVO();
        appPublishLimitResponse.setUid(appPublishLimit.getUid());
        appPublishLimitResponse.setAppUid(appPublishLimit.getAppUid());
        appPublishLimitResponse.setPublishUid(appPublishLimit.getPublishUid());
        appPublishLimitResponse.setChannelUid(appPublishLimit.getChannelUid());
        appPublishLimitResponse.setRateConfig(JsonUtils.parseObject(appPublishLimit.getRateConfig(), AppLimitConfigDTO.class));
        appPublishLimitResponse.setUserRateConfig(JsonUtils.parseObject(appPublishLimit.getUserRateConfig(), AppLimitConfigDTO.class));
        appPublishLimitResponse.setAdvertisingConfig(JsonUtils.parseObject(appPublishLimit.getAdvertisingConfig(), AppLimitConfigDTO.class));
        appPublishLimitResponse.setTenantId(appPublishLimit.getTenantId());
        appPublishLimitResponse.setCreator(appPublishLimit.getCreator());
        appPublishLimitResponse.setUpdater(appPublishLimit.getUpdater());
        appPublishLimitResponse.setCreateTime(appPublishLimit.getCreateTime());
        appPublishLimitResponse.setUpdateTime(appPublishLimit.getUpdateTime());
        return appPublishLimitResponse;
    }

    /**
     * 转换为 LimitConfigDTO
     *
     * @param code    编码
     * @param limitBy 限流依据
     * @param request 请求
     * @return LimitConfigDTO
     */
    default AppLimitConfigDTO convertConfig(String code, String limitBy, AppLimitConfigReqVO request) {
        AppLimitConfigDTO config = new AppLimitConfigDTO();
        config.setCode(code);
        config.setLimitBy(limitBy);
        config.setEnable(request.getEnable());
        config.setThreshold(request.getThreshold());
        config.setTimeInterval(request.getTimeInterval());
        config.setTimeUnit(request.getTimeUnit());
        config.setMessage(request.getMessage());
        return config;
    }

}
