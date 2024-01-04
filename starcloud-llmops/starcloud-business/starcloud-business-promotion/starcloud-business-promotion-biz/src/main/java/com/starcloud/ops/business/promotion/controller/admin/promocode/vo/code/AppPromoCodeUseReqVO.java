package com.starcloud.ops.business.promotion.controller.admin.promocode.vo.code;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 优惠劵领取 Request VO")
@Data
public class AppPromoCodeUseReqVO {

    @Schema(description = "兑换码", example = "96219")
    @NotNull(message = "兑换码不能为空")
    private String code;


}
