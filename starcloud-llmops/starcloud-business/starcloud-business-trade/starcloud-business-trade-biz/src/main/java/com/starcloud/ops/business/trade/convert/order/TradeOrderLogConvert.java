package com.starcloud.ops.business.trade.convert.order;

import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderLogDO;
import com.starcloud.ops.business.trade.service.order.bo.TradeOrderLogCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TradeOrderLogConvert {

    TradeOrderLogConvert INSTANCE = Mappers.getMapper(TradeOrderLogConvert.class);

    TradeOrderLogDO convert(TradeOrderLogCreateReqBO bean);

}
