package com.starcloud.ops.business.app.service.chat;

import com.starcloud.ops.business.app.api.app.vo.response.AudioTranscriptRespVO;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author starcloud
 */
public interface AudioTranscriptService {

    /**
     * 语音转文字
     */
    AudioTranscriptRespVO audioTranscript(MultipartFile file);

}
