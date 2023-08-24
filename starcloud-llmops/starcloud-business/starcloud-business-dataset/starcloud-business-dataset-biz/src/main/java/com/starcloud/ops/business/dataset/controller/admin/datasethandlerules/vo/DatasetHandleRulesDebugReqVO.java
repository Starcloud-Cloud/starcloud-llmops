package com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo;

import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 数据集规则 Request VO")
@Data
@ToString(callSuper = true)
public class DatasetHandleRulesDebugReqVO  {

    @Schema(description = "数据集 UID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据集 UID不能为空")
    private String datasetUid;

    @Schema(description = "测试数据", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "测试数据不能为空")
    private String url;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "标题不能为空")
    private String title;

    @Schema(description = "内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "内容不能为空")
    private String context;

    @Schema(description = "测试数据类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "测试数据类型不能为空")
    private String dataType;

    @Schema(description = "预处理规则", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "预处理规则不能为空")
    private com.starcloud.ops.business.dataset.pojo.dto.CleanRuleVO CleanRuleVO;

    @Schema(description = "分段规则", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分段规则不能为空")
    private SplitRule splitRule;
}
