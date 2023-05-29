package cn.iocoder.yudao.module.pay.controller.admin.merchant.vo.merchant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 支付商户信息更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PayMerchantUpdateReqVO extends PayMerchantBaseVO {

    @Schema(description = "商户编号", required = true)
    @NotNull(message = "商户编号不能为空")
    private Long id;

}
