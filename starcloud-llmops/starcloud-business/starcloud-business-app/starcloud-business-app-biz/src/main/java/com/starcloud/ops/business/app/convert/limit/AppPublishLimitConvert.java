package com.starcloud.ops.business.app.convert.limit;

import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.limit.dto.AppLimitRuleDTO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppLimitRuleReqVO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitModifyReqVO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitReqVO;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;
import com.starcloud.ops.business.app.dal.databoject.limit.AppPublishLimitDO;
import com.starcloud.ops.business.app.enums.limit.AppLimitByEnum;
import com.starcloud.ops.business.app.enums.limit.AppLimitRuleEnum;
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
        AppLimitRuleDTO appLimitRule = limitRuleConverter(AppLimitRuleEnum.APP_RATE.name(), AppLimitByEnum.APP.name(), request.getAppLimitRule());
        appPublishLimit.setAppLimitRule(JSONUtil.toJsonStr(appLimitRule));
        // 用户使用率限流
        AppLimitRuleDTO userLimitRule = limitRuleConverter(AppLimitRuleEnum.USER_RATE.name(), AppLimitByEnum.USER.name(), request.getUserLimitRule());
        appPublishLimit.setUserLimitRule(JSONUtil.toJsonStr(userLimitRule));
        // 出现广告间隔
        AppLimitRuleDTO advertisingRule = limitRuleConverter(AppLimitRuleEnum.ADVERTISING.name(), AppLimitByEnum.ADVERTISING.name(), request.getAdvertisingRule());
        appPublishLimit.setAdvertisingRule(JSONUtil.toJsonStr(advertisingRule));
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
        appPublishLimitResponse.setAppLimitRule(JSONUtil.toBean(appPublishLimit.getAppLimitRule(), AppLimitRuleDTO.class));
        appPublishLimitResponse.setUserLimitRule(JSONUtil.toBean(appPublishLimit.getUserLimitRule(), AppLimitRuleDTO.class));
        appPublishLimitResponse.setAdvertisingRule(JSONUtil.toBean(appPublishLimit.getAdvertisingRule(), AppLimitRuleDTO.class));
        appPublishLimitResponse.setTenantId(appPublishLimit.getTenantId());
        appPublishLimitResponse.setCreator(appPublishLimit.getCreator());
        appPublishLimitResponse.setUpdater(appPublishLimit.getUpdater());
        appPublishLimitResponse.setCreateTime(appPublishLimit.getCreateTime());
        appPublishLimitResponse.setUpdateTime(appPublishLimit.getUpdateTime());
        return appPublishLimitResponse;
    }

    /**
     * 转换为 AppLimitRuleDTO
     *
     * @param code    编码
     * @param limitBy 限流依据
     * @param request 请求
     * @return AppLimitRuleDTO
     */
    default AppLimitRuleDTO limitRuleConverter(String code, String limitBy, AppLimitRuleReqVO request) {
        AppLimitRuleDTO config = new AppLimitRuleDTO();
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
