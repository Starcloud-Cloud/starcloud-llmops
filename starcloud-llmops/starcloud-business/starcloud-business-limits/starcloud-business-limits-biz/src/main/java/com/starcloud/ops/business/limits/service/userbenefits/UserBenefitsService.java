package com.starcloud.ops.business.limits.service.userbenefits;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsInfoResultVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsPagInfoResultVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsPageReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefits.UserBenefitsDO;

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

}
