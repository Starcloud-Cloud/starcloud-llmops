package com.starcloud.ops.business.app.dal.mysql.favorite;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

    List<AppMarketRespVO> listFavorite(@Param("userId") String userId);


}
