package cn.iocoder.yudao.module.pay.api.order.dto;

import lombok.Data;

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
public class PayOrderSubmitRespDTO {
    private Integer status;

    private String displayMode;

    private String displayContent;

}
