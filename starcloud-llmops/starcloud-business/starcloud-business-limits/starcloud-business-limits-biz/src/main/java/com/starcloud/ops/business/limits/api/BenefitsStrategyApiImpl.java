package com.starcloud.ops.business.limits.api;

import com.starcloud.ops.business.limits.api.benefitsstrategy.BenefitsStrategyApi;
import com.starcloud.ops.business.limits.api.benefitsstrategy.dto.UserBenefitsStrategyBaseDTO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import com.starcloud.ops.business.limits.service.userbenefitsstrategy.UserBenefitsStrategyService;
import com.starcloud.ops.business.limits.convert.userbenefitsstrategy.UserBenefitsStrategyConvert;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * BenefitsApi 的实现类
 */
@Service
public class BenefitsStrategyApiImpl implements BenefitsStrategyApi {

    @Resource
    private UserBenefitsStrategyService userBenefitsStrategyService;

    /**
     * 使用 图片 权益
     *
     * @param code
     */
    @Override
    public UserBenefitsStrategyBaseDTO getUserBenefitsStrategy( String code) {
        UserBenefitsStrategyDO userBenefitsStrategy = userBenefitsStrategyService.getUserBenefitsStrategy(code);
        return UserBenefitsStrategyConvert.INSTANCE.convertBaseDTO(userBenefitsStrategy);
    }
}
