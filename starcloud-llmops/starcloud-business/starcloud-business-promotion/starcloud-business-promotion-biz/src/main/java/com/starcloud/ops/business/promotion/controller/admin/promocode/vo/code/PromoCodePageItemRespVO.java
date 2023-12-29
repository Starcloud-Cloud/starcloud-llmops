package com.starcloud.ops.business.promotion.controller.admin.promocode.vo.code;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 优惠劵分页的每一项 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PromoCodePageItemRespVO extends PromoCodeRespVO {

    @Schema(description = "用户昵称", example = "老芋艿")
    private String nickname;

}
