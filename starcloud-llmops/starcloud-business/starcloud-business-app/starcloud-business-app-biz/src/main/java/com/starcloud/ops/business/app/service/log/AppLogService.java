package com.starcloud.ops.business.app.service.log;

import com.starcloud.ops.business.app.api.image.vo.response.ImageMessageRespVO;
import com.starcloud.ops.business.app.api.log.vo.response.AppLogMessageRespVO;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-09
 */
public interface AppLogService {

    /**
     * 获取文本生成消息详情
     *
     * @param conversationUid 消息唯一标识
     * @return AppLogMessageRespVO
     */
    List<AppLogMessageRespVO> getLogAppMessageDetail(String conversationUid);

    /**
     * 获取图片生成消息详情
     *
     * @param conversationUid 消息唯一标识
     * @return ImageRespVO
     */
    List<ImageMessageRespVO> getLogImageMessageDetail(String conversationUid);

}
