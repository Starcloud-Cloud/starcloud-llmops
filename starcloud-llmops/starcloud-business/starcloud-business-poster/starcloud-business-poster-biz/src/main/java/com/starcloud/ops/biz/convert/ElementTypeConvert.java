package com.starcloud.ops.biz.convert;

import com.starcloud.ops.biz.controller.admin.elementtype.vo.ElementTypeRespVO;
import com.starcloud.ops.biz.controller.admin.elementtype.vo.ElementTypeSaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.elementtype.ElementtypeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ElementTypeConvert {

    ElementTypeConvert INSTANCE = Mappers.getMapper(ElementTypeConvert.class);

    ElementtypeDO convert(ElementTypeSaveReqVO createReqVO);

    ElementTypeRespVO convert(ElementtypeDO elementtypeDO);
}
