package com.starcloud.ops.biz.convert;

import com.starcloud.ops.biz.controller.admin.templatetype.vo.TemplateTypeRespVO;
import com.starcloud.ops.biz.controller.admin.templatetype.vo.TemplateTypeSaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.templatetype.TemplatetypeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TemplatetypeConvert {

    TemplatetypeConvert INSTANCE = Mappers.getMapper(TemplatetypeConvert.class);

    TemplatetypeDO convert(TemplateTypeSaveReqVO createReqVO);

    TemplateTypeRespVO convert(TemplatetypeDO templatetypeDO);
}
