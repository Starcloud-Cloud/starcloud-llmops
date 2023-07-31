package com.starcloud.ops.business.chat.controller.admin.voices;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.chat.controller.admin.voices.vo.ChatVoiceVO;
import com.starcloud.ops.business.chat.service.impl.AzureVoiceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 语音管理接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@RestController
@RequestMapping("/llm/chat/voice")
@Tag(name = "chat语音管理", description = "chat语音管理")
public class ChatVoiceController {

    @Autowired
    public AzureVoiceServiceImpl azureVoiceService;

    @GetMapping("/list")
    @Operation(summary = "查询语音模型列表", description = "查询语音模型列表")
    public CommonResult<List<ChatVoiceVO>> categories() {

        return CommonResult.success(null);
    }


    @PostMapping("/speak")
    @Operation(summary = "消息文本语音生成", description = "消息文本语音生成")
    public CommonResult<String> speak(String messageUid, String text) {

        azureVoiceService.speak(text);

        return CommonResult.success(null);
    }

}
