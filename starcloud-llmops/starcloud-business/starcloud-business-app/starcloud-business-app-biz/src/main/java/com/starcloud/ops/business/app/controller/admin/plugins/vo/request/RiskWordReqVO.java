package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;

import com.starcloud.ops.business.app.enums.plugin.ProcessMannerEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "敏感词检测")
public class RiskWordReqVO {

    @Schema(description = "字段code")
    @NotEmpty(message = "字段code不能为空")
    private List<String> checkedFieldList;

    @Schema(description = "素材")
    @NotEmpty(message = "素材不能为空")
    private List<Map<String, Object>> materialList;

    @Schema(description = "处理方式")
    @InEnum(value = ProcessMannerEnum.class, field = InEnum.EnumField.CODE, message = "处理方式[{value}]必须在: [{values}] 范围内！")
    private String processManner;
}
