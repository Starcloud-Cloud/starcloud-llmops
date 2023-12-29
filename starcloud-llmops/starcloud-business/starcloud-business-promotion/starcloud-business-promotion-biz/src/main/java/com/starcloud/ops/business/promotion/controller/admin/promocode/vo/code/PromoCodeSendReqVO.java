package com.starcloud.ops.business.promotion.controller.admin.promocode.vo.code;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Schema(description = "管理后台 - 优惠劵发放 Request VO")
@Data
@ToString(callSuper = true)
public class PromoCodeSendReqVO {

    @Schema(description = "优惠劵模板编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "优惠劵模板编号不能为空")
    private Long templateId;

    @Schema(description = "用户编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2]")
    @NotEmpty(message = "用户编号列表不能为空")
    private Set<Long> userIds;

}
