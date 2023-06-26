package com.starcloud.ops.business.app.dal.mysql.favorite;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starcloud.ops.business.app.api.favorite.vo.response.AppFavoriteRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoriteDO;
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
    List<AppFavoriteRespVO> listFavorite(@Param("userId") String userId);

    /**
     * 根据用户ID和应用UID查询收藏的应用
     *
     * @param userId 用户ID
     * @param appUid 应用UID
     * @return 收藏的应用
     */
    AppFavoriteRespVO getFavoriteApp(@Param("userId") String userId, @Param("appUid") String appUid);
}
