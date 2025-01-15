package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request;

import com.starcloud.ops.business.app.feign.dto.video.VideoMergeConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "查询视频生成结果")
public class VideoMergeReqVO extends VideoMergeConfig{

}
