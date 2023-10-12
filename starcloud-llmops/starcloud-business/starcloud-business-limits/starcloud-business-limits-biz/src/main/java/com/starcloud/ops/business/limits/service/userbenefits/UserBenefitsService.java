package com.starcloud.ops.business.limits.service.userbenefits;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.*;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefits.UserBenefitsDO;

import java.util.List;

/**
 * 用户权益 Service 接口
 *
 * @author AlanCusack
 */
public interface UserBenefitsService {

    /**
     * 新增用户权益
     *
     * @param code   权益 code
     * @param userId 用户 ID
     * @return 编号
     */
    Boolean addUserBenefitsByCode(String code, Long userId);

    /**
     * 新增用户权益
     *
     * @param benefitsType 权益 type
     * @param userId       用户 ID
     * @return 编号
     */
    Boolean addUserBenefitsByStrategyType(String benefitsType, Long userId);

    /**
     * 根据用户 ID 获取当前用户权益信息
     *
     * @param userId 用户 ID
     * @return UserBenefitsInfoResultVO
     */
    UserBenefitsInfoResultVO getUserBenefits(Long userId);

    /**
     * 检测是否存在可扣除的权益
     *
     * @param benefitsType 权益类型 对应 BenefitsTypeEnums 枚举类
     * @param userId       用户 ID
     */

    void allowExpendBenefits(String benefitsType, Long userId);

    /**
     * 权益使用
     *
     * @param userId       用户 ID
     * @param amount       使用数
     * @param benefitsType 权益类型 对应 BenefitsTypeEnums 枚举类
     */
    void expendBenefits(String benefitsType, Long amount, Long userId, String outId);

    /**
     * 获得用户权益分页
     *
     * @param pageReqVO 分页查询
     * @return 用户权益分页
     */
    PageResult<UserBenefitsPagInfoResultVO> getUserBenefitsPage(UserBenefitsPageReqVO pageReqVO);


    /**
     * 根据策略 ID 检测测罗是否被使用
     *
     * @param strategyId 策略编号
     * @return Boolean
     */
    Boolean exitBenefitsStrategy(Long strategyId);


    /**
     * 判断当前时间是否在签到权益时间范围内，并且存在签到记录
     *
     * @return Boolean
     */
    Boolean hasSignInBenefitToday(Long userId);

    /**
     * 新增用户权益
     *
     * @param benefitsType 权益 type
     * @param userId       用户 ID
     * @param roleCode     角色 code
     * @return 编号
     */
    Boolean addBenefitsAndRole(String benefitsType, Long userId, String roleCode);

    /**
     * 用户有邀请码的情况--增加权益
     *
     * @param inviteUserId  邀请人 ID
     * @param currentUserId 被邀请人 ID
     */
    void addUserBenefitsInvitation(Long inviteUserId, Long currentUserId);

    /**
     * 用户普通注册--增加权益
     *
     * @param userId  用户 ID
     */
    void addUserBenefitsSign(Long userId);


    /**
     * 根据类型获取有效的权益总量
     */
    UserBenefitsBaseResultVO getBenefitsByType(String benefitsType, Long userId);


    /**
     * 获取七天内即将过期的权益
     */
    ExpiredReminderVO getBenefitsExpired();

    /**
     * 权益过期处理-返回已处理的数据
     */
    Long userBenefitsExpired();


}
