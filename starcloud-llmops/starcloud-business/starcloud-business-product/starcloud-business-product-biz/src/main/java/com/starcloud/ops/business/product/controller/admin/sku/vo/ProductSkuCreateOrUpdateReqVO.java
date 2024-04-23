package com.starcloud.ops.business.product.controller.admin.sku.vo;

import com.starcloud.ops.business.product.api.sku.dto.SubscribeConfigDTO;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 商品 SKU 创建/更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductSkuCreateOrUpdateReqVO extends ProductSkuBaseVO {

    /**
     * 商品附属权益
     */
    private AdminUserRightsAndLevelCommonDTO rightsConfig;
    /**
     * 商品签约配置
     */
    private SubscribeConfigDTO subscribeConfig;

}
