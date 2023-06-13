package com.starcloud.ops.business.limits.service.userbenefitsstrategy;


import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyCreateReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyPageReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyUpdateReqVO;
import com.starcloud.ops.business.limits.convert.userbenefitsstrategy.UserBenefitsStrategyConvert;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import com.starcloud.ops.business.limits.dal.mysql.userbenefitsstrategy.UserBenefitsStrategyMapper;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.limits.enums.ErrorCodeConstants.*;

/**
 * 用户权益策略
 * Service 实现类
 *
 * @author AlanCusack
 */
@Slf4j
@Service
@Validated
public class UserBenefitsStrategyServiceImpl implements UserBenefitsStrategyService {

    @Resource
    private UserBenefitsStrategyMapper userBenefitsStrategyMapper;


    /**
     * 为用户提供权益  code (生成唯一)
     *
     * @return 编号
     */
    @Override
    public String generateUniqueCode() {
        String code = IdUtil.fastSimpleUUID().substring(0, 12);
        // 创建查询条件
        LambdaQueryWrapper<UserBenefitsStrategyDO> wrapper = Wrappers.lambdaQuery(UserBenefitsStrategyDO.class);
        wrapper.eq(UserBenefitsStrategyDO::getCode, code);

        // 校验 code 是否存在
        long count = userBenefitsStrategyMapper.selectCount(wrapper);
        while (count > 0) {
            code = IdUtil.fastSimpleUUID().substring(0, 8);
            // 重新设置查询条件
            wrapper.clear();
            wrapper.eq(UserBenefitsStrategyDO::getCode, code);
            count = userBenefitsStrategyMapper.selectCount(wrapper);
        }
        return code;
    }

    /**
     * 为用户提供权益 code
     *
     * @return 编号
     */
    @Override
    public Boolean checkCode(String code) {
        // 创建查询条件
        LambdaQueryWrapper<UserBenefitsStrategyDO> wrapper = Wrappers.lambdaQuery(UserBenefitsStrategyDO.class);
        wrapper.eq(UserBenefitsStrategyDO::getCode, code);
        return BooleanUtil.isTrue(userBenefitsStrategyMapper.selectCount(wrapper) <= 0);
    }

    /**
     * 创建用户权益策略
     *
     * @param createReqVO 创建信息
     *
     * @return Long
     */
    @Override
    public Long createUserBenefitsStrategy(UserBenefitsStrategyCreateReqVO createReqVO) {
        // 权益策略枚举校验
        BenefitsStrategyTypeEnums strategyTypeEnums = BenefitsStrategyTypeEnums.getByCode(createReqVO.getStrategyType());
        if (strategyTypeEnums == null) {
            throw exception(BENEFITS_STRATEGY_TYPE_NOT_EXISTS);
        }
        // 输入转换
        UserBenefitsStrategyDO userBenefitsStrategy = UserBenefitsStrategyConvert.convert(createReqVO);
        // 设置 code 前缀
        userBenefitsStrategy.setCode(strategyTypeEnums.getPrefix() + "_" + userBenefitsStrategy.getCode());

        // 数据插入
        userBenefitsStrategyMapper.insert(userBenefitsStrategy);
        // 返回
        return userBenefitsStrategy.getId();
    }

    /**
     * 更新用户权益策略
     *
     * @param updateReqVO 更新信息
     */
    @Override
    public void updateUserBenefitsStrategy(UserBenefitsStrategyUpdateReqVO updateReqVO) {
        // 校验数据是否可以修改
        validateCanModify(updateReqVO.getId());
        // 更新
        UserBenefitsStrategyDO updateObj = UserBenefitsStrategyConvert.convert(updateReqVO);
        userBenefitsStrategyMapper.updateById(updateObj);
        log.info("[deleteUserBenefitsStrategy][修改用户权益策略成功。：策略ID({})|用户ID({})", updateReqVO.getId(), getLoginUserId());

    }


    /**
     * 删除用户权益策略
     *
     * @param id 编号
     */
    @Override
    public void deleteUserBenefitsStrategy(Long id) {
        // 删除
        userBenefitsStrategyMapper.deleteById(id);
        log.info("[deleteUserBenefitsStrategy][删除用户权益策略成功！：策略ID({})|用户ID({})", id, getLoginUserId());
    }


    @Override
    public UserBenefitsStrategyDO getUserBenefitsStrategy(Long id) {
        return userBenefitsStrategyMapper.selectById(id);
    }

