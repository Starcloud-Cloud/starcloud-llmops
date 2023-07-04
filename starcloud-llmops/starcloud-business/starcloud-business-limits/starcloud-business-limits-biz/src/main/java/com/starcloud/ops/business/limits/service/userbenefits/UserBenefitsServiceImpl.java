package com.starcloud.ops.business.limits.service.userbenefits;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.service.SecurityFrameworkService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.*;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo.UserBenefitsUsageLogCreateReqVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefits.UserBenefitsDO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import com.starcloud.ops.business.limits.dal.mysql.userbenefits.UserBenefitsMapper;
import com.starcloud.ops.business.limits.dal.mysql.userbenefitsstrategy.UserBenefitsStrategyMapper;
import com.starcloud.ops.business.limits.enums.*;
import com.starcloud.ops.business.limits.service.userbenefitsstrategy.UserBenefitsStrategyService;
import com.starcloud.ops.business.limits.service.userbenefitsusagelog.UserBenefitsUsageLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId;
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
    private SecurityFrameworkService securityFrameworkService;


    @Resource
    private UserBenefitsMapper userBenefitsMapper;


    @Resource
    private UserBenefitsStrategyMapper userBenefitsStrategyMapper;

    /**
     * 新增用户权益
     *
     * @param code   权益 code
     * @param userId 用户 ID
     * @return 编号
     */
    @Override
    public Boolean addUserBenefitsByCode(String code, Long userId) {
        log.info("[addUserBenefitsByCode][1.准备通过 权益code增加权益：用户ID({})|租户 ID({})｜权益代码({})]", userId, getTenantId(), code);
        // 根据 code 获取权益策略
        UserBenefitsStrategyDO benefitsStrategy;
        try {
            benefitsStrategy = userBenefitsStrategyService.getUserBenefitsStrategy(code);
        }catch (RuntimeException e){
            throw exception(USER_BENEFITS_USELESS_INTEREST);
        }

        // 获取当前策略枚举
        // BenefitsStrategyTypeEnums strategyTypeEnums = BenefitsStrategyTypeEnums.getByCode(benefitsStrategy.getStrategyType());

        if (benefitsStrategy.getLimitNum() != -1) {
            // 查询条件-检验是否超过兑换限制
            LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class);
            wrapper.eq(UserBenefitsDO::getStrategyId, benefitsStrategy.getId());

            if (benefitsStrategy.getLimitNum() <= userBenefitsMapper.selectCount(wrapper)) {
                log.error("[addUserBenefitsByCode][权益超出兑换次数：用户ID({})｜权益类型({})]", userId, benefitsStrategy.getStrategyType());
                throw exception(USER_BENEFITS_CASH_COUNT_EXCEEDED);
            }
        }
        // 检测权益使用频率是否合法
        if (benefitsStrategy.getLimitIntervalNum() > 0) {
            if (!checkBenefitsUsageFrequency(benefitsStrategy, userId)) {
                log.error("[addUserBenefitsByCode][权益使用频率超出限制：用户ID({})｜权益类型({})]", userId, benefitsStrategy.getStrategyType());
                throw exception(USER_BENEFITS_USAGE_FREQUENCY_EXCEEDED);
            }
        }

        // 如果可以使用，使用 userBenefitsMapper新增权益
        UserBenefitsDO userBenefitsDO = createUserBenefits(userId, benefitsStrategy);
        userBenefitsMapper.insert(userBenefitsDO);

        // 增加记录
        userBenefitsUsageLogService.batchCreateUserBenefitsUsageBatchLog(userBenefitsDO, benefitsStrategy);
        log.info("[addUserBenefitsByCode][1.增加权益成功：用户ID({})｜权益代码({})]", userId, code);
        return true;
    }

    /**
     * 根据类型新增权益 - 用作系统权益增加
     *
     * @param strategyType 权益类型
     * @param userId       用户 ID
     * @return Boolean
     */
    public Boolean addUserBenefitsByStrategyType(String strategyType, Long userId) {
        log.info("[addUserBenefitsByCode][1.准备增加权益，根据权益类型获取权益配置：用户ID({})|租户 ID({})｜权益类型({})]", userId, getTenantId(), strategyType);
        // 获取租户

        try {
            // 根据 code 获取权益策略
            UserBenefitsStrategyDO benefitsStrategy = userBenefitsStrategyService.getMasterConfigStrategyByType(strategyType);
            log.info("[addUserBenefitsByCode][2.获取权益配置成功：用户ID({})｜权益类型({})｜权益数据为({})]", userId, strategyType, JSONObject.toJSONString(benefitsStrategy));
            if (benefitsStrategy.getLimitNum() != -1) {
                // 查询条件-检验是否超过兑换限制
                LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class);
                wrapper.eq(UserBenefitsDO::getStrategyId, benefitsStrategy.getId());

                if (benefitsStrategy.getLimitNum() <= userBenefitsMapper.selectCount(wrapper)) {
                    log.error("[addUserBenefitsByCode][权益超出兑换次数：用户ID({})｜权益类型({})]", userId, strategyType);
                    throw exception(USER_BENEFITS_CASH_COUNT_EXCEEDED);
                }
            }
            // 检测权益使用频率是否合法
            if (benefitsStrategy.getLimitIntervalNum() > 0) {
                if (!checkBenefitsUsageFrequency(benefitsStrategy, userId)) {
                    log.error("[addUserBenefitsByCode][权益使用频率超出限制：用户ID({})｜权益类型({})]", userId, strategyType);
                    throw exception(USER_BENEFITS_USAGE_FREQUENCY_EXCEEDED);
                }
            }

            // 如果可以使用，使用 userBenefitsMapper新增权益
            UserBenefitsDO userBenefitsDO = createUserBenefits(userId, benefitsStrategy);
            userBenefitsMapper.insert(userBenefitsDO);

            // 增加记录
            userBenefitsUsageLogService.batchCreateUserBenefitsUsageBatchLog(userBenefitsDO, benefitsStrategy);

        } catch (RuntimeException e) {
            log.error("[addUserBenefitsByCode][1.增加权益失败：用户ID({})｜权益类型({})]", userId, strategyType);
        }
        log.info("[addUserBenefitsByCode][1.增加权益成功：用户ID({})｜权益类型({})]", userId, strategyType);
        return true;
    }

    /**
     * 检测是否兑换频率限制
     *
     * @param benefitsStrategy 权益数据
     * @param userId           用户 ID
     * @return boolean
     * // 1. LimitIntervalNum 如果不做限制 则表明该权益用户可以一直兑换 ，不校验LimitIntervalUnit
     * // 2. 如果 LimitIntervalNum 大于 0 则根据LimitIntervalUnit开始校验
     * // 3. LimitIntervalUnit 值为枚举 ONCE_ONLY("ONCE_ONLY", " 仅一次", "Once Only"), DAY("DAY", "天", "DAY"),WEEK("WEEK", "周", "WEEK"),MONTH("MONTH", "月", "MONTH"), YEAR("YEAR", "年", "YEAR"),
     * // 4. 如果    LimitIntervalUnit 为 NEVER 表明当前策略仅可以使用一次，否则就报错
     * // 5. 如果    LimitIntervalUnit 为 DAY、WEEK、MONTH、YEAR 表明 该策略在，LimitIntervalUnit 单位下 仅可以使用LimitIntervalNum 次 比如 LimitIntervalUnit 为 DAY  LimitIntervalNum 为 1 表明这条策略 一天可以使用一次否则就报错
     */
    private Boolean checkBenefitsUsageFrequency(UserBenefitsStrategyDO benefitsStrategy, Long userId) {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = null;

        BenefitsStrategyLimitIntervalEnums limitIntervalUnit = BenefitsStrategyLimitIntervalEnums.getByCode(benefitsStrategy.getLimitIntervalUnit());

        switch (limitIntervalUnit) {
            case DAY:
                endTime = startTime.minus(benefitsStrategy.getLimitIntervalNum(), ChronoUnit.DAYS);
                break;
            case WEEK:
                endTime = startTime.minus(benefitsStrategy.getLimitIntervalNum(), ChronoUnit.WEEKS);
                break;
            case MONTH:
                endTime = startTime.minus(benefitsStrategy.getLimitIntervalNum(), ChronoUnit.MONTHS);
                break;
            case YEAR:
                endTime = startTime.minus(benefitsStrategy.getLimitIntervalNum(), ChronoUnit.YEARS);
                break;
            case ONCE_ONLY:
                // endTime = startTime.minus(100, ChronoUnit.YEARS);
            default:
                // No need to set endTime
                break;
        }
        // 构建查询条件
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.<UserBenefitsDO>lambdaQuery()
                .eq(UserBenefitsDO::getUserId, userId)
                .eq(UserBenefitsDO::getStrategyId, benefitsStrategy.getId())
                .lt(UserBenefitsDO::getCreateTime, startTime);

        if (endTime != null) {
            wrapper.gt(UserBenefitsDO::getCreateTime, endTime);
        }

        // 查询符合条件的权益数
        long usageCount = userBenefitsMapper.selectCount(wrapper);

        // 判断是否超过限制的使用次数
        return usageCount < benefitsStrategy.getLimitIntervalNum();
    }

    /**
     * 根据策略和用户封装用户权益数据
     *
     * @param userId           用户 ID
     * @param benefitsStrategy 权益数据
     * @return UserBenefitsDO
     */
    private UserBenefitsDO createUserBenefits(Long userId, UserBenefitsStrategyDO benefitsStrategy) {
        UserBenefitsDO userBenefitsDO = new UserBenefitsDO();
        userBenefitsDO.setUid(IdUtil.fastSimpleUUID());
        userBenefitsDO.setUserId(String.valueOf(userId));
        userBenefitsDO.setStrategyId(String.valueOf(benefitsStrategy.getId()));
        userBenefitsDO.setAppCountUsed(benefitsStrategy.getAppCount());
        userBenefitsDO.setDatasetCountUsed(benefitsStrategy.getDatasetCount());
        userBenefitsDO.setImageCountUsed(benefitsStrategy.getImageCount());
        userBenefitsDO.setTokenCountUsed(benefitsStrategy.getTokenCount());

        userBenefitsDO.setAppCountInit(benefitsStrategy.getAppCount());
        userBenefitsDO.setDatasetCountInit(benefitsStrategy.getDatasetCount());
        userBenefitsDO.setImageCountInit(benefitsStrategy.getImageCount());
        userBenefitsDO.setTokenCountInit(benefitsStrategy.getTokenCount());

        // 根据策略设置时间
        BenefitsStrategyEffectiveUnitEnums scopeEnums = BenefitsStrategyEffectiveUnitEnums.getByCode(benefitsStrategy.getEffectiveUnit());
        LocalDateTime now = LocalDateTimeUtil.now();
        LocalDateTime expirationTime = calculateExpirationTime(now, scopeEnums, benefitsStrategy.getEffectiveNum());
        userBenefitsDO.setEffectiveTime(now);
        userBenefitsDO.setExpirationTime(expirationTime);
        userBenefitsDO.setEnabled(true);

        return userBenefitsDO;
    }

    /**
     * 根据权益策略设置用户权益时间
     *
     * @param now           当前时间
     * @param effectiveUnit 权益有效时间单位
     * @param effectiveNum  权益有效时间单位
     * @return ExpirationTime
     */
    private LocalDateTime calculateExpirationTime(LocalDateTime now, BenefitsStrategyEffectiveUnitEnums effectiveUnit, Long effectiveNum) {
        LocalDateTime expirationTime = null;
        switch (effectiveUnit) {
            case ALWAYS:
                expirationTime = now.plusYears(100);
                break;
            case DAY:
                expirationTime = now.plusDays(effectiveNum);
                break;
            case WEEK:
                expirationTime = now.plusWeeks(effectiveNum);
                break;
            case MONTH:
                expirationTime = now.plusMonths(effectiveNum);
                break;
            case YEAR:
                expirationTime = now.plusYears(effectiveNum);
                break;
        }
        return expirationTime;
    }

    /**
     * 根据用户 ID 获取当前用户权益信息
     *
     * @param userId 用户 ID
     * @return UserBenefitsInfoResultVO
     */
    @Override
    public UserBenefitsInfoResultVO getUserBenefits(Long userId) {

        UserBenefitsInfoResultVO userBenefitsInfoResultVO = new UserBenefitsInfoResultVO();


        // 查询条件：当前用户下启用的权益，并且未过期
        LocalDateTime currentTime = LocalDateTime.now();
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class)
                .eq(UserBenefitsDO::getUserId, userId)
                .eq(UserBenefitsDO::getEnabled, true)
                // TODO 暂时缺少定时任务 以实时更新权益是否过期 所以增加时间校验
                .ge(UserBenefitsDO::getExpirationTime, currentTime);

        List<UserBenefitsDO> resultList = userBenefitsMapper.selectList(wrapper);

        long totalImageCountUsed = 0;
        long totalTokenCountUsed = 0;
        long totalAppCountUsed = 0;
        long totalDatasetCountUsed = 0;


        long totalImageCount = 0;
        long totalTokenCount = 0;
        long totalAppCount = 0;
        long totalDatasetCount = 0;

        for (UserBenefitsDO userBenefits : resultList) {
            totalAppCountUsed += userBenefits.getAppCountUsed();
            totalDatasetCountUsed += userBenefits.getDatasetCountUsed();
            totalImageCountUsed += userBenefits.getImageCountUsed();
            totalTokenCountUsed += userBenefits.getTokenCountUsed();

            totalAppCount += userBenefits.getAppCountInit();
            totalDatasetCount += userBenefits.getDatasetCountInit();
            totalImageCount += userBenefits.getImageCountInit();
            totalTokenCount += userBenefits.getTokenCountInit();
        }
        userBenefitsInfoResultVO.setQueryTime(currentTime);

        // 根据用户权限判断用户等级
        if (securityFrameworkService.hasRole("MOFAAI_PRO")) {
            userBenefitsInfoResultVO.setUserLevel("pro");
        } else if (securityFrameworkService.hasRole("MOFAAI_PLUS")) {
            userBenefitsInfoResultVO.setUserLevel("plus");
        } else {
            userBenefitsInfoResultVO.setUserLevel("free");
        }


        List<UserBenefitsBaseResultVO> benefitsList = new ArrayList<>();
        // TODO: 2023/6/26
        //  1.暂时取消应用和数据集显示
        //  2.显示顺序 令牌>图片>应用>数据集

        benefitsList.add(createUserBenefitsBaseResultVO(BenefitsTypeEnums.TOKEN, totalTokenCountUsed, totalTokenCount));
        benefitsList.add(createUserBenefitsBaseResultVO(BenefitsTypeEnums.IMAGE, totalImageCountUsed, totalImageCount));
