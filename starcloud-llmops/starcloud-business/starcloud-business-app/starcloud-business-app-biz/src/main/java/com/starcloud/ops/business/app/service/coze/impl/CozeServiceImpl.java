package com.starcloud.ops.business.app.service.coze.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.starcloud.ops.business.app.controller.admin.coze.vo.CozeChatQuery;
import com.starcloud.ops.business.app.controller.admin.coze.vo.CozeChatReqVO;
import com.starcloud.ops.business.app.convert.coze.CozeConvert;
import com.starcloud.ops.business.app.feign.CozeClient;
import com.starcloud.ops.business.app.feign.dto.coze.CozeChatResult;
import com.starcloud.ops.business.app.feign.dto.coze.CozeMessageResult;
import com.starcloud.ops.business.app.feign.request.coze.CozeChatRequest;
import com.starcloud.ops.business.app.feign.response.CozeResponse;
import com.starcloud.ops.business.app.model.coze.ChatResult;
import com.starcloud.ops.business.app.model.coze.MessageResult;
import com.starcloud.ops.business.app.service.coze.CozeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Slf4j
@Service
public class CozeServiceImpl implements CozeService {

    public static final String TOOL_RESPONSE = "tool_response";

    public static final String ANSWER = "answer";

    @Resource
    private CozeClient cozeClient;

