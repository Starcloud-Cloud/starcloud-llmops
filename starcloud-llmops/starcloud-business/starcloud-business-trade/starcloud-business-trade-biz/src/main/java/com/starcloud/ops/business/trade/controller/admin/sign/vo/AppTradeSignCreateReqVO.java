package com.starcloud.ops.business.trade.controller.admin.sign.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.AssertTrue;

@Schema(description = "用户 App - 交易订单创建 Request VO")
@Data
public class AppTradeSignCreateReqVO extends AppTradeSignSettlementReqVO {

    @Schema(description = "备注", example = "这个是我的订单哟")
    private String remark;

    @Schema(description ="订单来源", example = "订单来源 20 =》H5 网页")
    private Integer terminal;

    @Schema(description ="创建来源", example = "创建来源")
    private String from;


    @AssertTrue(message = "配送方式不能为空")
    @JsonIgnore
    public boolean isDeliveryTypeNotNull() {
        return getDeliveryType() != null;
    }

}