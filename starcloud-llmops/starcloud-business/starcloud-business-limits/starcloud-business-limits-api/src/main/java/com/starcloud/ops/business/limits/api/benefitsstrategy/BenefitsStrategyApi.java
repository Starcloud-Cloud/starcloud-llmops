package com.starcloud.ops.business.limits.api.benefitsstrategy;

import com.starcloud.ops.business.limits.api.benefitsstrategy.dto.UserBenefitsStrategyBaseDTO;

/**
 * Filename:     starcloud-llmops
 * Description:  com.starcloud.ops.business.limits.api.benefitsstrategy
 * Company:      mdc.ai Inc.
 *
 * @Author: djl
 * @version: 1.0
 * Create at:    2023/11/14  17:43
 * Modification History:
 * Date          Author      Version     Description
 * ------------------------------------------------------------------
 * 2023/11/14   djl     1.0         1.0 Version
 */
public interface BenefitsStrategyApi {


    /**
     *通过 code 获取权益信息
     */
    UserBenefitsStrategyBaseDTO getUserBenefitsStrategy(String code);

}
