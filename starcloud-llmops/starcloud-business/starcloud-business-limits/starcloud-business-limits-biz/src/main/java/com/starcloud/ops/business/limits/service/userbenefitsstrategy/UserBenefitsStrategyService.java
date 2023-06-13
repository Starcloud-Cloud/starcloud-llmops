package com.starcloud.ops.business.limits.service.userbenefitsstrategy;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyCreateReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyPageReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyUpdateReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * 用户权益策略表
 * Service 接口
 *
 * @author AlanCusack
 */
public interface UserBenefitsStrategyService {


    /**
     * 生成权益 code
     *
     * @return 编号
     */
    String generateUniqueCode();

    /**
     * 验证当前 code 是否可以使用
     *
     * @return 编号
     */
    Boolean checkCode(String code);

    /**
     * 创建用户权益策略
     *
     * @param createReqVO 创建信息
     *
     * @return 编号
     */
    Long createUserBenefitsStrategy(@Valid UserBenefitsStrategyCreateReqVO createReqVO);

    /**
     * 更新用户权益策略表
     *
     * @param updateReqVO 更新信息
     */
    void updateUserBenefitsStrategy(@Valid UserBenefitsStrategyUpdateReqVO updateReqVO);

    /**
     * 删除用户权益策略
     *
     * @param id 编号
     */
    void deleteUserBenefitsStrategy(Long id);

    /**
     * 获得用户权益策略表
     *
     * @param id 编号
     *
     * @return 用户权益策略表
     */
    UserBenefitsStrategyDO getUserBenefitsStrategy(Long id);

    /**
     * 通过 code 获取权益信息
     *
     * @return 编号
     */
    UserBenefitsStrategyDO getUserBenefitsStrategy(String code);


    /**
     * 获得用户权益策略表
     * 列表
     *
     * @param ids 编号
     *
     * @return 用户权益策略表
     * 列表
     */
    List<UserBenefitsStrategyDO> getUserBenefitsStrategyList(Collection<Long> ids);

    /**
     * 获得用户权益策略表
     * 分页
     *
     * @param pageReqVO 分页查询
     *
     * @return 用户权益策略表
     * 分页
     */
    PageResult<UserBenefitsStrategyDO> getUserBenefitsStrategyPage(UserBenefitsStrategyPageReqVO pageReqVO);

    /**
     * 启用策略
     *
     * @return Boolean
     */
    Boolean enabledBenefitsStrategy(Long id);

    /**
     * 停用策略
     *
     * @return Boolean
     */
    Boolean unEnabledBenefitsStrategy(Long id);

    /**
     * 策略归档
     *
     * @return Boolean
     */
    Boolean archivedBenefitsStrategy(Long id);

    /**
     * 校验数据是否存在
     *
     * @param id 数据 ID
     */
    void validateUserBenefitsStrategyExists(Long id);



}

