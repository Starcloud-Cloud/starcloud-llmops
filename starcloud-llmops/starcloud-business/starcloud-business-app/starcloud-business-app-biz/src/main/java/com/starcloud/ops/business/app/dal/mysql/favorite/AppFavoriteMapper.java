package com.starcloud.ops.business.app.dal.mysql.favorite;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.favorite.vo.query.AppFavoriteListReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.query.AppFavoritePageReqVO;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoriteDO;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoritePO;
import com.starcloud.ops.business.app.enums.favorite.AppFavoriteTypeEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用操作关联 Mapper 接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
@Mapper
public interface AppFavoriteMapper extends BaseMapper<AppFavoriteDO> {

    /**
     * 根据收藏UID查询收藏的应用记录
     *
     * @param uid 收藏UID
     * @return 收藏的应用
     */
    default AppFavoriteDO get(String uid) {
        LambdaQueryWrapper<AppFavoriteDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AppFavoriteDO::getUid, uid);
        return selectOne(wrapper);
    }

    /**
     * 根据应用UID和用户ID查询收藏的应用
     *
     * @param marketUid 应用UID
     * @param userId    用户ID
     * @return 收藏的应用
     */
    default AppFavoriteDO get(String marketUid, String userId) {
        LambdaQueryWrapper<AppFavoriteDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AppFavoriteDO::getMarketUid, marketUid);
        wrapper.eq(AppFavoriteDO::getCreator, userId);
        return selectOne(wrapper);
    }

    /**
     * 根据应用UID和用户ID查询收藏的应用
     *
     * @param marketUid 应用UID
     * @param userId    用户ID
     * @return 收藏的应用
     */
    default AppFavoriteDO get(String marketUid, String userId, String type, String styleUid) {
        LambdaQueryWrapper<AppFavoriteDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AppFavoriteDO::getMarketUid, marketUid);
        wrapper.eq(AppFavoriteDO::getCreator, userId);
        wrapper.eq(AppFavoriteDO::getType, type);
        if (AppFavoriteTypeEnum.TEMPLATE_MARKET.name().equals(type)) {
            wrapper.eq(AppFavoriteDO::getStyleUid, styleUid);
        }
        return selectOne(wrapper);
    }

    /**
     * 根据用户ID和应用UID查询收藏的应用
     *
     * @param uid 用户ID
     * @return 收藏的应用
     */
    AppFavoritePO getMarketInfo(@Param("uid") String uid);

    /**
     * 查询用户收藏的应用列表
     *
     * @param userId 用户ID
     * @return 收藏应用列表
     */
    default List<AppFavoriteDO> listByUserId(String userId, String type) {
        LambdaQueryWrapper<AppFavoriteDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AppFavoriteDO::getCreator, userId);
        wrapper.eq(AppFavoriteDO::getType, type);
        return selectList(wrapper);
    }

    /**
     * 查询用户收藏的应用Map
     *
     * @param userId 用户ID
     * @return 收藏应用Map
     */
    default Map<String, AppFavoriteDO> mapByUserId(String userId, String type) {
        List<AppFavoriteDO> list = listByUserId(userId, type);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(AppFavoriteDO::getMarketUid, Function.identity()));
    }

    /**
     * 查询用户收藏的应用列表，只查询少量字段
     *
     * @param query 搜索条件
     * @return 收藏应用列表
     */
    List<AppFavoritePO> list(@Param("query") AppFavoriteListReqVO query);

    /**
     * 分页查询用户收藏的应用列表
     *
     * @param page  分页参数
     * @param query 搜索条件
     * @return 收藏应用列表
     */
    IPage<AppFavoritePO> page(IPage<AppFavoritePO> page, @Param("query") AppFavoritePageReqVO query);

    /**
     * 根据应用UID删除收藏的应用
     *
     * @param marketUid 应用UID
     */
    default void deleteByMarketUid(String marketUid) {
        LambdaQueryWrapper<AppFavoriteDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AppFavoriteDO::getMarketUid, marketUid);
        delete(wrapper);
    }
}
