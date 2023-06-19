package com.starcloud.ops.business.app.convert.market;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.app.dto.AppChatConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppConfigDTO;
import com.starcloud.ops.business.app.api.app.request.AppPublishRequest;
import com.starcloud.ops.business.app.api.market.dto.AppMarketDTO;
import com.starcloud.ops.business.app.api.market.request.AppMarketRequest;
import com.starcloud.ops.business.app.api.market.request.AppMarketUpdateRequest;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.market.AppMarketAuditEnum;
import com.starcloud.ops.business.app.enums.market.AppMarketFreeEnum;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.business.app.util.market.AppMarketUtils;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import com.starcloud.ops.business.app.validate.market.AppMarketValidate;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 模版市场转换器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@UtilityClass
public class AppMarketConvert {

    /**
     * 将 DO 转换为 DTO
     *
     * @param marketDO 模版市场 DO
     * @return 模版市场 DTO
     */
    public static AppMarketDTO convert(AppMarketDO marketDO) {
        AppMarketDTO market = new AppMarketDTO();
        market.setUid(marketDO.getUid());
        market.setName(marketDO.getName());
        market.setModel(marketDO.getModel());
        market.setVersion(Optional.ofNullable(marketDO.getVersion()).orElse(AppConstants.DEFAULT_VERSION));
        market.setLanguage(marketDO.getLanguage());
        market.setTags(AppUtils.split(marketDO.getTags()));
        market.setCategories(AppUtils.split(marketDO.getCategories()));
        market.setScenes(AppUtils.splitScenes(marketDO.getScenes()));
        market.setImages(AppUtils.split(marketDO.getImages()));
        market.setIcon(marketDO.getIcon());
        market.setFree(marketDO.getFree());
        market.setCost(marketDO.getCost());
        market.setWord(marketDO.getWord());
        market.setLikeCount(marketDO.getLikeCount());
        market.setViewCount(marketDO.getViewCount());
        market.setDownloadCount(marketDO.getDownloadCount());
        market.setDescription(marketDO.getDescription());
        market.setExample(marketDO.getExample());
        market.setAudit(marketDO.getAudit());
        market.setStatus(marketDO.getStatus());
        market.setCreator(marketDO.getCreator());
        market.setUpdater(marketDO.getUpdater());
        market.setCreateTime(marketDO.getCreateTime());
        market.setUpdateTime(marketDO.getUpdateTime());
        market.setDeleted(marketDO.getDeleted());
        market.setTenantId(marketDO.getTenantId());
        if (AppModelEnum.COMPLETION.name().equals(marketDO.getModel())) {
            market.setConfig(JSON.parseObject(marketDO.getConfig(), AppConfigDTO.class));
        } else if (AppModelEnum.CHAT.name().equals(marketDO.getModel())) {
            market.setChatConfig(JSON.parseObject(marketDO.getConfig(), AppChatConfigDTO.class));
        } else {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MODEL_IS_UNKNOWN, marketDO.getModel());
        }
        return market;
    }

    /**
     * 将生成模版市场的请求，转换为 DO
     *
     * @param request 生成模版市场的请求
     * @return 模版市场 DO
     */
    public static AppMarketDO convertCreate(AppMarketRequest request) {

        AppMarketValidate.validate(request);
        AppMarketUtils.buildRequest(request);

        AppMarketDO market = new AppMarketDO();
        market.setName(request.getName());
        market.setModel(request.getModel());
        market.setVersion(AppConstants.DEFAULT_VERSION);
        market.setLanguage(request.getLanguage());
        market.setTags(AppUtils.join(request.getTags()));
        market.setCategories(AppUtils.join(request.getCategories()));
        market.setScenes(AppUtils.joinScenes(request.getScenes()));
        market.setImages(AppUtils.join(request.getImages()));
        market.setIcon(request.getIcon());
        market.setFree(request.getFree());
        market.setCost(request.getCost());
        market.setLikeCount(Optional.ofNullable(request.getLikeCount()).orElse(0));
        market.setViewCount(Optional.ofNullable(request.getViewCount()).orElse(0));
        market.setDownloadCount(Optional.ofNullable(request.getDownloadCount()).orElse(0));
        market.setDescription(request.getDescription());
        market.setExample("");
        market.setAudit(AppMarketAuditEnum.PENDING.getCode());
        market.setStatus(StateEnum.ENABLE.getCode());
        market.setDeleted(Boolean.FALSE);
        if (AppModelEnum.COMPLETION.name().equals(request.getModel())) {
            AppConfigDTO config = request.getConfig();
            market.setConfig(JSON.toJSONString(config));
            market.setWord(AppUtils.buildWord(config));
        } else if (AppModelEnum.CHAT.name().equals(request.getModel())) {
            AppChatConfigDTO chatConfig = request.getChatConfig();
            market.setConfig(JSON.toJSONString(chatConfig));
        } else {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MODEL_IS_UNKNOWN, request.getModel());
        }

        return market;
    }

    /**
     * 将修改请求转换为 DO
     *
     * @param request 修改请求
     * @return 模版市场 DO
     */
    public static AppMarketDO convertModify(AppMarketUpdateRequest request) {
        AppValidate.assertNotNull(request, "App Market Update Request Data");
        Assert.notBlank(request.getUid(), () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_IS_REQUIRED, "uid"));
        AppMarketDO market = convertCreate(request);
        // 修改时，版本号加 1
        market.setVersion(request.getVersion() + 1);
        market.setStatus(null);
        market.setDeleted(null);
        market.setAudit(null);
        return market;
    }

    /**
     * 将发布请求转换为 DO
     *
     * @param request 发布请求
     * @return 模版市场 DO
     */
    public static AppMarketDO convertPublish(AppPublishRequest request) {
        AppValidate.assertNotNull(request, "App Market Publish Request Data");
        Assert.notBlank(request.getUid(), () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_IS_REQUIRED, "uid"));

        AppValidate.validate(request);
        AppUtils.buildRequest(request);

        AppMarketDO market = new AppMarketDO();
        market.setName(request.getName());
        market.setModel(request.getModel());
        market.setVersion(AppConstants.DEFAULT_VERSION);
        market.setLanguage(request.getLanguage());
        market.setTags(AppUtils.join(request.getTags()));
        market.setCategories(AppUtils.join(request.getCategories()));
        market.setScenes(AppUtils.joinScenes(request.getScenes()));
        market.setImages(AppUtils.join(request.getImages()));
        market.setIcon(request.getIcon());
        market.setFree(AppMarketFreeEnum.FREE.getCode());
        market.setCost(BigDecimal.ZERO);
        market.setDescription(request.getDescription());
        market.setExample("");
        market.setAudit(AppMarketAuditEnum.PENDING.getCode());
        market.setStatus(StateEnum.ENABLE.getCode());
        market.setDeleted(Boolean.FALSE);
        if (AppModelEnum.COMPLETION.name().equals(request.getModel())) {
            AppConfigDTO config = request.getConfig();
            market.setConfig(JSON.toJSONString(config));
            market.setWord(AppUtils.buildWord(config));
        } else if (AppModelEnum.CHAT.name().equals(request.getModel())) {
            AppChatConfigDTO chatConfig = request.getChatConfig();
            market.setConfig(JSON.toJSONString(chatConfig));
        } else {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MODEL_IS_UNKNOWN, request.getModel());
        }

        return market;
    }
}
