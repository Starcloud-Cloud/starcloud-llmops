package com.starcloud.ops.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@Schema(description = "任务单价明细")
public class PostingUnitPriceDTO {

    @Schema(description = "发帖单价")
    @Min(value = 0, message = "发帖单价要大于0")
    private BigDecimal postingUnitPrice;

    @Schema(description = "回复单价")
    @Min(value = 0, message = "回复单价要大于0")
    private BigDecimal replyUnitPrice;

    @Schema(description = "点赞单价")
    @Min(value = 0, message = "点赞单价要大于0")
    private BigDecimal likeUnitPrice;
}
