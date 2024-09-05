package com.starcloud.ops.business.job.biz.controller.admin.vo.response;

import com.starcloud.ops.business.job.biz.controller.admin.vo.JobLogBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "任务日志")
public class CozeJobLogRespVO extends JobLogBaseVO {

    @Schema(description = "插件名称")
    private String pluginName;

    @Schema(description = "插件uid")
    private String pluginUid;

    @Schema(description = "素材库uid")
    private String libraryUid;

    @Schema(description = "生成条数")
    private String count;

}
