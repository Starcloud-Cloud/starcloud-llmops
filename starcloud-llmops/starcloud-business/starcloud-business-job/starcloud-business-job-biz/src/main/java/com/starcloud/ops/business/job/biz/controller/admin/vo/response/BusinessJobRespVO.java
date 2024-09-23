package com.starcloud.ops.business.job.biz.controller.admin.vo.response;

import com.starcloud.ops.business.job.biz.controller.admin.vo.BusinessJobBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "定时任务配置")
public class BusinessJobRespVO extends BusinessJobBaseVO {

    @Schema(description = "uid")
    private String uid;

}
