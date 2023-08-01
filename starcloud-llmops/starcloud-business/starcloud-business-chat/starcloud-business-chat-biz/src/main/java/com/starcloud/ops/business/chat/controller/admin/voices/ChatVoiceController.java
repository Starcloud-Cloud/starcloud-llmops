package com.starcloud.ops.business.chat.controller.admin.voices;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.chat.controller.admin.voices.vo.ChatVoiceVO;
import com.starcloud.ops.business.chat.controller.admin.voices.vo.SpeakConfigVO;
import com.starcloud.ops.business.chat.service.impl.AzureVoiceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * 语音管理接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Slf4j
@RestController
@RequestMapping("/llm/chat/voice")
@Tag(name = "chat语音管理", description = "chat语音管理")
public class ChatVoiceController {

    private static List<String> showChatVoiceList = new ArrayList<String>() {{
        add("zh-CN-YunyeNeural");
        add("zh-CN-YunxiNeural");
        add("wuu-CN-YunzheNeural");
        add("zh-TW-YunJheNeural");
        add("zh-CN-henan-YundengNeural");

        add("zh-CN-XiaoxuanNeural");
        add("zh-CN-XiaoruiNeural");
        add("zh-CN-XiaoqiuNeural");

        add("zh-CN-XiaomoNeural");
        add("zh-CN-XiaoxiaoNeural");
        add("wuu-CN-XiaotongNeural");

        add("zh-TW-HsiaoChenNeural");
        add("zh-CN-shaanxi-XiaoniNeural");
        add("zh-CN-liaoning-XiaobeiNeural");

    }};

    @Autowired
    public AzureVoiceServiceImpl azureVoiceService;

    @GetMapping("/list")
    @Operation(summary = "查询语音模型列表", description = "查询语音模型列表")
    public CommonResult<List<ChatVoiceVO>> list() {

        return CommonResult.success(azureVoiceService.findVoiceList(showChatVoiceList));
    }


    @GetMapping("/example")
    @Operation(summary = "语音事例执行", description = "语音事例执行")
    public SseEmitter example(@RequestBody SpeakConfigVO speakConfigVO) {

        SseEmitter emitter = new SseEmitter(60000L);

        azureVoiceService.setEventCompletedConsumer((bytes) -> {

            try {
                emitter.send(bytes);
            } catch (Exception e) {
                log.error("example is fail: {}", e.getMessage(), e);
            }
        });

        azureVoiceService.speak("魔法AI是一家专注于生成式 AI 领域的科技公司，致力于用前沿的AI技术来创造内容。", speakConfigVO);

        return emitter;
    }

    @PostMapping("/speak")
    @Operation(summary = "消息文本语音生成", description = "消息文本语音生成")
    public SseEmitter speak(@RequestBody SpeakConfigVO speakConfigVO, String messageUid, String text) {

        SseEmitter emitter = new SseEmitter(60000L);

        //messageUid 校验合法性

        azureVoiceService.setEventCompletedConsumer((bytes) -> {

            try {
                emitter.send(bytes);
            } catch (Exception e) {
                log.error("example is fail: {}", e.getMessage(), e);
            }

        });

        azureVoiceService.speak(text, speakConfigVO);

        return emitter;
    }

}
