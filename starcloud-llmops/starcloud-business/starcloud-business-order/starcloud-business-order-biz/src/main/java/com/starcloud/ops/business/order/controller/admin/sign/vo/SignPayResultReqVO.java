package com.starcloud.ops.business.order.controller.admin.sign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = " 获取支付订单状态")
@Data
public class SignPayResultReqVO {




    private Long channelId;

    private String channelCode;

    private Long orderId;

    private Long orderExtensionId;

    private String orderExtensionNo;

    private String resultCode;

    private String resultMsg;


}
