package com.starcloud.ops.business.app.api.xhs.material;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "元素字段定义")
public class FieldDefinitionDTO {

    @Schema(description = "字段名")
    private String fieldName;

    @Schema(description = "字段描述")
    private String desc;

    @Schema(description = "字段类型")
    private String type;

}
