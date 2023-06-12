package com.starcloud.ops.business.app.dal.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starcloud.ops.business.app.dal.databoject.AppMarketDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 模版市场表 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2023-06-05
 */
@Mapper
public interface AppMarketMapper extends BaseMapper<AppMarketDO> {

}
