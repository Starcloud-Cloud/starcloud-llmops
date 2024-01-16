package cn.iocoder.yudao.module.pay.api.notify.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 签约单的通知 Request DTO
 *
 * @author jason
 */
@Data
public class PaySignNotifyReqDTO {

    /**
     * 商户签约单号
     */
    @NotEmpty(message = "商户签约单号不能为空")
    private String merchantSignId;

    /**
     * 签约订单编号
     */
    @NotNull(message = "签约订单编号不能为空")
    private Long paySignId;

    /**
     * 签约状态
     */
    @NotNull(message = "签约状态不能为空")
    private Integer status;
}
