package com.starcloud.ops.business.app.convert.coze;

import com.starcloud.ops.business.app.controller.admin.coze.vo.CozeChatReqVO;
import com.starcloud.ops.business.app.feign.dto.coze.CozeChatResult;
import com.starcloud.ops.business.app.feign.request.coze.CozeChatRequest;
import com.starcloud.ops.business.app.model.coze.ChatResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

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

}
