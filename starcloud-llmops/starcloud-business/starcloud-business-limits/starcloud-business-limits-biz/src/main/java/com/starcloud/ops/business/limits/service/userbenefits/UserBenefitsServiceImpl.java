package com.starcloud.ops.business.limits.service.userbenefits;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsInfoResultVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsPageReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefits.UserBenefitsDO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsusagelog.UserBenefitsUsageLogDO;
import com.starcloud.ops.business.limits.dal.mysql.userbenefits.UserBenefitsMapper;
import com.starcloud.ops.business.limits.enums.BenefitsActionEnums;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyScopeEnums;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyTypeEnums;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefitsstrategy.UserBenefitsStrategyService;
import com.starcloud.ops.business.limits.service.userbenefitsusagelog.UserBenefitsUsageLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.limits.enums.ErrorCodeConstants.*;

/**
 * 用户权益 Service 实现类
 *
 * @author AlanCusack
 */
@Slf4j
@Service
@Validated
public class UserBenefitsServiceImpl implements UserBenefitsService {


    @Resource
    private UserBenefitsStrategyService userBenefitsStrategyService;

    @Resource
    private UserBenefitsUsageLogService userBenefitsUsageLogService;


    @Resource
    private UserBenefitsMapper userBenefitsMapper;


    /**
     * 新增用户权益
     *
     * @param code   权益 code
     * @param userId 用户 ID
     *
     * @return 编号
     */
    @Override
    public Boolean addUserBenefitsByCode(String code, Long userId) {
        // 根据 code 获取权益策略
        UserBenefitsStrategyDO benefitsStrategy = userBenefitsStrategyService.getUserBenefitsStrategy(code);
        // 获取当前策略枚举
        BenefitsStrategyTypeEnums strategyTypeEnums = BenefitsStrategyTypeEnums.getByCode(benefitsStrategy.getStrategyType());
        // 根据枚举中的规则判断当前策略是否可以使用
        switch (strategyTypeEnums) {
            case USER_ATTENDANCE:
                // 检查用户是否已签到
                if (hasUserCheckIn(userId)) {
                    log.error("[addUserBenefitsByCode][签到权益重复领取：用户ID({})]", userId);
                    throw exception(USER_BENEFITS_GET_FAIL_ATTENDANCE);
                }
                break;
            case SIGN_IN:
            case INVITE_TO_REGISTER:
                // 是否已使用注册和邀请注册类型的权益
                if (hasUserClaimedBenefits(userId, strategyTypeEnums)) {
                    log.error("[addUserBenefitsByCode][权益重复领取：用户ID({})｜权益类型({})]", userId, strategyTypeEnums.getCode());
                    throw exception(USER_BENEFITS_GET_FAIL_SING_IN);
                }
                break;
            default:
                // 其他类型的权益暂不需要进行额外检查
                break;
        }

        // 如果可以使用，使用 userBenefitsMapper新增权益
        UserBenefitsDO userBenefitsDO = new UserBenefitsDO();
        userBenefitsDO.setUid(IdUtil.fastSimpleUUID());
        userBenefitsDO.setUserId(String.valueOf(userId));
        userBenefitsDO.setStrategyId(String.valueOf(benefitsStrategy.getId()));
        userBenefitsDO.setAppCountUsed(benefitsStrategy.getAppCount());
        userBenefitsDO.setDatasetCountUsed(benefitsStrategy.getDatasetCount());
        userBenefitsDO.setImageCountUsed(benefitsStrategy.getImageCount());
        userBenefitsDO.setTokenCountUsed(benefitsStrategy.getTokenCount());
        // 根据策略设置时间
        BenefitsStrategyScopeEnums scopeEnums = BenefitsStrategyScopeEnums.getByCode(benefitsStrategy.getScope());
        LocalDateTime now = LocalDateTimeUtil.now();
        LocalDateTime expirationTime = null;
        switch (scopeEnums) {
            case ALWAYS:
                expirationTime = now.plusYears(100);
                break;
            case DAY:
                expirationTime = now.plusDays(benefitsStrategy.getScopeNum());
                break;
            case WEEK:
                expirationTime = now.plusWeeks(benefitsStrategy.getScopeNum());
            case MONTH:
                expirationTime = now.plusMonths(benefitsStrategy.getScopeNum());
                break;
            case YEAR:
                expirationTime = now.plusYears(benefitsStrategy.getScopeNum());
                break;
        }
        userBenefitsDO.setEffectiveTime(now);
        userBenefitsDO.setExpirationTime(expirationTime);
        userBenefitsDO.setEnabled(true);
        userBenefitsMapper.insert(userBenefitsDO);

        return true;
    }

    /**
     * 当天是否存在签到权益
     *
     * @param userId
     *
     * @return
     */
    private boolean hasUserCheckIn(Long userId) {
        // 查询条件-获取当天新增的所有权益
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class);

        wrapper.eq(UserBenefitsDO::getUserId, userId);
        LocalDateTime now = LocalDateTimeUtil.now();

        LocalDateTime startOfDay = now.with(LocalTime.MIN);
        LocalDateTime endOfDay = now.with(LocalTime.MAX);
        wrapper.between(UserBenefitsDO::getCreateTime, startOfDay, endOfDay);

