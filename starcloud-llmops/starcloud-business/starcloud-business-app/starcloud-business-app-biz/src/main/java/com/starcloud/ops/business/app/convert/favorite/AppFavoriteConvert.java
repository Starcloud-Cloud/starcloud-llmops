package com.starcloud.ops.business.app.convert.favorite;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.app.vo.response.config.ChatConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ImageConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.favorite.vo.response.AppFavoriteRespVO;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoritePO;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.util.AppUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-26
 */
@Mapper
public interface AppFavoriteConvert {

    AppFavoriteConvert INSTANCE = Mappers.getMapper(AppFavoriteConvert.class);

    /**
     * AppFavoritePO 转 AppFavoriteRespVO
     *
     * @param favorite 收藏的应用
     * @return 收藏的应用
     */
    default AppFavoriteRespVO convert(AppFavoritePO favorite) {
        AppFavoriteRespVO appFavoriteResponse = new AppFavoriteRespVO();
        appFavoriteResponse.setUid(favorite.getUid());
        appFavoriteResponse.setName(favorite.getName());
        appFavoriteResponse.setModel(favorite.getModel());
        appFavoriteResponse.setVersion(favorite.getVersion());
        appFavoriteResponse.setLanguage(favorite.getLanguage());
        appFavoriteResponse.setTags(AppUtils.split(favorite.getTags()));
        appFavoriteResponse.setCategories(AppUtils.split(favorite.getCategories()));
        appFavoriteResponse.setScenes(AppUtils.splitScenes(favorite.getScenes()));
        appFavoriteResponse.setImages(AppUtils.split(favorite.getImages()));
        appFavoriteResponse.setIcon(favorite.getIcon());
        appFavoriteResponse.setFree(favorite.getFree());
        appFavoriteResponse.setCost(favorite.getCost());
        appFavoriteResponse.setUsageCount(favorite.getUsageCount());
        appFavoriteResponse.setLikeCount(favorite.getLikeCount());
        appFavoriteResponse.setViewCount(favorite.getViewCount());
        appFavoriteResponse.setInstallCount(favorite.getInstallCount());
        appFavoriteResponse.setDescription(favorite.getDescription());
        appFavoriteResponse.setExample(favorite.getExample());
        appFavoriteResponse.setCreateTime(favorite.getCreateTime());
        appFavoriteResponse.setUpdateTime(favorite.getUpdateTime());
        appFavoriteResponse.setFavoriteTime(favorite.getFavoriteTime());

        if (StringUtils.isNotBlank(favorite.getConfig())) {
            if (AppModelEnum.COMPLETION.name().equals(favorite.getModel())) {
                appFavoriteResponse.setWorkflowConfig(JSON.parseObject(favorite.getConfig(), WorkflowConfigRespVO.class));
            } else if (AppModelEnum.CHAT.name().equals(favorite.getModel())) {
                appFavoriteResponse.setChatConfig(JSON.parseObject(favorite.getConfig(), ChatConfigRespVO.class));
            } else if (AppModelEnum.IMAGE.name().equals(favorite.getModel())) {
                appFavoriteResponse.setImageConfig(JSON.parseObject(favorite.getConfig(), ImageConfigRespVO.class));
            }
        }
        
        return appFavoriteResponse;
    }

}
