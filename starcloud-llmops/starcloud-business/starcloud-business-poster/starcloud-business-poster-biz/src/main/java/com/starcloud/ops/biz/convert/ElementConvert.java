package com.starcloud.ops.biz.convert;

import com.starcloud.ops.biz.controller.admin.element.vo.ElementRespVO;
import com.starcloud.ops.biz.controller.admin.element.vo.ElementSaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.element.ElementDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ElementConvert {

    ElementConvert INSTANCE = Mappers.getMapper(ElementConvert.class);

    ElementDO convert(ElementSaveReqVO createReqVO);

    ElementRespVO convert(ElementDO element);
}
