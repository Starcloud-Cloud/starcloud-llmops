package com.starcloud.ops.business.limits.api;

import com.starcloud.ops.business.limits.api.benefits.BenefitsApi;
import com.starcloud.ops.business.limits.api.benefits.dto.UserBaseDTO;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsBaseResultVO;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.limits.enums.ErrorCodeConstants.*;

/**
 * BenefitsApi 的实现类
 */
@Service
public class BenefitsApiImpl implements BenefitsApi {


    @Resource
    private UserBenefitsService userBenefitsService;

    /**
     * 普通注册权益发放
     *
     * @param userBaseDTO 用户信息
     */
    @Override
    public void registerBenefits(UserBaseDTO userBaseDTO) {
        userBenefitsService.addUserBenefitsSign(userBaseDTO.getUserId());
    }

    /**
     * 邀请注册权益发放
     *
     * @param inviteUser  用户信息
     * @param currentUser 用户信息
     */
    @Override
    public void inviteBenefits(UserBaseDTO inviteUser, UserBaseDTO currentUser) {
        userBenefitsService.addUserBenefitsInvitation(inviteUser.getUserId(), currentUser.getUserId());
    }

    /**
     * 判断 令牌权益 是否可以被使用
     * 1.如果 nums 为0 或者 null 则只要权益大于 0 就返回 True
     * 2.如果 nums 大于 0 则判断有效的权益总量是否大于 nums
     *
     * @param userBaseDTO 用户信息
     * @param nums        检测数
     * @return Boolean
     */
    @Override
    public Boolean CanUsageToken(Long nums, UserBaseDTO userBaseDTO) {
        // 获取有效权益总数
        UserBenefitsBaseResultVO resultVO = userBenefitsService.getBenefitsByType(BenefitsTypeEnums.TOKEN.getCode(), userBaseDTO.getUserId());
        if (nums != null && nums > 0) {
            return resultVO.getRemaining() > nums;
        }
        return resultVO.getRemaining() > 0;
    }

    /**
     * 使用 令牌 权益
     *
     * @param nums        使用数
     * @param outId       外键值 映射应用 message 的 ID
     * @param userBaseDTO 用户信息
     */
    @Override
    public void UsageToken(Long nums, String outId, UserBaseDTO userBaseDTO) {
        userBenefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), nums, userBaseDTO.getUserId(), outId);
    }

    /**
     * 使用 令牌 权益 Token 不足会抛出异常
     *
     * @param nums        使用数
     * @param outId       外键值 映射应用 message 的 ID
     * @param userBaseDTO 用户信息
     */
    @Override
    public void UsageTokenBenefits(Long nums, String outId, UserBaseDTO userBaseDTO) {
        if (!CanUsageToken(nums, userBaseDTO)) {
            throw exception(USER_TOKEN_NOT_ADEQUATE);
        }
        UsageToken(nums, outId, userBaseDTO);
    }

    /**
     * 判断 算力值 权益 是否可以被使用
     * 1.如果 nums 为0 或者 null 则只要权益大于 0 就返回 True
     * 2.如果 nums 大于 0 则判断有效的权益总量是否大于 nums
     *
     * @param nums        检测数
     * @param userBaseDTO 用户信息
     * @return Boolean
     */
    @Override
    public Boolean CanUsageComputationalPower(Long nums, UserBaseDTO userBaseDTO) {
        // 获取有效权益总数
        UserBenefitsBaseResultVO resultVO = userBenefitsService.getBenefitsByType(BenefitsTypeEnums.COMPUTATIONAL_POWER.getCode(), userBaseDTO.getUserId());
        if (nums != null && nums > 0) {
            return resultVO.getRemaining() > nums;
        }
        return resultVO.getRemaining() > 0;
    }


    /**
     * 使用 算力值 权益
     *
     * @param nums        使用数
     * @param outId       外键值 映射应用 message 的 ID
     * @param userBaseDTO 用户信息
     */
    @Override
    public void UsageComputationalPower(Long nums, String outId, UserBaseDTO userBaseDTO) {
        userBenefitsService.expendBenefits(BenefitsTypeEnums.COMPUTATIONAL_POWER.getCode(), nums, userBaseDTO.getUserId(), outId);
    }

    /**
     * 使用 算力值 权益
     *
     * @param nums        使用数
     * @param outId       外键值 映射应用 message 的 ID
     * @param userBaseDTO 用户信息
     */
    @Override
    public void UsageComputationalPowerBenefits(Long nums, String outId, UserBaseDTO userBaseDTO) {
        if (!CanUsageComputationalPower(nums, userBaseDTO)) {
            throw exception(USER_COMPUTATIONAL_POWER_NOT_ADEQUATE);
        }
        UsageComputationalPower(nums, outId, userBaseDTO);
    }


    /**
     * 判断 图片权益 是否可以被使用
     * 1.如果 nums 为0 或者 null 则只要权益大于 0 就返回 True
     * 2.如果 nums 大于 0 则判断有效的权益总量是否大于 nums
     *
     * @param nums        检测数
     * @param userBaseDTO 用户信息
     * @return Boolean
     */
    @Override
    public Boolean CanUsageImage(Long nums, UserBaseDTO userBaseDTO) {
        // 获取有效权益总数
        UserBenefitsBaseResultVO resultVO = userBenefitsService.getBenefitsByType(BenefitsTypeEnums.IMAGE.getCode(), userBaseDTO.getUserId());
        if (nums != null && nums > 0) {
            return resultVO.getRemaining() > nums;
        }
        return resultVO.getRemaining() > 0;
    }

    /**
     * 使用 图片 权益
     *
     * @param nums        使用数
     * @param outId       外键值 映射应用 message 的 ID
     * @param userBaseDTO 用户信息
     */
    @Override
    public void UsageImage(Long nums, String outId, UserBaseDTO userBaseDTO) {
        userBenefitsService.expendBenefits(BenefitsTypeEnums.IMAGE.getCode(), nums, userBaseDTO.getUserId(), outId);
    }

    /**
     * 使用 图片 权益 Token 不足会抛出异常
     *
     * @param nums        使用数
     * @param outId       外键值 映射应用 message 的 ID
     * @param userBaseDTO 用户信息
     */
    @Override
    public void UsageImageBenefits(Long nums, String outId, UserBaseDTO userBaseDTO) {
        if (!CanUsageImage(nums, userBaseDTO)) {
            throw exception(USER_IMAGE_NOT_ADEQUATE);
        }
        UsageImage(nums, outId, userBaseDTO);
    }

    /**
     *
     */
    @Override
    public void addUserBenefitsByCode(String code,Long useId) {
        userBenefitsService.addUserBenefitsByCode(code, useId);
    }

    public void addBenefitsAndRole(String benefitsType,Long useId,String roleCode) {
        // TODO 设置用户角色 异常处理 日志
        userBenefitsService.addBenefitsAndRole(benefitsType, useId, roleCode);
    }


    public Boolean validateDiscount(String productCode,String discountCode,Long useId) {
       return userBenefitsService.validateDiscount(productCode, discountCode, useId);
    }


    public Long calculateDiscountPrice(String productCode,String discountCode) {
        return  userBenefitsService.calculateDiscountPrice(productCode, discountCode);
    }


}