        // 数据查询
        List<UserBenefitsDO> userBenefitsDOS = userBenefitsMapper.selectList(wrapper);

        boolean result = false;
        for (UserBenefitsDO userBenefitsDO : userBenefitsDOS) {
            UserBenefitsStrategyDO userBenefitsStrategy = userBenefitsStrategyService.getUserBenefitsStrategy(userBenefitsDO.getStrategyId());

            if (BenefitsStrategyTypeEnums.USER_ATTENDANCE.getCode().equals(userBenefitsStrategy.getStrategyType())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 是否存在注册权益
     * @param userId
     * @param benefitsType
     * @return
     */
    private boolean hasUserClaimedBenefits(Long userId, BenefitsStrategyTypeEnums benefitsType) {
        // 查询条件-获取当天新增的所有权益
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class);
        wrapper.eq(UserBenefitsDO::getUserId, userId);

        // 数据查询
        List<UserBenefitsDO> userBenefitsDOS = userBenefitsMapper.selectList(wrapper);
        boolean result = false;
        for (UserBenefitsDO userBenefitsDO : userBenefitsDOS) {
            UserBenefitsStrategyDO userBenefitsStrategy = userBenefitsStrategyService.getUserBenefitsStrategy(userBenefitsDO.getStrategyId());

            if (BenefitsStrategyTypeEnums.SIGN_IN.getCode().equals(userBenefitsStrategy.getStrategyType()) ||
                    BenefitsStrategyTypeEnums.INVITE_TO_REGISTER.getCode().equals(userBenefitsStrategy.getStrategyType())) {

                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 根据用户 ID 获取当前用户权益信息
     *
     * @param userId 用户 ID
     *
     * @return UserBenefitsInfoResultVO
     */
    @Override
    public UserBenefitsInfoResultVO getUserBenefits(Long userId) {

        UserBenefitsInfoResultVO userBenefitsInfoResultVO = new UserBenefitsInfoResultVO();
        // 查询条件 当前用户下启用的权益
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class);
        wrapper.eq(UserBenefitsDO::getUserId, userId);
        wrapper.eq(UserBenefitsDO::getEnabled, true);
        // TODO 暂时缺少定时任务 以实时更新权益是否过期 所以增加时间校验
        wrapper.le(UserBenefitsDO::getExpirationTime, LocalDateTime.now());

        List<UserBenefitsDO> resultList = userBenefitsMapper.selectList(wrapper);
        // 根据 ID 获取不同权益类型的剩余量
        long totalAppCountUsed = resultList.stream()
                .mapToLong(UserBenefitsDO::getAppCountUsed)
                .sum();

        long totalDatasetCountUsed = resultList.stream()
                .mapToLong(UserBenefitsDO::getDatasetCountUsed)
                .sum();

        long totalImageCountUsed = resultList.stream()
                .mapToLong(UserBenefitsDO::getImageCountUsed)
                .sum();

        long totalTokenCountUsed = resultList.stream()
                .mapToLong(UserBenefitsDO::getTokenCountUsed)
                .sum();

        userBenefitsInfoResultVO.setAppCountUsed(totalAppCountUsed);
        userBenefitsInfoResultVO.setDatasetCountUsed(totalDatasetCountUsed);
        userBenefitsInfoResultVO.setImageCountUsed(totalImageCountUsed);
        userBenefitsInfoResultVO.setTokenCountUsed(totalTokenCountUsed);

        // 根据 权益ID获取 当前总量
        Map<String, UserBenefitsStrategyDO> strategyMap = resultList.stream()
                .collect(Collectors.toMap(UserBenefitsDO::getStrategyId, user -> userBenefitsStrategyService.getUserBenefitsStrategy(Long.parseLong(user.getStrategyId()))));


        long totalAppCount = resultList.stream()
                .mapToLong(userBenefits -> strategyMap.get(userBenefits.getStrategyId()).getAppCount())
                .sum();

        long totalDatasetCount = resultList.stream()
                .mapToLong(userBenefits -> strategyMap.get(userBenefits.getStrategyId()).getDatasetCount())
                .sum();

        long totalImageCount = resultList.stream()
                .mapToLong(userBenefits -> strategyMap.get(userBenefits.getStrategyId()).getImageCount())
                .sum();

        long totalTokenCount = resultList.stream()
                .mapToLong(userBenefits -> strategyMap.get(userBenefits.getStrategyId()).getTokenCount())
                .sum();

        userBenefitsInfoResultVO.setAppTotal(totalAppCount);
        userBenefitsInfoResultVO.setDatasetTotal(totalDatasetCount);
        userBenefitsInfoResultVO.setImageTotal(totalImageCount);
        userBenefitsInfoResultVO.setTokenTotal(totalTokenCount);
        userBenefitsInfoResultVO.setQueryTime(LocalDateTimeUtil.now());

        return userBenefitsInfoResultVO;
    }

    /**
     * 权益使用
     *
     * @param benefitsTypeCode 权益类型 对应 BenefitsTypeEnums 中的 code 枚举类
     * @param amount           使用数
     * @param userId           用户 ID
     *
     * @return Boolean
     */
    @Override
    public void expendBenefits(String benefitsTypeCode, Long amount, Long userId) {

        // 校验权益类型是否合法
        BenefitsTypeEnums benefitsType = BenefitsTypeEnums.getByCode(benefitsTypeCode);
        if (benefitsType == null) {
            log.error("[expendBenefits][权益扣减失败，权益类型不存在：用户ID({})｜权益类型({})|数量({})", userId, benefitsTypeCode, amount);
            throw exception(BENEFITS_TYPE_NOT_EXISTS);
        }

        // 查询条件：当前用户下启用且未过期且权益值大于0的数据
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class);
        wrapper.eq(UserBenefitsDO::getUserId, userId);
        wrapper.eq(UserBenefitsDO::getEnabled, true);
        // TODO 暂时缺少定时任务 以实时更新权益是否过期 所以增加时间校验
        wrapper.le(UserBenefitsDO::getExpirationTime, LocalDateTime.now());

        switch (benefitsType) {
            case APP:
                wrapper.gt(UserBenefitsDO::getAppCountUsed, 0L);
                break;
            case DATASET:
                wrapper.gt(UserBenefitsDO::getDatasetCountUsed, 0L);
                break;
            case IMAGE:
                wrapper.gt(UserBenefitsDO::getImageCountUsed, 0L);
                break;
            case TOKEN:
                wrapper.gt(UserBenefitsDO::getTokenCountUsed, 0L);
                break;
        }
        // 查询数据
        List<UserBenefitsDO> resultList = userBenefitsMapper.selectList(wrapper);

        switch (benefitsType) {
            case APP:
                updateBenefits(resultList, UserBenefitsDO::getAppCountUsed, UserBenefitsDO::setAppCountUsed, amount);
                break;
            case DATASET:
                updateBenefits(resultList, UserBenefitsDO::getDatasetCountUsed, UserBenefitsDO::setDatasetCountUsed, amount);
                break;
            case IMAGE:
                updateBenefits(resultList, UserBenefitsDO::getImageCountUsed, UserBenefitsDO::setImageCountUsed, amount);
                break;
            case TOKEN:
                updateBenefits(resultList, UserBenefitsDO::getTokenCountUsed, UserBenefitsDO::setTokenCountUsed, amount);
                break;
        }

        // 批量更新数据
        userBenefitsMapper.updateBatch(resultList, resultList.size());
    }

    /**
     * 权益扣减
     * 扣减规则
     * 1.从第一条数据开始扣除 如果第一条数据满足扣除，不再遍历后面的账户 直接扣除 更新数据
     * 2.第一条数据不满足扣除，在第一条数据扣除至 0 后 继续从第二条数据开始扣除，直至扣除结束 更新所有扣除的数据记录
     * 3.如果直到最后一条数据依旧没办法满足扣除，那么允许用户扣除成功，将最后一条数据也设置为 0 更新数据记录
     *
     * @param resultList
     * @param getter
     * @param setter
     * @param amount
     */
    private void updateBenefits(List<UserBenefitsDO> resultList, Function<UserBenefitsDO, Long> getter, BiConsumer<UserBenefitsDO, Long> setter, Long amount) {

        long remainingAmount = amount;

        for (UserBenefitsDO userBenefits : resultList) {
            // 获取当前权益已使用数量
            long used = getter.apply(userBenefits);
            // 计算可用数量
            long available = Math.max(0, used - remainingAmount);
            // 计算已扣除数量
            long deducted = used - available;
            // 更新权益的可用数量
            setter.accept(userBenefits, available);
            // 更新剩余需扣除数量
            remainingAmount -= deducted;

            // 如果剩余需扣除数量已为0或负数，退出循环
            if (remainingAmount <= 0) {

                break;
            }
        }

        if (remainingAmount > 0) {
            log.warn("[expendBenefits][权益扣减成功，用户剩余权益不足扣除：用户ID({})｜权益类型({})|剩余数量({})", getLoginUserId(), getter.toString(), remainingAmount);
        }

    }

    @Override
    public PageResult<UserBenefitsDO> getUserBenefitsPage(UserBenefitsPageReqVO pageReqVO) {
        return userBenefitsMapper.selectPage(pageReqVO);
    }

    /**
     * 根据策略 ID 检测测罗是否被使用
     *
     * @param strategyId 策略编号
     *
     * @return Boolean
     */
    @Override
    public Boolean exitBenefitsStrategy(Long strategyId) {
        // 查询条件
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class);
        wrapper.eq(UserBenefitsDO::getStrategyId, strategyId);
        Long count = userBenefitsMapper.selectCount(wrapper);
        return BooleanUtil.isTrue(count > 0);
    }


    /**
     * 验证数据是存在
     *
     * @param id
     */
    private void validateUserBenefitsExists(Long id) {
        if (userBenefitsMapper.selectById(id) == null) {
            throw exception(USER_BENEFITS_NOT_EXISTS);
        }
    }

    public static void main(String[] args) {
        long available = 0;  // 计算可用数量
    }


}
