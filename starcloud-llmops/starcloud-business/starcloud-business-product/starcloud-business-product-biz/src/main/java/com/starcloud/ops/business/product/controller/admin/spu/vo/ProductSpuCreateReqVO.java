package com.starcloud.ops.business.product.controller.admin.spu.vo;

import com.starcloud.ops.business.product.controller.admin.sku.vo.ProductSkuCreateOrUpdateReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

@Schema(description = "管理后台 - 商品 SPU 创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductSpuCreateReqVO extends ProductSpuBaseVO {

    // ========== SKU 相关字段 =========

    @Schema(description = "SKU 数组")
    @Valid
    private List<ProductSkuCreateOrUpdateReqVO> skus;
    // ========== 权益 相关字段 =========
    @Schema(description = "权益参数")
    @Valid
    private GiveRightsVO giveRights;

    @Schema(description = "订阅参数")
    @Valid
    private SubscribeConfigVO subscribeConfig;


}
