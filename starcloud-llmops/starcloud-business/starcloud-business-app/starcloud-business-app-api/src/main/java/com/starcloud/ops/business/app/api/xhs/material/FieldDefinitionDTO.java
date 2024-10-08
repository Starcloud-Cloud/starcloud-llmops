package com.starcloud.ops.business.app.api.xhs.material;

import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "元素字段定义")
public class FieldDefinitionDTO {

    @Schema(description = "dto字段名")
    private String fieldName;

    @Schema(description = "字段描述 excel标题")
    private String desc;

    @Schema(description = "字段是否必填")
    private boolean required;

    @Schema(description = "表格宽度")
    private int width;

    /**
     * {@link FieldTypeEnum#getTypeCode()}
     */
    @Schema(description = "字段类型")
    private String type;

}
