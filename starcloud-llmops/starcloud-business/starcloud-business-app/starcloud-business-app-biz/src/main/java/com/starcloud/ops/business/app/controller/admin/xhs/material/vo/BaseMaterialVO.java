package com.starcloud.ops.business.app.controller.admin.xhs.material.vo;

import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "素材")
public class BaseMaterialVO {

    @Schema(description = "素材类型")
    @NotBlank(message = "素材类型不能为空")
    @InEnum(value = MaterialTypeEnum.class, field = InEnum.EnumField.CODE, message = "素材类型({value}) 必须属于: {values}")
    private String type;

    /**
     * 素材详情
     */
    @Schema(description = "素材内容")
    @NotNull(message = "素材内容不能为空")
    private AbstractBaseCreativeMaterialDTO materialDetail;

    /**
     * 标签用于筛选
     */
    @Schema(description = "标签")
    private List<String> tags;

}
