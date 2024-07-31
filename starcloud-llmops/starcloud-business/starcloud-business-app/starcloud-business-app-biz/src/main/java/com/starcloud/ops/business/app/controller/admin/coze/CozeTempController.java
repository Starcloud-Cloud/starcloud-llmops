package com.starcloud.ops.business.app.controller.admin.coze;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.ParseResult;
import com.starcloud.ops.business.app.feign.CozeClient;
import com.starcloud.ops.business.app.feign.dto.coze.CozeChatResult;
import com.starcloud.ops.business.app.feign.dto.coze.CozeMessage;
import com.starcloud.ops.business.app.feign.dto.coze.CozeMessageResult;
import com.starcloud.ops.business.app.feign.request.coze.CozeChatRequest;
import com.starcloud.ops.business.app.feign.response.CozeResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.*;


@RestController
@RequestMapping("/llm/coze/temp")
@Tag(name = "星河云海-扣子接入", description = "星河云海-扣子接入")
public class CozeTempController {

    @Autowired
    private CozeClient cozeClient;

    @PostMapping("/chat")
    public CommonResult<CozeChatResult> chat(@RequestBody Map<String, Object> params) {
        CozeChatRequest request = new CozeChatRequest();

        request.setUserId(Objects.requireNonNull(SecurityFrameworkUtils.getLoginUserId()).toString());
        request.setBotId("7397320453251579913");
        CozeMessage cozeMessage = new CozeMessage();
        cozeMessage.setRole("user");
        cozeMessage.setContent("分析小红书链接内容：" + params.get("urls"));
        cozeMessage.setContentType("text");
        request.setMessages(Collections.singletonList(cozeMessage));
        CozeResponse<CozeChatResult> chat = cozeClient.chat(null, request);
        CommonResult<CozeChatResult> result = CommonResult.success(chat.getData());
        result.setCode(chat.getCode());
        result.setMsg(chat.getMsg());
        return result;
    }


    @GetMapping("/chat/result")
    public CommonResult<ParseResult> list(@RequestParam(value = "conversation_id") String conversationId, @RequestParam(value = "chat_id") String chatId) {
        CozeResponse<CozeChatResult> retrieve = cozeClient.retrieve(conversationId, chatId);

        if (!Objects.equals(0, retrieve.getCode())) {
            return CommonResult.error(retrieve.getCode(), retrieve.getMsg());
        }

        String status = Optional.ofNullable(retrieve).map(CozeResponse::getData).map(CozeChatResult::getStatus).orElse(StringUtils.EMPTY);


        if (!Objects.equals("completed", status)) {
            return CommonResult.success(new ParseResult());
        }

        CozeResponse<List<CozeMessageResult>> list = cozeClient.messageList(conversationId, chatId);
        Optional<CozeMessageResult> toolResponse = list.getData().stream().filter(res -> Objects.equals("tool_response", res.getType())).findFirst();
        if (toolResponse.isPresent()) {
            ParseResult parseResult = new ParseResult();
            String content = toolResponse.get().getContent();
            Type type = new TypeReference<Map<String, String>>() {
            }.getType();

            parseResult.setMaterialList(Collections.singletonList(JSON.parseObject(content,type)));
            parseResult.setComplete(true);
            return CommonResult.success(parseResult);
        }
        return CommonResult.error(500,"");
    }
}
