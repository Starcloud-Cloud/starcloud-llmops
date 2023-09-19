package com.starcloud.ops.business.share.convert;


import com.starcloud.ops.business.share.controller.admin.vo.ConversationShareResp;
import com.starcloud.ops.business.share.dal.dataobject.ShareConversationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ConversationShareConvert {

    ConversationShareConvert INSTANCE = Mappers.getMapper(ConversationShareConvert.class);


    List<ConversationShareResp> convert(List<ShareConversationDO> shareConversationDOS);

    ConversationShareResp convert(ShareConversationDO shareConversationDO);
}
