package com.starcloud.ops.business.promotion.convert.decorate;

import com.starcloud.ops.business.promotion.controller.admin.decorate.vo.DecorateComponentRespVO;
import com.starcloud.ops.business.promotion.controller.admin.decorate.vo.DecorateComponentSaveReqVO;
import com.starcloud.ops.business.promotion.controller.app.decorate.vo.AppDecorateComponentRespVO;
import com.starcloud.ops.business.promotion.dal.dataobject.decorate.DecorateComponentDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DecorateComponentConvert {

    DecorateComponentConvert INSTANCE = Mappers.getMapper(DecorateComponentConvert.class);

    List<DecorateComponentRespVO> convertList02(List<DecorateComponentDO> list);

    DecorateComponentDO convert(DecorateComponentSaveReqVO bean);

    List<AppDecorateComponentRespVO> convertList(List<DecorateComponentDO> list);

}
