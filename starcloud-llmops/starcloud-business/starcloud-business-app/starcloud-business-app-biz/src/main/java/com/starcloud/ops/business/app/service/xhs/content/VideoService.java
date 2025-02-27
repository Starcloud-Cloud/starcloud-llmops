package com.starcloud.ops.business.app.service.xhs.content;

import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.VideoGenerateReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.VideoResult;

public interface VideoService {

    /**
     * 开始生成视频并合并
     */
    void generateVideo(VideoGenerateReqVO reqVO);

    /**
     * 生成视频结果
     */
    VideoResult generateResult(String creativeContentUid);
}
