package com.starcloud.ops.business.order.convert.order;

import com.starcloud.ops.business.order.dal.dataobject.order.TradeOrderLogDO;
import com.starcloud.ops.business.order.service.order.bo.TradeOrderLogCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TradeOrderLogConvert {

    TradeOrderLogConvert INSTANCE = Mappers.getMapper(TradeOrderLogConvert.class);

    TradeOrderLogDO convert(TradeOrderLogCreateReqBO bean);

}
