package cn.iocoder.yudao.module.pay.controller.admin.merchant.vo.channel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 支付渠道 更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PayChannelUpdateReqVO extends PayChannelBaseVO {

    @Schema(description = "商户编号", required = true)
    @NotNull(message = "商户编号不能为空")
    private Long id;

    @Schema(description = "渠道配置的json字符串")
    @NotBlank(message = "渠道配置不能为空")
    private String config;
}
