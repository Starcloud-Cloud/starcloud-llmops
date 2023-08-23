package com.starcloud.ops.business.order.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 支付订单详细信息 Response VO")
@Data
@ToString(callSuper = true)
public class AppPayOrderDetailsRespVO {


    @Schema(description = "订单编号")
    private String id;

    @Schema(description = "商户订单编号")
    private String merchantOrderId;

    @Schema(description = "商品标题")
    private String subject;

    @Schema(description = "商品描述")
    private String body;

    @Schema(description = "支付金额，单位：分")
    private Long amount;

    @Schema(description = "支付状态")
    private Integer status;

    @Schema(description = "订单创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTime;
}
