package com.starcloud.ops.business.share.convert;

import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.share.controller.admin.vo.AppDetailRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChatAppConvert {

    ChatAppConvert INSTANCE = Mappers.getMapper(ChatAppConvert.class);


    AppDetailRespVO convert(BaseAppEntity appEntity);
}
