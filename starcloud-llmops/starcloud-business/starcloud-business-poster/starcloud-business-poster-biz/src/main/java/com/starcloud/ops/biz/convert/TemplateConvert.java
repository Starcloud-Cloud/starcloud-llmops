package com.starcloud.ops.biz.convert;

import com.starcloud.ops.biz.controller.admin.template.vo.TemplateRespVO;
import com.starcloud.ops.biz.controller.admin.template.vo.TemplateSaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.template.TemplateDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TemplateConvert {

    TemplateConvert INSTANCE = Mappers.getMapper(TemplateConvert.class);

    TemplateDO convert(TemplateSaveReqVO createReqVO);

    TemplateRespVO convert(TemplateDO templateDO);

}
