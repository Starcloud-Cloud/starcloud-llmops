package com.starcloud.ops.business.app.convert;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.market.dto.AppMarketDTO;
import com.starcloud.ops.business.app.api.market.request.AppMarketRequest;
import com.starcloud.ops.business.app.api.market.request.AppMarketUpdateRequest;
import com.starcloud.ops.business.app.api.app.dto.AppConfigDTO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.AppResultCode;
import com.starcloud.ops.business.app.exception.AppMarketException;
import com.starcloud.ops.business.app.util.AppUtil;
import com.starcloud.ops.business.app.validate.AppValidate;
import lombok.experimental.UtilityClass;

import java.util.List;
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
        // id 为 null，防止前端使用
        market.setId(null);
        market.setUid(marketDO.getUid());
        market.setName(marketDO.getName());
        market.setType(marketDO.getType());
        market.setLogotype(marketDO.getLogotype());
        market.setSourceType(marketDO.getSourceType());
        market.setVersion(Optional.ofNullable(marketDO.getVersion()).orElse(AppConstants.DEFAULT_VERSION));
        market.setTags(AppUtil.buildField(marketDO.getTags()));
        market.setCategories(AppUtil.buildField(marketDO.getCategories()));
        market.setScenes(AppUtil.buildScenes(marketDO.getScenes()));
        market.setConfig(JSON.parseObject(marketDO.getConfig(), AppConfigDTO.class));
        market.setImages(AppUtil.buildField(marketDO.getImages()));
        market.setIcon(marketDO.getIcon());
        market.setStepCount(marketDO.getStepCount());
        market.setStepIcons(AppUtil.buildField(marketDO.getStepIcons()));
        market.setDescription(marketDO.getDescription());
        market.setPromptInfo(marketDO.getPromptInfo());
        market.setCost(marketDO.getCost());
        market.setWord(marketDO.getWord());
        market.setFree(marketDO.getFree());
        market.setLikeCount(marketDO.getLikeCount());
        market.setViewCount(marketDO.getViewCount());
        market.setDownloadCount(marketDO.getDownloadCount());
        market.setPluginLevel(marketDO.getPluginLevel());
        market.setPluginVersion(marketDO.getPluginVersion());
        market.setAudit(marketDO.getAudit());
        market.setStatus(marketDO.getStatus());
        market.setCreator(marketDO.getCreator());
        market.setUpdater(marketDO.getUpdater());
        market.setCreateTime(marketDO.getCreateTime());
        market.setUpdateTime(marketDO.getUpdateTime());
        market.setDeleted(marketDO.getDeleted());
        market.setTenantId(marketDO.getTenantId());
        return market;
    }

    public static AppMarketDO convertCreate(AppMarketRequest request) {

        // 基础校验和数据处理
        Assert.notNull(request, () -> AppMarketException.exception(AppResultCode.TEMPLATE_MARKET_DATA_IS_NULL, "TemplateMarketRequest"));
        String name = AppValidate.validateName(request.getName());
        String type = AppValidate.validateType(request.getType());
        String logotype = AppValidate.validateLogotype(request.getLogotype());
        String sourceType = AppValidate.validateSourceType(request.getSourceType());
        AppConfigDTO config = AppValidate.validateConfig(request.getConfig());
        String tags = AppUtil.buildField(request.getTags());
        String categories = AppUtil.buildField(request.getCategories());
        String scenes = AppUtil.buildScenes(request.getScenes());

        AppMarketDO market = new AppMarketDO();
        market.setName(name);
        market.setType(type);
        market.setLogotype(logotype);
        market.setSourceType(sourceType);
        market.setVersion(Optional.ofNullable(request.getVersion()).orElse(AppConstants.DEFAULT_VERSION));
        market.setTags(tags);
        market.setCategories(categories);
        market.setScenes(scenes);
        market.setLanguage(request.getLanguage());
        market.setImages(AppUtil.buildField(request.getImages()));
        market.setIcon(request.getIcon());
        market.setStepIcons(AppUtil.buildStepIcons(config));
        market.setStepCount(Optional.ofNullable(config.getSteps()).map(List::size).orElse(0));
        market.setDescription(request.getDescription());
        market.setPromptInfo("");

        market.setCost(request.getCost());
        market.setWord(AppUtil.buildWord(config));
        market.setFree(request.getFree());
        market.setLikeCount(Optional.ofNullable(request.getLikeCount()).orElse(0));
        market.setViewCount(Optional.ofNullable(request.getViewCount()).orElse(0));
        market.setDownloadCount(Optional.ofNullable(request.getDownloadCount()).orElse(0));
        market.setPluginLevel(request.getPluginLevel());
        market.setPluginVersion(request.getPluginVersion());
        market.setAudit(request.getAudit());
        market.setStatus(request.getStatus());

        // 保证 config 中的一些数据和 template 中的一致
        config.setType(type);
        config.setLogotype(logotype);
        config.setSourceType(sourceType);
        config.setTags(AppUtil.buildField(tags));
        config.setCategories(AppUtil.buildField(categories));
        config.setScenes(AppUtil.buildScenes(scenes));
        market.setConfig(JSON.toJSONString(config));

        return market;
    }

    /**
     * 将修改请求转换为 DO
     *
     * @param request 修改请求
     * @return 模版市场 DO
     */
    public static AppMarketDO convertModify(AppMarketUpdateRequest request) {
        AppMarketDO market = new AppMarketDO();
        Assert.notNull(request, () -> AppMarketException.exception(AppResultCode.TEMPLATE_MARKET_DATA_IS_NULL, "TemplateMarketUpdateRequest"));
        market.setUid(request.getUid());
        return convertCreate(request);
    }
}
