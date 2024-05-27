package com.starcloud.ops.business.product.controller.admin.sku.vo;

import com.starcloud.ops.business.product.api.sku.dto.ComplimentaryConfigDTO;
import com.starcloud.ops.business.product.api.sku.dto.OrderLimitConfig;
import com.starcloud.ops.business.product.api.sku.dto.SubscribeConfigDTO;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Schema(description = "管理后台 - 商品 SKU 创建/更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductSkuCreateOrUpdateReqVO extends ProductSkuBaseVO {
    @Schema(description = "商品设置用户基础权益")
    private AdminUserRightsAndLevelCommonDTO rightsConfig;

    @Schema(description = "绑定的优惠券")
    private List<Long> giveCouponTemplateIds;

    @Schema(description = "附赠配置")
    private ComplimentaryConfigDTO complimentaryConfig;

    @Schema(description = "附赠配置")
    private OrderLimitConfig orderLimitConfig;

    @Schema(description = "商品签约配置")
    private SubscribeConfigDTO subscribeConfig;

}
