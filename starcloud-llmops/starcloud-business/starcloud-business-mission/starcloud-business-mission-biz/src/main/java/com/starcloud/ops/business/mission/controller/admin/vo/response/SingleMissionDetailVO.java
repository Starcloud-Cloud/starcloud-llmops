package com.starcloud.ops.business.mission.controller.admin.vo.response;

import com.starcloud.ops.business.mission.controller.admin.vo.dto.PostingContentDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "单条任务明细")
public class SingleMissionDetailVO {

    @Schema(description = "uid")
    private String uid;

    @Schema(description = "创作计划uid")
    private String creativePlanUid;

    @Schema(description = "通告名称")
    private String notificationName;

    @Schema(description = "任务内容")
    private PostingContentDTO content;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "标签")
    private List<String> tags;

}
