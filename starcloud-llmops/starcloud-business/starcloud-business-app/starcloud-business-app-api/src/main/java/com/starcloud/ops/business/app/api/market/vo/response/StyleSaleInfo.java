package com.starcloud.ops.business.app.api.market.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class StyleSaleInfo {

    @Schema(description = "开启售卖")
    private Boolean openSale;

    @Schema(description = "演示笔记 ID")
    private String demoId;
}
