package com.starcloud.ops.business.app.api.xhs.material;

import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialFieldTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "素材字段定义")
public class MaterialFieldConfigDTO {

    @Schema(description = "字段code")
    @NotBlank(message = "字段code 不能为空")
    private String fieldName;

    @Schema(description = "字段名称 表头")
    private String desc;

    @Schema(description = "字段是否必填")
    private boolean required = false;

    @Schema(description = "字段顺序")
    private int order;

    /**
     * {@link MaterialFieldTypeEnum#getTypeCode()}
     */
    @Schema(description = "字段类型")
    @NotBlank(message = "字段类型不能为空")
    @InEnum(value = MaterialFieldTypeEnum.class, field = InEnum.EnumField.CODE, message = "素材字段类型({value}) 必须属于: {values}")
    private String type;

}
