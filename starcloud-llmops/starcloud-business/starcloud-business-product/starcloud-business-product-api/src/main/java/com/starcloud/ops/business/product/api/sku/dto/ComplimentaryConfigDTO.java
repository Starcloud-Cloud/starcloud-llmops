package com.starcloud.ops.business.product.api.sku.dto;

import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 赠送的权益
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplimentaryConfigDTO {

    @Schema(description = "附赠权益显示名称")
    private String name;

    /**
     * 商品权益
     */
    @Schema(description = "附赠权益配置")
    private AdminUserRightsAndLevelCommonDTO rightsConfig;



    @Schema(description = "备注")
    private String remark;

}
