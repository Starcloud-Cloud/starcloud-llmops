package com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 优惠劵模板更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PromoCodeTemplateUpdateReqVO extends PromoCodeTemplateBaseVO {

    @Schema(description = "模板编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "模板编号不能为空")
    private Long id;

}