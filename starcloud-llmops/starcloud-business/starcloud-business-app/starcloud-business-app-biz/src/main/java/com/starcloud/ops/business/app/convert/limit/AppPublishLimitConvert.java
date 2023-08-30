package com.starcloud.ops.business.app.convert.limit;

import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.limit.dto.LimitConfigDTO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitModifyReqVO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitReqVO;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;
import com.starcloud.ops.business.app.dal.databoject.limit.AppPublishLimitDO;
import com.starcloud.ops.business.app.enums.limit.LimitByEnum;
import com.starcloud.ops.business.app.enums.limit.LimitConfigEnum;
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
        LimitConfigDTO rateConfig = (LimitConfigDTO) request.getRateConfig();
        rateConfig.setCode(LimitConfigEnum.RATE.name());
        rateConfig.setLimitBy(LimitByEnum.APP.name());
        appPublishLimit.setRateConfig(JSONUtil.toJsonStr(rateConfig));

        // 用户使用率限流
        LimitConfigDTO userRateConfig = (LimitConfigDTO) request.getUserRateConfig();
        userRateConfig.setCode(LimitConfigEnum.USER_RATE.name());
        userRateConfig.setLimitBy(LimitByEnum.USER.name());
        appPublishLimit.setUserRateConfig(JSONUtil.toJsonStr(userRateConfig));

        // 广告使用率
        LimitConfigDTO advertisingConfig = (LimitConfigDTO) request.getAdvertisingConfig();
        advertisingConfig.setCode(LimitConfigEnum.ADVERTISING.name());
        advertisingConfig.setLimitBy(LimitByEnum.ADVERTISING.name());
        appPublishLimit.setAdvertisingConfig(JSONUtil.toJsonStr(advertisingConfig));

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
        appPublishLimitResponse.setRateConfig(JSONUtil.toBean(appPublishLimit.getRateConfig(), LimitConfigDTO.class));
        appPublishLimitResponse.setUserRateConfig(JSONUtil.toBean(appPublishLimit.getUserRateConfig(), LimitConfigDTO.class));
        appPublishLimitResponse.setAdvertisingConfig(JSONUtil.toBean(appPublishLimit.getAdvertisingConfig(), LimitConfigDTO.class));
        appPublishLimitResponse.setCreator(appPublishLimit.getCreator());
        appPublishLimitResponse.setUpdater(appPublishLimit.getUpdater());
        appPublishLimitResponse.setCreateTime(appPublishLimit.getCreateTime());
        appPublishLimitResponse.setUpdateTime(appPublishLimit.getUpdateTime());
        return appPublishLimitResponse;
    }

}
