package com.starcloud.ops.business.app.convert;

import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.template.dto.TemplateConfigDTO;
import com.starcloud.ops.business.app.api.market.dto.TemplateMarketDTO;
import com.starcloud.ops.business.app.api.market.request.TemplateMarketRequest;
import com.starcloud.ops.business.app.dal.databoject.market.TemplateMarketDO;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 模版市场转换器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@UtilityClass
public class TemplateMarketConvert {

    /**
     * 将 DTO 转换为 DO
     *
     * @param market 模版市场 DTO
     * @return 模版市场 DO
     */
    public static TemplateMarketDO convert(TemplateMarketDTO market) {
        TemplateMarketDO marketDO = new TemplateMarketDO();
        marketDO.setId(market.getId());
        marketDO.setKey(market.getKey());
        marketDO.setName(market.getName());
        marketDO.setType(market.getType());
        marketDO.setLogotype(market.getLogotype());
        marketDO.setSourceType(market.getSourceType());
        marketDO.setVersion(market.getVersion());
        marketDO.setTags(StringUtils.join(market.getTags(), ","));
        marketDO.setCategories(StringUtils.join(market.getCategories(), ","));
        marketDO.setScenes(StringUtils.join(market.getScenes(), ","));

        // 保证 config 中的一些数据和 template 中的一致
        TemplateConfigDTO config = market.getConfig();
        config.setType(market.getType());
        config.setLogotype(market.getLogotype());
        config.setSourceType(market.getSourceType());
        config.setTags(market.getTags());
        config.setCategories(market.getCategories());
        config.setScenes(market.getScenes());
        marketDO.setConfig(JSON.toJSONString(config));

        marketDO.setImages(StringUtils.join(market.getImages(), ","));
        marketDO.setIcon(market.getIcon());
        marketDO.setStepCount(market.getStepCount());
        marketDO.setStepIcons(StringUtils.join(market.getStepIcons(), ","));
        marketDO.setDescription(market.getDescription());
        marketDO.setPromptInfo(market.getPromptInfo());
        marketDO.setCost(market.getCost());
        marketDO.setWord(market.getWord());
        marketDO.setFree(market.getFree());
        marketDO.setLikeCount(market.getLikeCount());
        marketDO.setViewCount(market.getViewCount());
        marketDO.setDownloadCount(market.getDownloadCount());
        marketDO.setPluginLevel(market.getPluginLevel());
        marketDO.setPluginVersion(market.getPluginVersion());
        marketDO.setAudit(market.getAudit());
        marketDO.setStatus(market.getStatus());
        return marketDO;
    }

    /**
     * 将 DO 转换为 DTO
     *
     * @param marketDO 模版市场 DO
     * @return 模版市场 DTO
     */
    public static TemplateMarketDTO convert(TemplateMarketDO marketDO) {
        TemplateMarketDTO market = new TemplateMarketDTO();
        market.setId(marketDO.getId());
        market.setKey(marketDO.getKey());
        market.setName(marketDO.getName());
        market.setType(marketDO.getType());
        market.setLogotype(marketDO.getLogotype());
        market.setSourceType(marketDO.getSourceType());
        market.setVersion(marketDO.getVersion());
        market.setTags(Arrays.asList(StringUtils.split(marketDO.getTags(), ",")));
        market.setCategories(Arrays.asList(StringUtils.split(marketDO.getCategories(), ",")));
        market.setScenes(Arrays.asList(StringUtils.split(marketDO.getScenes(), ",")));
        market.setConfig(JSON.parseObject(marketDO.getConfig(), TemplateConfigDTO.class));
        market.setImages(Arrays.asList(StringUtils.split(marketDO.getImages(), ",")));
        market.setIcon(marketDO.getIcon());
        market.setStepCount(marketDO.getStepCount());
        market.setStepIcons(Arrays.asList(StringUtils.split(marketDO.getStepIcons(), ",")));
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

    public static TemplateMarketDO convertCreate(TemplateMarketRequest request) {
        TemplateMarketDO marketDO = new TemplateMarketDO();

        return marketDO;
    }

    public static TemplateMarketDO convertModify(TemplateMarketRequest request) {
        TemplateMarketDO marketDO = new TemplateMarketDO();

        return marketDO;
    }
}
