package cn.iocoder.yudao.module.pay.api.order.dto;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Filename:     starcloud-llmops
 * Description:  cn.iocoder.yudao.module.pay.api.order.dto
 * Company:      mdc.ai Inc.
 *
 * @Author: djl
 * @version: 1.0
 * Create at:    2024/01/16  10:31
 * Modification History:
 * Date          Author      Version     Description
 * ------------------------------------------------------------------
 * 2024/01/16   AlanCusack    1.0         1.0 Version
 */
@Data
public class PayOrderSubmitReqDTO {

    @NotNull(message = "支付单编号不能为空")
    private Long id;

    @NotEmpty(message = "支付渠道不能为空")
    private String channelCode;

    private Map<String, String> channelExtras;

    private String displayMode;

    @URL(message = "回跳地址的格式必须是 URL")
    private String returnUrl;

    private Boolean isSign;

}
