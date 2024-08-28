package com.starcloud.ops.business.job.biz.controller.admin.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
@Schema(description = "素材库任务日志分页查询")
public class LibraryJobLogPageReqVO extends PageParam {


    @Schema(description = "素材库uid")
    @NotBlank(message = "素材库uid不能为空")
    private String libraryUid;
}
