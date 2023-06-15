package com.starcloud.ops.business.limits.service.util;

import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyUpdateReqVO;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.limits.service.userbenefitsstrategy.UserBenefitsStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.limits.enums.ErrorCodeConstants.BENEFITS_STRATEGY_CAN_NOT_DELETE;
import static com.starcloud.ops.business.limits.enums.ErrorCodeConstants.BENEFITS_STRATEGY_CAN_NOT_MODIFY_USED;

/**
 * 权益策略操作的实现 Service 实现类
 *
 * @author AlanCusack
 */

@Slf4j
@Service
@Validated
public class BenefitsOperationServiceImpl implements BenefitsOperationService {

    @Resource
    private UserBenefitsService userBenefitsService;

    @Resource
    private UserBenefitsStrategyService userBenefitsStrategyService;

    /**
     * 更新用户权益策略
     *
     * @param updateReqVO 更新信息
     */
    @Override
    public void updateUserBenefitsStrategy(UserBenefitsStrategyUpdateReqVO updateReqVO) {
        // 验证权益是否存在
        userBenefitsStrategyService.validateUserBenefitsStrategyExists(updateReqVO.getId());
        // 验证权益是否被使用
        if (userBenefitsService.exitBenefitsStrategy(updateReqVO.getId())) {
            log.error("[deleteUserBenefitsStrategy][修改用户权益策略失败，该配置已经被使用：策略ID({})|策略名称({})|用户ID({})", updateReqVO.getId(), updateReqVO.getStrategyName(), getLoginUserId());
            throw exception(BENEFITS_STRATEGY_CAN_NOT_MODIFY_USED);
        }
        // 数据更新
        userBenefitsStrategyService.updateStrategy(updateReqVO);

    }

    /**
     * 删除用户权益策略
     *
     * @param id 编号
     */
    @Override
    public void deleteUserBenefitsStrategy(Long id) {
        // 验证权益是否存在
        userBenefitsStrategyService.validateUserBenefitsStrategyExists(id);
        // 验证权益是否被使用
        if (userBenefitsService.exitBenefitsStrategy(id)) {
            log.error("[deleteUserBenefitsStrategy][修改用户权益策略失败，该配置已经被使用：策略ID({})|用户ID({})", id, getLoginUserId());
            throw exception(BENEFITS_STRATEGY_CAN_NOT_DELETE);
        }
        userBenefitsStrategyService.deleteStrategy(id);
    }


}
