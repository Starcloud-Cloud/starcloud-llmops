package com.starcloud.ops.business.product.controller.admin.sku.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "管理后台 - 商品 SKU Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductSkuRespVO extends ProductSkuBaseVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

}
