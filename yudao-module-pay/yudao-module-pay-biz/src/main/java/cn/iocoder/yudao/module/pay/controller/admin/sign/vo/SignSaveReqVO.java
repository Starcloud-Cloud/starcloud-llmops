package cn.iocoder.yudao.module.pay.controller.admin.sign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import javax.validation.constraints.*;
import java.util.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 支付签约新增/修改 Request VO")
@Data
public class SignSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10857")
    private Long id;

    @Schema(description = "应用编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10867")
    @NotNull(message = "应用编号不能为空")
    private Long appId;

    @Schema(description = "渠道编号", example = "17027")
    private Long channelId;

    @Schema(description = "用户 IP", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "用户 IP不能为空")
    private String userIp;

    @Schema(description = "扩展 Id", example = "22365")
    private Long extensionId;

    @Schema(description = "签约号")
    private String no;

    @Schema(description = "支付时间")
    private LocalDate payTime;

    @Schema(description = "渠道编码")
    private String channelCode;

    @Schema(description = "商户订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15835")
    @NotEmpty(message = "商户订单编号不能为空")
    private String merchantSignId;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "商品名称不能为空")
    private String subject;

    @Schema(description = "支付系统-签约编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "支付系统-签约编号不能为空")
    private String body;

    @Schema(description = "返回的地址", example = "https://www.iocoder.cn")
    private String returnUrl;

    @Schema(description = "签约通知时间", example = "https://www.iocoder.cn")
    private String notifyUrl;

    @Schema(description = "首次签约价格", example = "5926")
    private Long firstPrice;

    @Schema(description = "签约价格", requiredMode = Schema.RequiredMode.REQUIRED, example = "22486")
    @NotNull(message = "签约价格不能为空")
    private Long price;

    @Schema(description = "通知商户签约结果的回调状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "通知商户签约结果的回调状态不能为空")
    private Integer period;

    @Schema(description = "签约 有效周期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "签约 有效周期不能为空")
    private String periodUnit;

    @Schema(description = "费率")
    private Double channelFeeRate;

    @Schema(description = "费率价格", example = "13443")
    private Double channelFeePrice;

    @Schema(description = "签约状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "签约状态不能为空")
    private Integer status;

    @Schema(description = "签约成功时间")
    private LocalDateTime contractTime;

    @Schema(description = "签约失效时间")
    private LocalDateTime expireTime;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21134")
    @NotEmpty(message = "用户ID不能为空")
    private String userId;

}