    /**
     * 扣子机器人聊天
     *
     * @param request 请求参数
     * @return ChatResult
     */
    @Override
    public ChatResult chat(CozeChatReqVO request) {
        try {
            log.info("扣子机器人聊天【准备执行】：请求参数：{}", JsonUtils.toJsonString(request));
            CozeChatRequest cozeChatRequest = CozeConvert.INSTANCE.convert(request);
            cozeChatRequest.setUserId(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
            CozeResponse<CozeChatResult> cozeResponse = cozeClient.chat(request.getConversationId(), cozeChatRequest);
            // 判断是否成功
            if (cozeResponse.getCode() != 0) {
                throw ServiceExceptionUtil.exception(new ErrorCode(cozeResponse.getCode(), cozeResponse.getMsg()));
            }
            // 判断是否返回结果
            if (Objects.isNull(cozeResponse.getData())) {
                throw ServiceExceptionUtil.exception(new ErrorCode(3_105_00_001, "返回结果为空! 请稍后重试或者联系管理员"));
            }
            // 返回结果
            ChatResult result = CozeConvert.INSTANCE.convert(cozeResponse.getData());
            log.info("扣子机器人聊天【执行成功】：返回结果：{}", JsonUtils.toJsonString(cozeResponse.getData()));
            return result;
        } catch (ServiceException exception) {
            log.error("扣子机器人聊天【执行失败】：错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            log.error("扣子机器人聊天【执行失败】：未知错误，错误信息：{}", exception.getMessage());
            throw ServiceExceptionUtil.exception(new ErrorCode(3_105_00_000, exception.getMessage()));
        }
    }

    /**
     * 查询扣子机器人会话详情
     *
     * @param query 请求参数
     * @return ChatResult
     */
    @Override
    public ChatResult retrieve(CozeChatQuery query) {
        try {
            log.info("查询扣子机器人会话详情【准备执行】：请求参数：{}", JsonUtils.toJsonString(query));
            CozeResponse<CozeChatResult> cozeResponse = cozeClient.retrieve(query.getConversationId(), query.getChatId());
            // 判断是否成功
            if (cozeResponse.getCode() != 0) {
                throw ServiceExceptionUtil.exception(new ErrorCode(cozeResponse.getCode(), cozeResponse.getMsg()));
            }
            // 判断是否返回结果
            if (Objects.isNull(cozeResponse.getData())) {
                throw ServiceExceptionUtil.exception(new ErrorCode(3_105_00_001, "返回结果为空! 请稍后重试或者联系管理员"));
            }
            // 返回结果
            ChatResult result = CozeConvert.INSTANCE.convert(cozeResponse.getData());
            log.info("查询扣子机器人会话详情【执行成功】：返回结果：{}", JsonUtils.toJsonString(query));
            return result;
        } catch (ServiceException exception) {
            log.error("查询扣子机器人会话详情【执行失败】：错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            log.error("查询扣子机器人会话详情【执行失败】：未知错误，错误信息：{}", exception.getMessage());
            throw ServiceExceptionUtil.exception(new ErrorCode(3_105_00_000, exception.getMessage()));
        }
    }


    /**
     * 查询扣子机器执行消息列表
     *
     * @param query 请求参数
     * @return ChatResult
     */
    @Override
    public List<MessageResult> messageList(CozeChatQuery query) {
        try {
            log.info("查询扣子机器人会话消息列表【准备执行】：请求参数：{}", JsonUtils.toJsonString(query));
            CozeResponse<List<CozeMessageResult>> cozeResponse = cozeClient.messageList(query.getConversationId(), query.getChatId());

            // 判断是否成功
            if (cozeResponse.getCode() != 0) {
                throw ServiceExceptionUtil.exception(new ErrorCode(cozeResponse.getCode(), cozeResponse.getMsg()));
            }
            // 判断是否返回结果
            if (CollectionUtil.isEmpty(cozeResponse.getData())) {
                throw ServiceExceptionUtil.exception(new ErrorCode(3_105_00_001, "返回结果为空! 请稍后重试或者联系管理员"));
            }
            // 返回结果
            List<MessageResult> result = CozeConvert.INSTANCE.convert(cozeResponse.getData());
            log.info("查询扣子机器人会话消息列表【执行成功】：返回结果：{}", JsonUtils.toJsonString(query));
            return result;
        } catch (ServiceException exception) {
            log.error("查询扣子机器人会话消息列表【执行失败】：错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            log.error("查询扣子机器人会话消息列表【执行失败】：未知错误，错误信息：{}", exception.getMessage());
            throw ServiceExceptionUtil.exception(new ErrorCode(3_105_00_000, exception.getMessage()));
        }
    }

    /**
     * 解析消息, 返回解析后的结果
     *
     * @param query 请求参数
     * @return Object
     */
    @Override
    public Object getToolResponse(CozeChatQuery query) {
        // 查询消息列表
        List<MessageResult> messageList = this.messageList(query);
        for (int i = messageList.size() - 1; i >= 0; i--) {
            MessageResult message = messageList.get(i);
            if (Objects.isNull(message)) {
                continue;
            }
            if (TOOL_RESPONSE.equalsIgnoreCase(message.getType())) {
                return this.parseMessage(message.getContent());
            }
        }
        throw ServiceExceptionUtil.exception(new ErrorCode(3_105_00_001, "生成结果不未找到! 请稍后重试或者联系管理员"));
    }

    /**
     * 获取扣子机器人回答的结果
     *
     * @param query 请求参数
     * @return Object
     */
    @Override
    public Object getAnswer(CozeChatQuery query) {
        // 查询消息列表
        List<MessageResult> messageList = this.messageList(query);
        for (int i = messageList.size() - 1; i >= 0; i--) {
            MessageResult message = messageList.get(i);
            if (Objects.isNull(message)) {
                continue;
            }
            if (ANSWER.equalsIgnoreCase(message.getType())) {
                return this.parseMessage(message.getContent());
            }
        }
        throw ServiceExceptionUtil.exception(new ErrorCode(3_105_00_001, "生成结果不未找到! 请稍后重试或者联系管理员"));
    }

    /**
     * @param query
     */
    @Override
    public void oauth(String query) {



    }

    /**
     * 解析消息, 返回解析后的结果
     *
     * @param content 请求参数
     * @return Object
     */
    public Object parseMessage(String content) {
        if (StringUtils.isBlank(content)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(3_105_00_001, "生成结果不未找到! 请稍后重试或者联系管理员"));
        }
        // 判断是否是 JSON 格式
        if (JSONUtil.isTypeJSON(content)) {
            try {
                return JSONUtil.parse(content);
            } catch (Exception exception) {
                log.error("扣子生成结果格式化处理异常: {}", exception.getMessage());
                throw ServiceExceptionUtil.exception(new ErrorCode(3_105_00_002, "生成结果格式化异常! 请稍后重试或者联系管理员"));
            }
        }

        if (content.contains("抱歉")) {
            log.error("扣子生成结果异常: {}", content);
            throw ServiceExceptionUtil.exception(new ErrorCode(3_105_00_003, content));
        }

        return content;
    }

}
