package com.starcloud.ops.business.app.convert.market;

import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.app.vo.response.config.ChatConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketReqVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.entity.config.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.util.app.AppUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 模版市场转换器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Mapper
public interface AppMarketConvert {

    /**
     * AppMarketConvert 实例
     */
    AppMarketConvert INSTANCE = Mappers.getMapper(AppMarketConvert.class);

    /**
     * AppMarketReqVO 转 AppMarketEntity
     *
     * @param appMarketRequest AppMarketReqVO
     * @return AppMarketEntity
     */
    AppMarketEntity convert(AppMarketReqVO appMarketRequest);

    /**
     * AppMarketEntity 转 AppMarketDO
     *
     * @param appMarketEntity AppMarketEntity
     * @return AppMarketDO
     */
    default AppMarketDO convert(AppMarketEntity appMarketEntity) {
        AppMarketDO appMarket = new AppMarketDO();
        appMarket.setUid(appMarketEntity.getUid());
        appMarket.setName(appMarketEntity.getName());
        appMarket.setModel(appMarketEntity.getModel());
        appMarket.setVersion(appMarketEntity.getVersion());
        appMarket.setLanguage(appMarketEntity.getLanguage());
        appMarket.setTags(AppUtils.join(appMarketEntity.getTags()));
        appMarket.setCategories(AppUtils.join(appMarketEntity.getCategories()));
        appMarket.setScenes(AppUtils.joinScenes(appMarketEntity.getScenes()));
        appMarket.setImages(AppUtils.join(appMarketEntity.getImages()));
        appMarket.setIcon(appMarketEntity.getIcon());
        appMarket.setFree(appMarketEntity.getFree());
        appMarket.setCost(appMarketEntity.getCost());
        appMarket.setLikeCount(appMarketEntity.getLikeCount());
        appMarket.setViewCount(appMarketEntity.getViewCount());
        appMarket.setDownloadCount(appMarketEntity.getDownloadCount());
        appMarket.setDescription(appMarketEntity.getDescription());
        appMarket.setExample(appMarketEntity.getExample());

        if (AppModelEnum.COMPLETION.name().equals(appMarket.getModel())) {
            appMarket.setConfig(JSON.toJSONString(appMarketEntity.getWorkflowConfig()));
        } else if (AppModelEnum.CHAT.name().equals(appMarket.getModel())) {
            appMarket.setConfig(JSON.toJSONString(appMarketEntity.getChatConfig()));
        }
        return appMarket;
    }

    /**
     * AppMarketDO 转 AppMarketEntity
     *
     * @param appMarket AppMarketDO
     * @return AppMarketEntity
     */
    default AppMarketEntity convert(AppMarketDO appMarket) {
        AppMarketEntity appMarketEntity = new AppMarketEntity();
        appMarketEntity.setUid(appMarket.getUid());
        appMarketEntity.setName(appMarket.getName());
        appMarketEntity.setModel(appMarket.getModel());
        appMarketEntity.setVersion(appMarket.getVersion());
        appMarketEntity.setLanguage(appMarket.getLanguage());
        appMarketEntity.setTags(AppUtils.split(appMarket.getTags()));
        appMarketEntity.setCategories(AppUtils.split(appMarket.getCategories()));
        appMarketEntity.setScenes(AppUtils.splitScenes(appMarket.getScenes()));
        appMarketEntity.setImages(AppUtils.split(appMarket.getImages()));
        appMarketEntity.setIcon(appMarket.getIcon());
        appMarketEntity.setFree(appMarket.getFree());
        appMarketEntity.setCost(appMarket.getCost());
        appMarketEntity.setLikeCount(appMarket.getLikeCount());
        appMarketEntity.setViewCount(appMarket.getViewCount());
        appMarketEntity.setDownloadCount(appMarket.getDownloadCount());
        appMarketEntity.setDescription(appMarket.getDescription());
        appMarketEntity.setExample(appMarket.getExample());

        if (AppModelEnum.COMPLETION.name().equals(appMarket.getModel())) {
            appMarketEntity.setWorkflowConfig(JSON.parseObject(appMarket.getConfig(), WorkflowConfigEntity.class));
        } else if (AppModelEnum.CHAT.name().equals(appMarket.getModel())) {
            appMarketEntity.setChatConfig(JSON.parseObject(appMarket.getConfig(), ChatConfigEntity.class));
        }

        return appMarketEntity;
    }

    /**
     * AppMarketDO 转 AppMarketRespVO
     *
     * @param appMarket AppMarketDO
     * @return AppMarketRespVO
     */
    default AppMarketRespVO convertResp(AppMarketDO appMarket) {
        AppMarketRespVO appMarketResponse = new AppMarketRespVO();
        appMarketResponse.setUid(appMarket.getUid());
        appMarketResponse.setName(appMarket.getName());
        appMarketResponse.setModel(appMarket.getModel());
        appMarketResponse.setVersion(appMarket.getVersion());
        appMarketResponse.setLanguage(appMarket.getLanguage());
        appMarketResponse.setTags(AppUtils.split(appMarket.getTags()));
        appMarketResponse.setCategories(AppUtils.split(appMarket.getCategories()));
        appMarketResponse.setScenes(AppUtils.splitScenes(appMarket.getScenes()));
        appMarketResponse.setImages(AppUtils.split(appMarket.getImages()));
        appMarketResponse.setIcon(appMarket.getIcon());
        appMarketResponse.setFree(appMarket.getFree());
        appMarketResponse.setCost(appMarket.getCost());
        appMarketResponse.setLikeCount(appMarket.getLikeCount());
        appMarketResponse.setViewCount(appMarket.getViewCount());
        appMarketResponse.setDownloadCount(appMarket.getDownloadCount());
        appMarketResponse.setDescription(appMarket.getDescription());
        appMarketResponse.setExample(appMarket.getExample());
        appMarketResponse.setCreateTime(appMarket.getCreateTime());
        appMarketResponse.setUpdateTime(appMarket.getUpdateTime());

        if (AppModelEnum.COMPLETION.name().equals(appMarket.getModel())) {
            appMarketResponse.setWorkflowConfig(JSON.parseObject(appMarket.getConfig(), WorkflowConfigRespVO.class));
        } else if (AppModelEnum.CHAT.name().equals(appMarket.getModel())) {
            appMarketResponse.setChatConfig(JSON.parseObject(appMarket.getConfig(), ChatConfigRespVO.class));
        }

        return appMarketResponse;
    }

}
