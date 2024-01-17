package cn.iocoder.yudao.module.pay.controller.admin.sign.vo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 支付签约Response VO")
@Data
@ExcelIgnoreUnannotated
public class SignRespVO {


    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "10857")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "应用编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10867")
    @ExcelProperty("应用编号")
    private Long appId;

    @Schema(description = "渠道编号", example = "17027")
    @ExcelProperty("渠道编号")
    private Long channelId;

    @Schema(description = "用户 IP", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("用户 IP")
    private String userIp;

    @Schema(description = "扩展 Id", example = "22365")
    @ExcelProperty("扩展 Id")
    private Long extensionId;

    @Schema(description = "签约号")
    @ExcelProperty("签约号")
    private String no;

    @Schema(description = "支付时间")
    @ExcelProperty("支付时间")
    private LocalDate payTime;

    @Schema(description = "渠道编码")
    @ExcelProperty("渠道编码")
    private String channelCode;

    @Schema(description = "商户订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15835")
    @ExcelProperty("商户订单编号")
    private String merchantSignId;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("商品名称")
    private String subject;

    @Schema(description = "支付系统-签约编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("支付系统-签约编号")
    private String body;

    @Schema(description = "返回的地址", example = "https://www.iocoder.cn")
    @ExcelProperty("返回的地址")
    private String returnUrl;

    @Schema(description = "签约通知时间", example = "https://www.iocoder.cn")
    @ExcelProperty("签约通知时间")
    private String notifyUrl;

    @Schema(description = "首次签约价格", example = "5926")
    @ExcelProperty("首次签约价格")
    private Long firstPrice;

    @Schema(description = "签约价格", requiredMode = Schema.RequiredMode.REQUIRED, example = "22486")
    @ExcelProperty("签约价格")
    private Long price;

    @Schema(description = "通知商户签约结果的回调状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("通知商户签约结果的回调状态")
    private Integer period;

    @Schema(description = "签约 有效周期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("签约 有效周期")
    private String periodUnit;

    @Schema(description = "费率")
    @ExcelProperty("费率")
    private Double channelFeeRate;

    @Schema(description = "费率价格", example = "13443")
    @ExcelProperty("费率价格")
    private Double channelFeePrice;

    @Schema(description = "签约状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("签约状态")
    private Integer status;

    @Schema(description = "签约成功时间")
    @ExcelProperty("签约成功时间")
    private LocalDateTime contractTime;

    @Schema(description = "签约失效时间")
    @ExcelProperty("签约失效时间")
    private LocalDateTime expireTime;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21134")
    @ExcelProperty("用户ID")
    private String userId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
