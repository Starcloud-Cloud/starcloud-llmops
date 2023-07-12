package com.starcloud.ops.business.app.service.chat.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.starcloud.ops.business.app.api.app.vo.response.AudioTranscriptRespVO;
import com.starcloud.ops.business.app.service.chat.AudioTranscriptService;
import com.starcloud.ops.llm.langchain.config.OpenAIConfig;
import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.audio.TranscriptionResult;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.AUDIO_TRANSCRIPT_ERROR;

/**
 * @author starcloud
 */
@Slf4j
@Service
public class AudioTranscriptServiceImpl implements AudioTranscriptService {

    @Resource
    private OpenAIConfig openAIConfig;

    @Override
    public AudioTranscriptRespVO audioTranscript(MultipartFile file) {
        if (file.getSize() > 1024 * 1024) {
            throw exception(AUDIO_TRANSCRIPT_ERROR, "Audio size exceeded.");
        }
        validateFileType(file.getContentType());
        File tempFile = null;
        String suffix = "." + StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
        try {
            tempFile = File.createTempFile(IdUtil.simpleUUID(), suffix);
            FileUtil.writeBytes(file.getBytes(), tempFile);
            OpenAiService openAiService = new OpenAiService(openAIConfig.getApiKey(), Duration.ofSeconds(openAIConfig.getTimeOut()));
            CreateTranscriptionRequest request = CreateTranscriptionRequest.builder()
                    .model("whisper-1")
                    .build();
            TranscriptionResult transcription = openAiService.createTranscription(request, tempFile);
            return new AudioTranscriptRespVO(transcription.getText());
        } catch (IOException e) {
            log.warn("audio transcript error", e);
            throw exception(AUDIO_TRANSCRIPT_ERROR, e.getMessage());
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

    private void validateFileType(String contentType) {
        String[] type = new String[]{"mp3", "mp4", "mpeg", "mpga", "m4a", "wav", "webm"};
        for (String s : type) {
            if (contentType.endsWith(s)) {
                return;
            }
        }
        throw exception(AUDIO_TRANSCRIPT_ERROR, "Audio type not allowed.");
    }
}
