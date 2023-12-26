package com.starcloud.ops.business.trade.convert.aftersale;

import com.starcloud.ops.business.trade.dal.dataobject.aftersale.AfterSaleLogDO;
import com.starcloud.ops.business.trade.service.aftersale.bo.AfterSaleLogCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AfterSaleLogConvert {

    AfterSaleLogConvert INSTANCE = Mappers.getMapper(AfterSaleLogConvert.class);

    AfterSaleLogDO convert(AfterSaleLogCreateReqBO bean);

}
