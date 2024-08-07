package com.starcloud.ops.business.app.service.coze;

import com.starcloud.ops.business.app.controller.admin.coze.vo.CozeChatQuery;
import com.starcloud.ops.business.app.controller.admin.coze.vo.CozeChatReqVO;
import com.starcloud.ops.business.app.model.coze.ChatResult;
import com.starcloud.ops.business.app.model.coze.MessageResult;
import org.springframework.validation.annotation.Validated;

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
    ChatResult chat(@Validated CozeChatReqVO request);

    /**
     * 查询扣子机器人会话详情
     *
     * @param query 请求参数
     * @return ChatResult
     */
    ChatResult retrieve(@Validated CozeChatQuery query);

    /**
     * 查询扣子机器执行消息列表
     *
     * @param query 请求参数
     * @return ChatResult
     */
    List<MessageResult> messageList(@Validated CozeChatQuery query);

    /**
     * 获取机器人调用工具的结果
     *
     * @param query 请求参数
     * @return Object
     */
    Object getToolResponse(@Validated CozeChatQuery query);

    /**
     * 获取扣子机器人回答的结果
     *
     * @param query 请求参数
     * @return Object
     */
    Object getAnswer(@Validated CozeChatQuery query);


    void oauth(@Validated String query);


}
