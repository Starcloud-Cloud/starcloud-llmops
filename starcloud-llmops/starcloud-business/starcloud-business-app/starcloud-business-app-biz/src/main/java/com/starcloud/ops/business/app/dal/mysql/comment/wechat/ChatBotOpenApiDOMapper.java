package com.starcloud.ops.business.app.dal.mysql.comment.wechat;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.app.dal.databoject.comment.wechat.ChatBotOpenApiDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatBotOpenApiDOMapper extends BaseMapperX<ChatBotOpenApiDO> {


    default ChatBotOpenApiDO selectOpenApiByIdAndUserId(Long OpenApiId, Long loginUserId) {
        return selectOne(new LambdaQueryWrapperX<ChatBotOpenApiDO>()
                .eq(ChatBotOpenApiDO::getId, OpenApiId)
                .eq(ChatBotOpenApiDO::getUserId, loginUserId));
    }

}
