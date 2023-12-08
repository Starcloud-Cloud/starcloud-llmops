package com.starcloud.ops.business.limits.service.util;

import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyUpdateReqVO;
import org.springframework.validation.annotation.Validated;

/**
 * 权益操作 Service 接口
 *
 * @author AlanCusack
 */
public interface BenefitsOperationService {

    /**
     * 更新用户权益策略
     *
     * @param updateReqVO 更新信息
     */
    void updateUserBenefitsStrategy(@Validated UserBenefitsStrategyUpdateReqVO updateReqVO, Long userId);

    /**
     * 删除用户权益策略
     *
     * @param id 编号
     */
    void deleteUserBenefitsStrategy(Long id, Long userId);
}
