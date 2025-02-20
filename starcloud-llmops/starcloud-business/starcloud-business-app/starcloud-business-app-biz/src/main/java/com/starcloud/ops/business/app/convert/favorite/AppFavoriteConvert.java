package com.starcloud.ops.business.app.convert.favorite;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starcloud.ops.business.app.api.app.vo.response.config.ChatConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ImageConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.favorite.vo.request.AppFavoriteCreateReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.response.AppFavoriteRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.MarketStyle;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoriteDO;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoritePO;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.enums.favorite.AppFavoriteTypeEnum;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.PinyinCache;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        AppFavoriteRespVO response = new AppFavoriteRespVO();
        response.setUid(favorite.getUid());
        response.setName(favorite.getName());
        response.setSpell(PinyinCache.get(favorite.getName()));
        response.setSpellSimple(PinyinCache.getSimple(favorite.getName()));
        response.setType(favorite.getType());
        response.setModel(favorite.getModel());
        response.setVersion(favorite.getVersion());
        response.setLanguage(favorite.getLanguage());
        response.setSort(favorite.getSort());
        response.setTags(AppUtils.split(favorite.getTags()));
        response.setCategory(favorite.getCategory());
        response.setScenes(AppUtils.splitScenes(favorite.getScenes()));
        response.setImages(AppUtils.split(favorite.getImages()));
        response.setIcon(favorite.getIcon());
        response.setFree(favorite.getFree());
        response.setCost(favorite.getCost());
        response.setUsageCount(favorite.getUsageCount());
        response.setLikeCount(favorite.getLikeCount());
        response.setViewCount(favorite.getViewCount());
        response.setInstallCount(favorite.getInstallCount());
        response.setDescription(favorite.getDescription());
        response.setExample(favorite.getExample());
        response.setCreator(favorite.getCreator());
        response.setCreateTime(favorite.getCreateTime());
        response.setUpdateTime(favorite.getUpdateTime());

        response.setIsFavorite(Boolean.TRUE);
        response.setFavoriteUid(favorite.getFavoriteUid());
        response.setFavoriteCreator(favorite.getFavoriteCreator());
        response.setFavoriteTime(favorite.getFavoriteTime());
        response.setStyleUid(favorite.getStyleUid());
        response.setFavoriteType(favorite.getFavoriteType());

        if (StringUtils.isNotBlank(favorite.getConfig())) {
            if (AppModelEnum.COMPLETION.name().equals(favorite.getModel())) {
                response.setWorkflowConfig(JsonUtils.parseObject(favorite.getConfig(), WorkflowConfigRespVO.class));
            } else if (AppModelEnum.CHAT.name().equals(favorite.getModel())) {
                response.setChatConfig(JsonUtils.parseObject(favorite.getConfig(), ChatConfigRespVO.class));
            } else if (AppModelEnum.IMAGE.name().equals(favorite.getModel())) {
                response.setImageConfig(JsonUtils.parseObject(favorite.getConfig(), ImageConfigRespVO.class));
            }
        }
        if (AppTypeEnum.MEDIA_MATRIX.name().equalsIgnoreCase(favorite.getType())) {
            if (StringUtils.isNotBlank(favorite.getStyles())) {
                response.setStyles(JsonUtils.parseArray(favorite.getStyles(), MarketStyle.class));
            }
        }

        return response;
    }

    /**
     * AppFavoritePO 列表转 AppFavoriteRespVO 列表
     *
     * @param list 收藏的应用列表
     * @return 收藏的应用列表
     */
    default List<AppFavoriteRespVO> convertList(List<AppFavoritePO> list, String type) {
        return list.stream()
                .map(this::convert)
                .peek(response -> {
                    if (AppFavoriteTypeEnum.TEMPLATE_MARKET.name().equals(type)) {
                        String styleUid = response.getStyleUid();
                        if (StringUtils.isBlank(styleUid)) {
                            return;
                        }
                        List<MarketStyle> styles = response.getStyles();
                        MarketStyle style = CollectionUtils.emptyIfNull(styles)
                                .stream().filter(item -> styleUid.equals(item.getUuid()))
                                .findFirst()
                                .orElse(null);

                        if (Objects.nonNull(style)) {
                            response.setStyle(style);
                            response.setWorkflowConfig(null);
                            response.setStyles(null);
                        }
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * IPage<AppFavoritePO> 转 PageResp<AppFavoriteRespVO>
     *
     * @param page 收藏的应用分页列表
     * @return 收藏的应用分页列表
     */
    default PageResp<AppFavoriteRespVO> convertPage(IPage<AppFavoritePO> page, String type) {
        List<AppFavoritePO> records = page.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return PageResp.of(Collections.emptyList(), 0L, page.getCurrent(), page.getSize());
        }
        List<AppFavoriteRespVO> collect = convertList(records, type);
        return PageResp.of(collect, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * AppFavoriteCreateReqVO 转 AppFavoriteDO
     *
     * @param request 请求参数
     * @return 收藏的应用
     */
    default AppFavoriteDO convertRequest(AppFavoriteCreateReqVO request, String loginUserId) {
        AppFavoriteDO favorite = new AppFavoriteDO();
        favorite.setUid(IdUtil.fastSimpleUUID());
        favorite.setMarketUid(request.getMarketUid());
        favorite.setType(request.getType());
        favorite.setStyleUid(request.getStyleUid());
        favorite.setDeleted(Boolean.FALSE);
        favorite.setCreator(loginUserId);
        favorite.setUpdater(loginUserId);
        favorite.setCreateTime(LocalDateTime.now());
        favorite.setUpdateTime(LocalDateTime.now());
        return favorite;
    }

}
