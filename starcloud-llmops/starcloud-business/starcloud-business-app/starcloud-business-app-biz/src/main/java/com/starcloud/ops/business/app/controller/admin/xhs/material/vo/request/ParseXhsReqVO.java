package com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request;

import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Schema(description = "解析小红书内容")
public class ParseXhsReqVO {

    @Schema(description = "笔记url")
    @NotEmpty(message = "笔记url不能为空")
    private List<String> noteUrlList;

    @Schema(description = "素材类型")
    @NotBlank(message = "素材类型不能为空")
    @InEnum(value = MaterialTypeEnum.class, field = InEnum.EnumField.CODE, message = "素材类型({value}) 必须属于: {values}")
    private String materialType;
}
