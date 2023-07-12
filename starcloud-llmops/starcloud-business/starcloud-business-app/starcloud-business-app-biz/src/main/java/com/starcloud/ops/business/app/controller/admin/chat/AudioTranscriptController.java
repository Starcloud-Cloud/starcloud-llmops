package com.starcloud.ops.business.app.controller.admin.chat;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.api.app.vo.response.AudioTranscriptRespVO;
import com.starcloud.ops.business.app.service.chat.AudioTranscriptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;


/**
 * @author starcloud
 */
@Tag(name = "星河云海 - 语音转文字")
@RestController
@RequestMapping("/llm/audio")
public class AudioTranscriptController {

    @Resource
    private AudioTranscriptService audioTranscript;

    @Operation(summary = "语音转文字")
    @PostMapping("/transcript")
    public CommonResult<AudioTranscriptRespVO> transcript(@RequestParam("file") MultipartFile file) throws IOException {
        return CommonResult.success(audioTranscript.audioTranscript(file));
    }
}
