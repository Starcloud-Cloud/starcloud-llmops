package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 应用素材绑定新增/修改 Request VO")
@Data
public class MaterialLibraryAppBindSaveReqVO {

    @Schema(description = "主键(自增策略)", requiredMode = Schema.RequiredMode.REQUIRED, example = "30393")
    private Long id;

    @Schema(description = "素材编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "14790")
    @NotNull(message = "素材编号不能为空")
    private Long libraryId;

    @Schema(description = "应用类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer appType;

    @Schema(description = "应用编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "7090")
    @NotEmpty(message = "应用编号不能为空")
    private String appUid;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "23875")
    private Long userId;

}