package com.starcloud.ops.business.app.api.market.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class StyleSaleInfo implements Serializable {

    private static final long serialVersionUID = -2634741541905431999L;

    @Schema(description = "开启售卖")
    private Boolean openSale;

    @Schema(description = "演示笔记 ID")
    private String demoId;
}
