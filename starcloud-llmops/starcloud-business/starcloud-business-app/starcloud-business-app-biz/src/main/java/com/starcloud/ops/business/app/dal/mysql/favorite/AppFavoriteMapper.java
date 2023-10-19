package com.starcloud.ops.business.app.dal.mysql.favorite;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoriteDO;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoritePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
     * 查询用户收藏的应用列表，只查询少量字段
     *
     * @param userId 用户ID
     * @return 应用列表
     */
    List<AppFavoritePO> listFavorite(@Param("userId") String userId);

    /**
     * 根据用户ID和应用UID查询收藏的应用
     *
     * @param userId 用户ID
     * @param appUid 应用UID
     * @return 收藏的应用
     */
    AppFavoritePO getFavoriteApp(@Param("userId") String userId, @Param("appUid") String appUid);

    /**
     * 根据应用UID删除收藏的应用
     *
     * @param marketUid 应用UID
     */
    default void deleteByMarketUid(String marketUid) {
        LambdaQueryWrapper<AppFavoriteDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AppFavoriteDO::getAppUid, marketUid);
        delete(wrapper);
    }
}
