package com.starcloud.ops.business.app.controller.admin.opus.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "分页查询绑定")
public class OpusBindPageReqVO extends PageParam {

    @Schema(description = "作品集uid")
    @NotBlank(message = "作品集uid必填")
    private String opusUid;

    @Schema(description = "目录uid")
    private String dirUid;
}
