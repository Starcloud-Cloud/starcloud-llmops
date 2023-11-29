package com.starcloud.ops.business.mission.controller.admin.vo.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class SingleMissionExportVO {

    @ExcelProperty("uid")
    private String uid;

    @ExcelProperty("任务类型")
    private String type;

    @ExcelProperty("任务内容")
    private String content;

    @ExcelProperty("认领人Id")
    private String claimUserId;

    @ExcelProperty("认领人")
    private String claimUsername;

    @ExcelProperty("发布链接")
    private String publishUrl;

    @ExcelProperty("发布时间")
    private String publishTime;

    @ExcelProperty("预结算时间")
    private String preSettlementTime;

    @ExcelProperty("预估花费")
    private BigDecimal estimatedAmount;

    @ExcelProperty("结算时间")
    private String settlementTime;

    @ExcelProperty("结算金额")
    private BigDecimal settlementAmount;

    @ExcelProperty("支付单号")
    private String paymentOrder;

    @ExcelProperty("状态")
    private String status;
}
