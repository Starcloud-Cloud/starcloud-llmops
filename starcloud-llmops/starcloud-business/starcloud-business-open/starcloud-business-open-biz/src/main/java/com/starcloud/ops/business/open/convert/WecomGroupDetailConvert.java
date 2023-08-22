package com.starcloud.ops.business.open.convert;

import com.starcloud.ops.business.app.api.channel.dto.WecomGroupChannelConfigDTO;
import com.starcloud.ops.business.open.controller.admin.vo.response.WecomGroupRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WecomGroupDetailConvert {

    WecomGroupDetailConvert INSTANCE = Mappers.getMapper(WecomGroupDetailConvert.class);

    WecomGroupRespVO convert(WecomGroupChannelConfigDTO channelRespVO);

}
