package com.starcloud.ops.business.mission.controller.admin.vo.response;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class SingleMissionExportVO {

    @ExcelProperty("uid")
    private String uid;

    @ExcelProperty("通告名称")
    private String notificationName;

    @ExcelProperty("内容标题")
    private String contentTitle;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("认领链接")
    private String claimUrl;

    @ExcelProperty("认领人")
    private String claimUsername;

    @ExcelProperty("认领时间")
    private String claimTime;

    @ExcelProperty("发布时间")
    private String publishTime;

    @ExcelProperty("发布链接")
    private String publishUrl;

    @ExcelProperty("点赞数")
    private Integer likedCount;

    @ExcelProperty("评论数")
    private Integer commentCount;

    @ExcelProperty("预结算金额")
    private BigDecimal estimatedAmount;

    @ExcelProperty("预结算时间")
    private String preSettlementTime;

    @ExcelProperty("结算金额")
    private BigDecimal settlementAmount;

    @ExcelProperty("结算时间")
    private String settlementTime;

    @ExcelProperty("支付订单号")
    private String paymentOrder;

}
