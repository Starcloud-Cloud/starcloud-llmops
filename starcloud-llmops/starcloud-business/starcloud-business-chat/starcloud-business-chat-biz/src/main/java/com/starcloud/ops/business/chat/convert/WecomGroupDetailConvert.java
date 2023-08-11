package com.starcloud.ops.business.chat.convert;

import com.starcloud.ops.business.app.api.channel.dto.WecomGroupChannelConfigDTO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.chat.controller.admin.wecom.vo.response.WecomGroupRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WecomGroupDetailConvert {

    WecomGroupDetailConvert INSTANCE = Mappers.getMapper(WecomGroupDetailConvert.class);

    WecomGroupRespVO convert(WecomGroupChannelConfigDTO channelRespVO);

}