    /**
     * 通过 code 获取权益信息
     *
     * @param code     权益码
     *
     * @return 编号
     */
    @Override
    public UserBenefitsStrategyDO getUserBenefitsStrategy(String code) {

        // 设置查询条件
        LambdaQueryWrapper<UserBenefitsStrategyDO> wrapper = Wrappers.lambdaQuery(UserBenefitsStrategyDO.class);
        wrapper.eq(UserBenefitsStrategyDO::getCode, code);
        // 获取当前租户下的权益策略
        UserBenefitsStrategyDO userBenefitsStrategyDO = userBenefitsStrategyMapper.selectOne(wrapper);
        if (userBenefitsStrategyDO == null) {
            throw exception(BENEFITS_STRATEGY_DATA_NOT_EXISTS);
        }
        return userBenefitsStrategyDO;
    }

    @Override
    public List<UserBenefitsStrategyDO> getUserBenefitsStrategyList(Collection<Long> ids) {
        return userBenefitsStrategyMapper.selectBatchIds(ids);
    }

    /**
     * 获得用户权益策略表
     * 分页
     *
     * @param pageReqVO 分页查询
     *
     * @return 用户权益策略表
     * 分页
     */
    @Override
    public PageResult<UserBenefitsStrategyDO> getUserBenefitsStrategyPage(UserBenefitsStrategyPageReqVO pageReqVO) {
        return userBenefitsStrategyMapper.selectPage(pageReqVO);
    }

    /**
     * 启用策略
     *
     * @return Boolean
     */
    @Override
    public Boolean enabledBenefitsStrategy(Long id) {

        // 获取数据
        UserBenefitsStrategyDO userBenefitsStrategyDO = userBenefitsStrategyMapper.selectById(id);
        // 非空校验
        if (userBenefitsStrategyDO == null) {
            throw exception(BENEFITS_STRATEGY_DATA_NOT_EXISTS);
        }
        // 归档校验
        if (userBenefitsStrategyDO.getArchived()) {
            throw exception(BENEFITS_STRATEGY_CAN_NOT_MODIFY_ARCHIVED);
        }
        // 停用校验
        if (userBenefitsStrategyDO.getEnabled()) {
            throw exception(BENEFITS_STRATEGY_CAN_NOT_MODIFY_ENABLE);
        }

        return null;
    }

    /**
     * 停用策略
     *
     * @return Boolean
     */
    @Override
    public Boolean unEnabledBenefitsStrategy(Long id) {
        return null;
    }

    /**
     * 策略归档
     *
     * @return Boolean
     */
    @Override
    public Boolean archivedBenefitsStrategy(Long id) {
        return null;
    }


    /**
     * 校验数据是否存在
     *
     * @param id 数据 ID
     */
    @Override
    public void validateUserBenefitsStrategyExists(Long id) {
        if (userBenefitsStrategyMapper.selectById(id) == null) {
            throw exception(BENEFITS_STRATEGY_DATA_NOT_EXISTS);
        }
    }

    /**
     * 校验数据是否可以修改 -仅启用状态且未归档的状态可以修改
     *
     * @param id 主键 ID
     */
    private void validateCanModify(Long id) {
        // 获取数据
        UserBenefitsStrategyDO userBenefitsStrategyDO = userBenefitsStrategyMapper.selectById(id);
        // 非空校验
        if (userBenefitsStrategyDO == null) {
            log.error("[deleteUserBenefitsStrategy][修改用户权益策略失败，策略数据不存在：策略ID({})|用户ID({})", id, getLoginUserId());
            throw exception(BENEFITS_STRATEGY_DATA_NOT_EXISTS);
        }
        // 归档校验
        if (userBenefitsStrategyDO.getArchived()) {
            log.error("[deleteUserBenefitsStrategy][修改用户权益策略失败，数据已经归档：策略ID({})|用户ID({})", id, getLoginUserId());
            throw exception(BENEFITS_STRATEGY_CAN_NOT_MODIFY_ARCHIVED);
        }
        // 启用校验
        if (userBenefitsStrategyDO.getEnabled()) {
            log.error("[deleteUserBenefitsStrategy][修改用户权益策略失败，数据未启用：策略ID({})|用户ID({})", id, getLoginUserId());
            throw exception(BENEFITS_STRATEGY_CAN_NOT_MODIFY_ENABLE);
        }

    }

    /**
     * 校验字典数据是否存在
     */
    private void validateStrategyTypeEnums(String code) {
        BenefitsStrategyTypeEnums strategyTypeEnums = BenefitsStrategyTypeEnums.getByCode(code);
        if (strategyTypeEnums == null) {
            throw exception(BENEFITS_STRATEGY_TYPE_NOT_EXISTS);
        }
    }





}
