package com.starcloud.ops.business.app.service.coze;

import com.starcloud.ops.business.app.controller.admin.coze.vo.CozeChatQuery;
import com.starcloud.ops.business.app.controller.admin.coze.vo.CozeChatReqVO;
import com.starcloud.ops.business.app.model.coze.ChatResult;
import com.starcloud.ops.business.app.model.coze.MessageResult;

import java.util.List;

/**
 * 扣子服务
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
public interface CozeService {


    /**
     * 扣子机器人聊天
     *
     * @param request 请求参数
     * @return ChatResult
     */
    ChatResult chat(CozeChatReqVO request);

    /**
     * 查询扣子机器执行会话执行情况
     *
     * @param query 请求参数
     * @return ChatResult
     */
    ChatResult retrieve(CozeChatQuery query);

    /**
     * 查询扣子机器执行消息列表
     *
     * @param query 请求参数
     * @return ChatResult
     */
    List<MessageResult> messageList(CozeChatQuery query);

}
