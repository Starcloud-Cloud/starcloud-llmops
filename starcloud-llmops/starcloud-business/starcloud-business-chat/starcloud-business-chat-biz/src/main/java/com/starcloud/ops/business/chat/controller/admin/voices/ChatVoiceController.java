package com.starcloud.ops.business.chat.controller.admin.voices;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.chat.controller.admin.voices.vo.ChatVoiceVO;
import com.starcloud.ops.business.chat.controller.admin.voices.vo.MessageSpeakConfigVO;
import com.starcloud.ops.business.chat.service.impl.AzureVoiceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

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


    @PostMapping("/example")
    @Operation(summary = "语音事例执行", description = "语音事例执行")
    public void example(@RequestBody MessageSpeakConfigVO messageSpeakConfigVO, HttpServletResponse httpServletResponse) {

        httpServletResponse.setContentType("application/octet-stream");

        azureVoiceService.setEventCompletedConsumer((bytes) -> {
            try {

                httpServletResponse.setContentLength(bytes.length);
                httpServletResponse.getOutputStream().write(bytes);

                httpServletResponse.getOutputStream().flush();
                httpServletResponse.getOutputStream().close();

            } catch (Exception e) {
                log.error("example is fail: {}", e.getMessage(), e);
            }
        });

        String text = StrUtil.isBlank(messageSpeakConfigVO.getText()) ? "魔法AI是一家专注于生成式 AI 领域的科技公司，致力于用前沿的AI技术来创造内容。" : messageSpeakConfigVO.getText();

        azureVoiceService.speak(text, messageSpeakConfigVO);
    }

    @PostMapping("/speak")
    @Operation(summary = "消息文本语音生成", description = "消息文本语音生成")
    public SseEmitter speak(@RequestBody MessageSpeakConfigVO messageSpeakConfigVO) {

        SseEmitter emitter = new SseEmitter(60000L);

        //messageUid 校验合法性, 查出配置

        if (StrUtil.isBlank(messageSpeakConfigVO.getText())) {
            emitter.completeWithError(new RuntimeException("speak text cannot be empty"));
            return emitter;
        }


        azureVoiceService.setEventCompletedConsumer((bytes) -> {

            try {
                emitter.send(bytes);
            } catch (Exception e) {
                log.error("example is fail: {}", e.getMessage(), e);
            }

        });

        azureVoiceService.speak(messageSpeakConfigVO.getText(), messageSpeakConfigVO);

        return emitter;
    }

}
