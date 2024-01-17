package cn.iocoder.yudao.module.pay.controller.admin.sign.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;


@Schema(description = "管理后台 - 支付签约分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SignPageReqVO extends PageParam {


    @Schema(description = "应用编号", example = "10867")
    private Long appId;

    @Schema(description = "渠道编号", example = "17027")
    private Long channelId;

    @Schema(description = "用户 IP")
    private String userIp;

    @Schema(description = "扩展 Id", example = "22365")
    private Long extensionId;

    @Schema(description = "签约号")
    private String no;

    @Schema(description = "支付时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDate[] payTime;

    @Schema(description = "渠道编码")
    private String channelCode;

    @Schema(description = "商户订单编号", example = "15835")
    private String merchantSignId;

    @Schema(description = "商品名称")
    private String subject;

    @Schema(description = "支付系统-签约编号")
    private String body;

    @Schema(description = "返回的地址", example = "https://www.iocoder.cn")
    private String returnUrl;

    @Schema(description = "签约通知时间", example = "https://www.iocoder.cn")
    private String notifyUrl;

    @Schema(description = "首次签约价格", example = "5926")
    private Long firstPrice;

    @Schema(description = "签约价格", example = "22486")
    private Long price;

    @Schema(description = "通知商户签约结果的回调状态")
    private Integer period;

    @Schema(description = "签约 有效周期")
    private String periodUnit;

    @Schema(description = "费率")
    private Double channelFeeRate;

    @Schema(description = "费率价格", example = "13443")
    private Double channelFeePrice;

    @Schema(description = "签约状态", example = "1")
    private Integer status;

    @Schema(description = "签约成功时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] contractTime;

    @Schema(description = "签约失效时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] expireTime;

    @Schema(description = "用户ID", example = "21134")
    private String userId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
