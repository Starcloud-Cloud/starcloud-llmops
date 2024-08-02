package com.starcloud.ops.business.app.controller.admin.coze;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.controller.admin.coze.vo.CozeChatQuery;
import com.starcloud.ops.business.app.controller.admin.coze.vo.CozeChatReqVO;
import com.starcloud.ops.business.app.model.coze.ChatResult;
import com.starcloud.ops.business.app.model.coze.MessageResult;
import com.starcloud.ops.business.app.service.coze.CozeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@RestController
@RequestMapping("/llm/coze")
@Tag(name = "星河云海-扣子接入", description = "星河云海-扣子接入")
public class CozeController {

    @Resource
    private CozeService cozeService;

    @PostMapping("/chat")
    @Operation(summary = "扣子机器人聊天", description = "扣子聊天")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<ChatResult> chat(@Validated @RequestBody CozeChatReqVO request) {
        return CommonResult.success(cozeService.chat(request));
    }

    @GetMapping("/chat/retrieve")
    @Operation(summary = "查询扣子聊天会话详情", description = "查询扣子会话详情")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<ChatResult> retrieve(@Validated CozeChatQuery query) {
        return CommonResult.success(cozeService.retrieve(query));
    }

    @GetMapping("/chat/messageList")
    @Operation(summary = "查询扣子聊天会话消息列表", description = "查询扣子消息列表")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<List<MessageResult>> messageList(@Validated CozeChatQuery query) {
        return CommonResult.success(cozeService.messageList(query));
    }

    @GetMapping("/chat/getToolResponse")
    @Operation(summary = "解析消息", description = "解析消息")
    @ApiOperationSupport(order = 40, author = "nacoyer")
    public CommonResult<Object> getToolResponse(@Validated CozeChatQuery query) {
        return CommonResult.success(cozeService.getToolResponse(query));
    }

    @GetMapping("/chat/getAnswer")
    @Operation(summary = "解析消息", description = "解析消息")
    @ApiOperationSupport(order = 40, author = "nacoyer")
    public CommonResult<Object> getAnswer(@Validated CozeChatQuery query) {
        return CommonResult.success(cozeService.getAnswer(query));
    }
}
