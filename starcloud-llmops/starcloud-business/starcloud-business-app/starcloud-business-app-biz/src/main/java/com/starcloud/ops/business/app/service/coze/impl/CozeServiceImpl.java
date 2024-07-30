package com.starcloud.ops.business.app.service.coze.impl;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.controller.admin.coze.vo.CozeChatQuery;
import com.starcloud.ops.business.app.controller.admin.coze.vo.CozeChatReqVO;
import com.starcloud.ops.business.app.convert.coze.CozeConvert;
import com.starcloud.ops.business.app.feign.CozeClient;
import com.starcloud.ops.business.app.feign.dto.coze.CozeChatResult;
import com.starcloud.ops.business.app.feign.request.coze.CozeChatRequest;
import com.starcloud.ops.business.app.feign.response.CozeResponse;
import com.starcloud.ops.business.app.model.coze.ChatResult;
import com.starcloud.ops.business.app.model.coze.MessageResult;
import com.starcloud.ops.business.app.service.coze.CozeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Slf4j
@Service
public class CozeServiceImpl implements CozeService {

    @Resource
    private CozeClient cozeClient;

    /**
     * 扣子机器人聊天
     *
     * @param request 请求参数
     * @return ChatResult
     */
    @Override
    public ChatResult chat(@Validated CozeChatReqVO request) {
        log.info("扣子机器人【聊天】：请求参数：{}", JsonUtils.toJsonString(request));

        try {
            CozeChatRequest cozeChatRequest = CozeConvert.INSTANCE.convert(request);
            CozeResponse<CozeChatResult> cozeResponse = cozeClient.chat(request.getConversationId(), cozeChatRequest);
            if (cozeResponse.getCode() != 0) {
                throw ServiceExceptionUtil.exception(new ErrorCode(cozeResponse.getCode(), cozeResponse.getMsg()));
            }
            return CozeConvert.INSTANCE.convert(cozeResponse.getData());

        } catch (Exception exception) {
            log.error("[chat][request({}) 发生异常]", JsonUtils.toJsonString(request), exception);
            throw ServiceExceptionUtil.exception(new ErrorCode(0, ""));
        }
    }

    /**
     * 查询扣子机器执行会话执行情况
     *
     * @param query 请求参数
     * @return ChatResult
     */
    @Override
    public ChatResult retrieve(CozeChatQuery query) {
        return null;
    }

    /**
     * 查询扣子机器执行消息列表
     *
     * @param query 请求参数
     * @return ChatResult
     */
    @Override
    public List<MessageResult> messageList(CozeChatQuery query) {
        return Collections.emptyList();
    }

}
