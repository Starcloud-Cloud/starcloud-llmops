package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 素材知识库 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MaterialLibraryAppReqVO {

    @Schema(description = "应用 UID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30132")
    @NotNull(message = "应用 UID不能为空")
    private String appUid;

    @Schema(description = "应用名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "30132")
    private String appName;

    @Schema(description = "区分应用的不同渠道（）", requiredMode = Schema.RequiredMode.REQUIRED, example = "30132")
    @InEnum(CreativePlanSourceEnum.class)
    private Integer appType;

    @Schema(description = "用户名称）", requiredMode = Schema.RequiredMode.REQUIRED, example = "30132")
    private Long userId;

}