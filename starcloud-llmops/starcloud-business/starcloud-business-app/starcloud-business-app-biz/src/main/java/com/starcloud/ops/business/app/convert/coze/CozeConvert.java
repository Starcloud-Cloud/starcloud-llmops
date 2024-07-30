package com.starcloud.ops.business.app.convert.coze;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.controller.admin.coze.vo.CozeChatReqVO;
import com.starcloud.ops.business.app.feign.dto.coze.CozeChatResult;
import com.starcloud.ops.business.app.feign.dto.coze.CozeMessageResult;
import com.starcloud.ops.business.app.feign.request.coze.CozeChatRequest;
import com.starcloud.ops.business.app.model.coze.ChatResult;
import com.starcloud.ops.business.app.model.coze.MessageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Mapper
public interface CozeConvert {

    CozeConvert INSTANCE = Mappers.getMapper(CozeConvert.class);

    /**
     * 讲请求转为扣子请求实体
     *
     * @param request 请求参数
     * @return CozeChatRequest
     */
    CozeChatRequest convert(CozeChatReqVO request);

    /**
     * 将扣子返回结果转为 ChatResult
     *
     * @param cozeChatResult 扣子返回结果
     * @return ChatResult
     */
    ChatResult convert(CozeChatResult cozeChatResult);

    /**
     * 将扣子返回结果转为 MessageResult
     *
     * @param cozeMessageResult 扣子返回结果
     * @return MessageResult
     */
    MessageResult convert(CozeMessageResult cozeMessageResult);

    /**
     * 将扣子返回结果转为 MessageResult
     *
     * @param messages 扣子返回结果
     * @return MessageResult
     */
    default List<MessageResult> convert(List<CozeMessageResult> messages) {
        return CollectionUtil.emptyIfNull(messages).stream()
                .map(this::convert).collect(Collectors.toList());
    }
}
