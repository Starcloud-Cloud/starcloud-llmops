package com.starcloud.ops.business.order.dal.mysql.config;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.order.dal.dataobject.config.TradeConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 交易中心配置 Mapper
 *
 * @author owen
 */
@Mapper
public interface TradeConfigMapper extends BaseMapperX<TradeConfigDO> {

}
