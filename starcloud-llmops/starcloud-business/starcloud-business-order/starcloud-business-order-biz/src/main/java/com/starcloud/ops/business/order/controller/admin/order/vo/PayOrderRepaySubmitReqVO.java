package com.starcloud.ops.business.order.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Schema(description = "管理后台 - 支付订单提交 Request VO")
@Data
public class PayOrderRepaySubmitReqVO {

    @Schema(description = "支付单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "支付单编号不能为空")
    private String orderId;

    @Schema(description = "支付渠道", requiredMode = Schema.RequiredMode.REQUIRED, example = "alipay_pc")
    @NotEmpty(message = "支付渠道不能为空")
    private String channelCode;

    @Schema(description = "支付渠道的额外参数，例如说，微信公众号需要传递 openid 参数")
    private Map<String, String> channelExtras;

    @Schema(description = "展示模式", example = "qr_code", defaultValue ="qr_code" ) // 参见 {@link PayDisplayModeEnum} 枚举。如果不传递，则每个支付渠道使用默认的方式
    private String displayMode;

    /**
     * 时间戳
     */
    @NotEmpty(message = "时间戳不能为空")
    private Long timestamp;
}
