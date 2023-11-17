package com.starcloud.ops.business.limits.api.benefits;

import com.starcloud.ops.business.limits.api.benefits.dto.UserBaseDTO;

/**
 * 用户权益 API 封装
 */
public interface BenefitsApi {

    /**
     * 普通注册权益发放
     *
     * @param userBaseDTO 用户信息
     * @return Boolean
     */
    void registerBenefits(UserBaseDTO userBaseDTO);

    /**
     * 邀请注册权益发放
     *
     * @param inviteUser  邀请人用户信息
     * @param currentUser 被邀请人用户信息
     */
    void inviteBenefits(UserBaseDTO inviteUser, UserBaseDTO currentUser);

    /**
     * 判断 令牌权益 是否可以被使用
     * 1.如果 nums 为0 或者 null 则只要权益大于 0 就返回 True
     * 2.如果 nums 大于 0 则判断有效的权益总量是否大于 nums
     *
     * @param userBaseDTO 用户信息
     * @param nums        检测数
     * @return Boolean
     */
    Boolean CanUsageToken(Long nums, UserBaseDTO userBaseDTO);


    /**
     * 使用 令牌 权益
     *
     * @param nums        使用数
     * @param outId       外键值 映射应用 message 的 ID
     * @param userBaseDTO 用户信息
     */
    void UsageToken(Long nums, String outId, UserBaseDTO userBaseDTO);

    /**
     * 使用 令牌 权益 Token 不足会抛出异常
     *
     * @param nums        使用数
     * @param outId       外键值 映射应用 message 的 ID
     * @param userBaseDTO 用户信息
     */
    void UsageTokenBenefits(Long nums, String outId, UserBaseDTO userBaseDTO);


    /**
     * 判断 算力值 权益 是否可以被使用
     * 1.如果 nums 为0 或者 null 则只要权益大于 0 就返回 True
     * 2.如果 nums 大于 0 则判断有效的权益总量是否大于 nums
     *
     * @param userBaseDTO 用户信息
     * @param nums        检测数
     * @return Boolean
     */
    Boolean CanUsageComputationalPower(Long nums, UserBaseDTO userBaseDTO);

    /**
     * 使用 算力值 权益
     *
     * @param nums        使用数
     * @param outId       外键值 映射应用 message 的 ID
     * @param userBaseDTO 用户信息
     */
    void UsageComputationalPower(Long nums, String outId, UserBaseDTO userBaseDTO);

    /**
     * 使用 算力值 权益 不足会抛出异常
     *
     * @param nums        使用数
     * @param outId       外键值 映射应用 message 的 ID
     * @param userBaseDTO 用户信息
     */
    void UsageComputationalPowerBenefits(Long nums, String outId, UserBaseDTO userBaseDTO);

    /**
     * 判断 图片权益 是否可以被使用
     * 1.如果 nums 为0 或者 null 则只要权益大于 0 就返回 True
     * 2.如果 nums 大于 0 则判断有效的权益总量是否大于 nums
     *
     * @param userBaseDTO 用户信息
     * @param nums        检测数
     * @return Boolean
     */
    Boolean CanUsageImage(Long nums, UserBaseDTO userBaseDTO);

    /**
     * 使用 图片 权益
     *
     * @param nums        使用数
     * @param outId       外键值 映射应用 message 的 ID
     * @param userBaseDTO 用户信息
     */
    void UsageImage(Long nums, String outId, UserBaseDTO userBaseDTO);

    /**
     * 使用 图片 权益 Token 不足会抛出异常
     *
     * @param nums        使用数
     * @param outId       外键值 映射应用 message 的 ID
     * @param userBaseDTO 用户信息
     */
    void UsageImageBenefits(Long nums, String outId, UserBaseDTO userBaseDTO);


    void addUserBenefitsByCode(String code,Long useId);

    void addBenefitsAndRole(String benefitsType,Long useId,String roleCode);

    Boolean validateDiscount(String productCode,String discountCode,Long useId);

    Long calculateDiscountPrice(String productCode,String discountCode);

}
