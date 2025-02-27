package com.starcloud.ops.business.app.controller.admin.xhs.content;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.VideoGenerateReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.VideoResult;
import com.starcloud.ops.business.app.feign.dto.video.VideoGeneratorConfig;
import com.starcloud.ops.business.app.service.xhs.content.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/llm/xhs/video")
@Tag(name = "星河云海-小红书 视频", description = "星河云海-小红书 视频")
public class VideoController {

    @Resource
    private VideoService videoService;

    @PostMapping("/generate")
    @Operation(summary = "生成视频", description = "生成视频")
    public CommonResult<Boolean> generateVideo(@Valid @RequestBody VideoGenerateReqVO reqVO) {
        videoService.generateVideo(reqVO);
        return CommonResult.success(true);
    }

    @GetMapping("/result/{creativeContentUid}")
    @Operation(summary = "生成结果", description = "生成结果")
    public CommonResult<VideoResult> generateResult(@PathVariable("creativeContentUid") String creativeContentUid) {
        return CommonResult.success(videoService.generateResult(creativeContentUid));
    }
}
