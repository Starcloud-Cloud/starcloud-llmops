package com.starcloud.ops.business.limits.service.userbenefits;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsCreateReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsInfoResultVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsPageReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsUpdateReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefits.UserBenefitsDO;
import org.springframework.validation.annotation.Validated;

/**
 * 用户权益 Service 接口
 *
 * @author AlanCusack
 */
public interface UserBenefitsService {

    /**
     * 新增用户权益
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Boolean addUserBenefitsByCode(String code,Long userId);

    /**
     * 根据用户 ID 获取当前用户权益信息
     * @param userId 用户 ID
     * @return UserBenefitsInfoResultVO
     */
    UserBenefitsInfoResultVO getUserBenefits(Long userId);

    /**
     * 权益使用
     * @param userId 用户 ID
     * @param amount 使用数
     * @param benefitsType 权益类型 对应 BenefitsTypeEnums 枚举类
     * @return Boolean
     */
    void expendBenefits(String benefitsType,Long amount,Long userId);

    /**
     * 获得用户权益分页
     *
     * @param pageReqVO 分页查询
     * @return 用户权益分页
     */
    PageResult<UserBenefitsDO> getUserBenefitsPage(UserBenefitsPageReqVO pageReqVO);


    /**
     * 根据策略 ID 检测测罗是否被使用
     *
     * @param strategyId 策略编号
     * @return Boolean
     */
    Boolean exitBenefitsStrategy(Long strategyId);

}