//        benefitsList.add(createUserBenefitsBaseResultVO(BenefitsTypeEnums.APP, totalAppCountUsed, totalAppCount));
//        benefitsList.add(createUserBenefitsBaseResultVO(BenefitsTypeEnums.DATASET, totalDatasetCountUsed, totalDatasetCount));

        userBenefitsInfoResultVO.setBenefits(benefitsList);

        return userBenefitsInfoResultVO;
    }



    private UserBenefitsBaseResultVO createUserBenefitsBaseResultVO(BenefitsTypeEnums benefitsType, long usedNum, long totalNum) {
        UserBenefitsBaseResultVO resultVO = new UserBenefitsBaseResultVO();
        resultVO.setName(benefitsType.getChineseName());
        resultVO.setType(benefitsType.getCode());
        if (totalNum != 0) {
            resultVO.setUsedNum(totalNum - usedNum);
            resultVO.setTotalNum(totalNum);
            resultVO.setPercentage(NumberUtil.round(NumberUtil.mul(NumberUtil.div(totalNum - usedNum, totalNum), 100), 0).intValue());
        } else {
            resultVO.setUsedNum(0L);
            resultVO.setTotalNum(0L);
            resultVO.setPercentage(0);
        }

        return resultVO;
    }

    /**
     * 检测是否存在可扣除的权益
     *
     * @param benefitsType 权益类型 对应 BenefitsTypeEnums 枚举类
     * @param userId       用户 ID
     * @return true 可以扣除 false 不可以扣除
     */
    @Override
    public void allowExpendBenefits(String benefitsType, Long userId) {
        // TODO 根据角色判断
        log.info("[allowExpendBenefits][检测是否存在可扣除的权益：用户ID({})｜权益类型({})", userId, benefitsType);
        // 校验权益类型是否合法
        BenefitsTypeEnums benefitsTypeEnums = BenefitsTypeEnums.getByCode(benefitsType);
        if (benefitsTypeEnums == null) {
            log.error("[allowExpendBenefits][检测是否存在可扣除的权益失败，权益类型不存在：用户ID({})｜权益类型({})", userId, benefitsType);
            throw exception(USER_BENEFITS_USELESS_INTEREST);
        }

        // 查询条件：当前用户下启用且未过期且权益值大于0的数据
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class);
        wrapper.eq(UserBenefitsDO::getUserId, userId);
        wrapper.eq(UserBenefitsDO::getEnabled, true);
        // TODO 暂时缺少定时任务 以实时更新权益是否过期 所以增加时间校验
        wrapper.ge(UserBenefitsDO::getExpirationTime, LocalDateTime.now());

        switch (benefitsTypeEnums) {
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
        if (CollUtil.isEmpty(resultList)) {
            log.error("[allowExpendBenefits][不存在可扣除的权益，用户所使用权益为 0：用户ID({})｜权益类型({})", userId, benefitsType);
            throw exception(USER_BENEFITS_USELESS_INSUFFICIENT);
        }
        log.info("[allowExpendBenefits][存在可扣除的权益：用户ID({})｜权益类型({})", userId, benefitsType);
    }

    /**
     * 权益使用
     * 扣减规则
     * 1.从第一条数据开始扣除 如果第一条数据满足扣除，不再遍历后面的账户 直接扣除 更新数据
     * 2.第一条数据不满足扣除，在第一条数据扣除至 0 后 继续从第二条数据开始扣除，直至扣除结束 更新所有扣除的数据记录
     * 3.如果直到最后一条数据依旧没办法满足扣除，那么允许用户扣除成功，将最后一条数据也设置为 0 更新数据记录
     *
     * @param benefitsTypeCode 权益类型 对应 BenefitsTypeEnums 中的 code 枚举类
     * @param amount           使用数
     * @param userId           用户 ID
     */
    @Override
    public void expendBenefits(String benefitsTypeCode, Long amount, Long userId, String outId) {
        // TODO 根据角色判断
        log.info("[expendBenefits][权益执行扣减操作：用户ID({})｜权益类型({})|数量({})|外键ID({})", userId, benefitsTypeCode, amount,outId);
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
        wrapper.ge(UserBenefitsDO::getExpirationTime, LocalDateTime.now());

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
        if (CollUtil.isEmpty(resultList)) {
            log.error("[expendBenefits][权益扣减失败，用户所使用权益为 0：用户ID({})｜权益类型({})|扣除数量({})", userId, benefitsTypeCode, amount);
            // throw exception(USER_BENEFITS_USELESS_INTEREST);
        }

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

        UserBenefitsUsageLogCreateReqVO userBenefitsUsageLogCreateReqVO = new UserBenefitsUsageLogCreateReqVO();

        userBenefitsUsageLogCreateReqVO.setUserId(String.valueOf(userId));
        userBenefitsUsageLogCreateReqVO.setBenefitsType(benefitsTypeCode);
        userBenefitsUsageLogCreateReqVO.setAction(BenefitsActionEnums.USED.getCode());
        userBenefitsUsageLogCreateReqVO.setAmount(amount);
        userBenefitsUsageLogCreateReqVO.setUsageTime(LocalDateTime.now());

        if (StrUtil.isNotBlank(outId)){
            userBenefitsUsageLogCreateReqVO.setOutId(outId);
        }

        List<Long> benefitsIds = resultList.stream()
                .map(UserBenefitsDO::getId)
                .collect(Collectors.toList());
        userBenefitsUsageLogCreateReqVO.setBenefitsIds(benefitsIds.toString());

        userBenefitsUsageLogService.createUserBenefitsUsageLog(userBenefitsUsageLogCreateReqVO);
    }

    /**
     * @param resultList 结果 List
     * @param getter     getter
     * @param setter     setter
     * @param amount     增加或者扣减数
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
    public PageResult<UserBenefitsPagInfoResultVO> getUserBenefitsPage(UserBenefitsPageReqVO pageReqVO) {
        PageResult<UserBenefitsPagInfoResultVO> userBenefitsPagInfoResultVOPageResult = new PageResult<>();

        // 获取当前用户权益分页
        pageReqVO.setUserId(Objects.requireNonNull(getLoginUserId()).toString());
        PageResult<UserBenefitsDO> userBenefitsDOPageResult = userBenefitsMapper.selectPage(pageReqVO);

        List<UserBenefitsPagInfoResultVO> resultList = new ArrayList<>();

        for (UserBenefitsDO userBenefitsDO : userBenefitsDOPageResult.getList()) {
            UserBenefitsPagInfoResultVO userBenefitsPagInfoResultVO = new UserBenefitsPagInfoResultVO();
            // 1. 查询权益信息，获取权益类型信息
            // 根据userBenefitsDO的strategyId查询权益信息，获取相应的权益类型信息
            UserBenefitsStrategyDO userBenefitsStrategy = userBenefitsStrategyService.getUserBenefitsStrategy(Long.valueOf(userBenefitsDO.getStrategyId()));
            userBenefitsPagInfoResultVO.setBenefitsName(userBenefitsStrategy.getStrategyName());
            userBenefitsPagInfoResultVO.setValidity(userBenefitsStrategy.getEffectiveNum());
            userBenefitsPagInfoResultVO.setValidityUnit(userBenefitsStrategy.getEffectiveUnit());
            userBenefitsPagInfoResultVO.setEffectiveTime(userBenefitsDO.getEffectiveTime());
            userBenefitsPagInfoResultVO.setExpirationTime(userBenefitsDO.getExpirationTime());
            userBenefitsPagInfoResultVO.setEnabled(userBenefitsDO.getEnabled());
            if ("-1".equals(userBenefitsStrategy.getEffectiveUnit())) {
                userBenefitsPagInfoResultVO.setValidityUnit("Year");
            }

            // 权益查询逻辑

            // 2. 创建UserBenefitsListResultVO对象并添加到结果列表
            List<UserBenefitsListResultVO> userBenefitsListResultVOS = new ArrayList<>();
            // 设置listResultVO的属性
            userBenefitsListResultVOS.add(new UserBenefitsListResultVO(BenefitsTypeEnums.TOKEN.getChineseName(), BenefitsTypeEnums.TOKEN.getCode(), userBenefitsDO.getTokenCountInit()));
            userBenefitsListResultVOS.add(new UserBenefitsListResultVO(BenefitsTypeEnums.IMAGE.getChineseName(), BenefitsTypeEnums.IMAGE.getCode(), userBenefitsDO.getImageCountInit()));
            // ...

            userBenefitsPagInfoResultVO.setBenefitsList(userBenefitsListResultVOS);
            resultList.add(userBenefitsPagInfoResultVO);
        }

        // 设置UserBenefitsPagInfoResultVO的属性
        userBenefitsPagInfoResultVOPageResult.setTotal(userBenefitsDOPageResult.getTotal());
        userBenefitsPagInfoResultVOPageResult.setList(resultList);
        return userBenefitsPagInfoResultVOPageResult;
    }

    /**
     * 根据策略 ID 检测权益是否被使用
     *
     * @param strategyId 策略编号
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
     * 判断当前时间是否在签到权益时间范围内，并且存在签到记录
     *
     * @return Boolean
     */
    @Override
    public Boolean hasSignInBenefitToday(Long userId) {
        // 当天的凌晨
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        // 当天的深夜
        LocalDateTime endDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        // 查询条件：当天用户权益
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class);
        wrapper.eq(UserBenefitsDO::getUserId, userId);
        wrapper.eq(UserBenefitsDO::getEnabled, true);
        wrapper.between(UserBenefitsDO::getCreateTime, startDateTime, endDateTime);

        List<UserBenefitsDO> userBenefitsDOS = userBenefitsMapper.selectList(wrapper);
        List<String> strategyIds = userBenefitsDOS.stream().map(UserBenefitsDO::getStrategyId).collect(Collectors.toList());
        // 当天新增的是否有签到的权益
        LambdaQueryWrapper<UserBenefitsStrategyDO> userBenefitsStrategyDOLambdaQueryWrapper = Wrappers.lambdaQuery(UserBenefitsStrategyDO.class);
        userBenefitsStrategyDOLambdaQueryWrapper.in(UserBenefitsStrategyDO::getId, strategyIds);
        userBenefitsStrategyDOLambdaQueryWrapper.eq(UserBenefitsStrategyDO::getStrategyType, BenefitsStrategyTypeEnums.USER_ATTENDANCE.getName());
        return userBenefitsStrategyMapper.selectCount(userBenefitsStrategyDOLambdaQueryWrapper) > 0;
    }


}
