package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 素材知识库数据新增/修改 Request VO")
@Data
public class SliceUsageCountReqVO {

    @Schema(description = "素材库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30132")
    @NotNull(message = "素材库ID不能为空")
    private String libraryUid;

    @Schema(description = "应用 UID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30132")
    @NotNull(message = "素材库ID不能为空")
    private String appUid;

    @Schema(description = "区分应用的不同渠道（）", requiredMode = Schema.RequiredMode.REQUIRED, example = "30132")
    @InEnum(CreativePlanSourceEnum.class)
    private Integer appType;

    @Schema(description = "素材编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "19427")
    private List<SliceCountReqVO> countReqVOS;


}