package com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request;

import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "筛选素材")
public class FilterMaterialReqVO {

    @Schema(description = "素材类型")
    @NotBlank(message = "素材类型不能为空")
    @InEnum(value = MaterialTypeEnum.class, field = InEnum.EnumField.CODE, message = "素材类型({value}) 必须属于: {values}")
    private String type;

    @Schema(description = "匹配内容关键字")
    private String content;

    @Schema(description = "匹配标签")
    private String tag;

    @Schema(description = "筛选数量")
    @Min(value = 1,message = "筛选数量最少为1")
    private Integer limitCount = 20;

}
