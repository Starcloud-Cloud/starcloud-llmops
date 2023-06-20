package com.starcloud.ops.business.order.controller.admin.refund.vo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "管理后台 - 退款订单创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PayRefundCreateReqVO extends PayRefundBaseVO {

}
