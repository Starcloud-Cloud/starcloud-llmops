package com.starcloud.ops.business.limits.service.userbenefits;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.security.core.service.SecurityFrameworkService;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.limits.api.benefits.dto.UserBaseDTO;
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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
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
    private PermissionService permissionService;

    @Resource
    private RoleService roleService;

    @Resource
    private AdminUserService adminUserService;

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
        log.info("[addUserBenefitsByCode][准备通过 权益code增加权益：用户ID({})|租户 ID({})｜权益代码({})]", userId, getTenantId(), code);
        // 根据 code 获取权益策略
        UserBenefitsStrategyDO benefitsStrategy;
        try {
            benefitsStrategy = userBenefitsStrategyService.getUserBenefitsStrategy(code);
        } catch (RuntimeException e) {
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
        UserBenefitsDO userBenefitsDO = createUserBenefits(userId, benefitsStrategy, LocalDateTime.now());
        userBenefitsMapper.insert(userBenefitsDO);

        // 增加记录
        userBenefitsUsageLogService.batchCreateUserBenefitsUsageBatchLog(userBenefitsDO, benefitsStrategy);
        log.info("[addUserBenefitsByCode][增加权益结束：用户ID({})｜权益代码({})]", userId, code);
        return true;
    }

    /**
     * 根据类型新增权益 - 用作系统权益增加
     *
     * @param strategyType 权益类型
     * @param userId       用户 ID
     * @return Boolean
     */
    @Transactional
    public Boolean addUserBenefitsByStrategyType(String strategyType, Long userId) {

        log.info("[addUserBenefitsByCode][1.准备增加权益，根据权益类型获取权益配置：用户ID({})|租户 ID({})｜权益类型({})]", userId, getTenantId(), strategyType);

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
            // 判断是支付权益
            LocalDateTime now;
            // 判断是支付权益
            if (StrUtil.equalsAny(strategyType, BenefitsStrategyTypeEnums.PAY_PLUS_MONTH.getName(), BenefitsStrategyTypeEnums.PAY_PLUS_YEAR.getName(),
                    BenefitsStrategyTypeEnums.PAY_PRO_MONTH.getName(), BenefitsStrategyTypeEnums.PAY_PRO_YEAR.getName())) {
                log.info("[addUserBenefitsByCode][用户增加支付权益：用户ID({})｜权益类型({})]", userId, strategyType);

                // 获取用户当前时间下生效的权益
                LocalDateTime currentTime = LocalDateTime.now();
                LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class)
                        .eq(UserBenefitsDO::getUserId, userId)
                        .eq(UserBenefitsDO::getStrategyId, benefitsStrategy.getId())
                        .eq(UserBenefitsDO::getEnabled, true)
                        // TODO 暂时缺少定时任务 以实时更新权益是否过期 所以增加时间校验
                        .ge(UserBenefitsDO::getExpirationTime, currentTime)
                        .orderByDesc(UserBenefitsDO::getExpirationTime)
                        .last("limit 1");

                UserBenefitsDO userBenefitsDO = userBenefitsMapper.selectOne(wrapper);
                // 用户新增权益与目前权益相同则按照目前权益的过期时间未开始时间新增一条权益

                if (userBenefitsDO == null) {
                    now = LocalDateTimeUtil.now();
                } else {
                    now = userBenefitsDO.getExpirationTime();
                }
            } else {
                now = LocalDateTimeUtil.now();
            }

            // 如果可以使用，使用 userBenefitsMapper新增权益
            UserBenefitsDO userBenefitsDO = createUserBenefits(userId, benefitsStrategy, now);
            userBenefitsMapper.insert(userBenefitsDO);
            // 增加记录
            userBenefitsUsageLogService.batchCreateUserBenefitsUsageBatchLog(userBenefitsDO, benefitsStrategy);

        } catch (RuntimeException e) {
            log.error("[addUserBenefitsByCode][3.增加权益失败：用户ID({})｜权益类型({})]｜完整错误为({})]", userId, strategyType, e.getMessage(), e);
            return false;
        }
        log.info("[addUserBenefitsByCode][4.增加权益结束：用户ID({})｜权益类型({})]", userId, strategyType);
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
     * // 4. 如果    LimitIntervalUnit 为 ONCE_ONLY 表明当前策略仅可以使用一次，否则就报错
     * // 5. 如果    LimitIntervalUnit 为 DAY、WEEK、MONTH、YEAR 表明 该策略在，LimitIntervalUnit 单位下 仅可以使用LimitIntervalNum 次 比如 LimitIntervalUnit 为 DAY  LimitIntervalNum 为 1 表明这条策略 一天可以使用一次否则就报错
     */
    private Boolean checkBenefitsUsageFrequency(UserBenefitsStrategyDO benefitsStrategy, Long userId) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime;
        LocalDateTime endTime;

        BenefitsStrategyLimitIntervalEnums limitIntervalUnit = BenefitsStrategyLimitIntervalEnums.getByCode(benefitsStrategy.getLimitIntervalUnit());

        switch (limitIntervalUnit) {
            case DAY:
                // 一天的开始时间 到一天的结束时间
                startTime = LocalDateTimeUtil.beginOfDay(now);
                endTime = LocalDateTimeUtil.endOfDay(now);
                break;
            case WEEK:
                // 周一
                LocalDateTime monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                // 周日
                LocalDateTime sunday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                // 本周开始时间
                startTime = LocalDateTimeUtil.beginOfDay(monday);
                // 本周结束时间
                endTime = LocalDateTimeUtil.endOfDay(sunday);
                break;
            case YEAR:
                // 年第一天
                startTime = now.with(TemporalAdjusters.firstDayOfYear());
                endTime = now.with(TemporalAdjusters.lastDayOfYear());
                break;
            case ONCE_ONLY:
                startTime = now.minus(50, ChronoUnit.YEARS);
                endTime = now.plusYears(50);
                break;
            case MONTH:
            default:
                // 本月1号
                LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
                // 本月最后一天
                LocalDateTime lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
                // 本月开始时间
                startTime = LocalDateTimeUtil.beginOfDay(firstDayOfMonth);
                // 本月结束时间
                endTime = LocalDateTimeUtil.endOfDay(lastDayOfMonth);
                break;
        }
        // 构建查询条件
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.<UserBenefitsDO>lambdaQuery()
                .eq(UserBenefitsDO::getUserId, userId)
                .eq(UserBenefitsDO::getStrategyId, benefitsStrategy.getId())
                .between(UserBenefitsDO::getCreateTime, startTime, endTime);

        // 查询符合条件的权益数
        long usageCount = userBenefitsMapper.selectCount(wrapper);

        Long limitCount = benefitsStrategy.getLimitIntervalNum();
        // 判断是否存在不限制情况
        if (-1 == limitCount) {
            limitCount = Long.MAX_VALUE;
        }

        // 判断是否超过限制的使用次数
        return usageCount < limitCount;
    }

    /**
     * 根据策略和用户封装用户权益数据
     *
     * @param userId           用户 ID
     * @param benefitsStrategy 权益数据
     * @return UserBenefitsDO
     */
    private UserBenefitsDO createUserBenefits(Long userId, UserBenefitsStrategyDO benefitsStrategy, LocalDateTime startTime) {

        UserBenefitsDO userBenefitsDO = new UserBenefitsDO();
        userBenefitsDO.setUid(IdUtil.fastSimpleUUID());
        userBenefitsDO.setUserId(String.valueOf(userId));

        userBenefitsDO.setStrategyId(String.valueOf(benefitsStrategy.getId()));

        userBenefitsDO.setAppRemaining(benefitsStrategy.getAppCount());
        userBenefitsDO.setDatasetRemaining(benefitsStrategy.getDatasetCount());
        userBenefitsDO.setImageRemaining(benefitsStrategy.getImageCount());
        userBenefitsDO.setTokenRemaining(benefitsStrategy.getTokenCount());
        userBenefitsDO.setComputationalPowerRemaining(benefitsStrategy.getComputationalPowerCount());

        userBenefitsDO.setAppCountInit(benefitsStrategy.getAppCount());
        userBenefitsDO.setDatasetCountInit(benefitsStrategy.getDatasetCount());
        userBenefitsDO.setImageCountInit(benefitsStrategy.getImageCount());
        userBenefitsDO.setTokenCountInit(benefitsStrategy.getTokenCount());
        userBenefitsDO.setComputationalPowerInit(benefitsStrategy.getComputationalPowerCount());

        userBenefitsDO.setCreator(String.valueOf(userId));
        userBenefitsDO.setUpdater(String.valueOf(userId));

        // 根据策略设置时间
        BenefitsStrategyEffectiveUnitEnums scopeEnums = BenefitsStrategyEffectiveUnitEnums.getByCode(benefitsStrategy.getEffectiveUnit());
        LocalDateTime expirationTime = calculateExpirationTime(startTime, scopeEnums, benefitsStrategy.getEffectiveNum());
        userBenefitsDO.setEffectiveTime(startTime);
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
        LocalDateTime now = LocalDateTime.now();

        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class)
                .eq(UserBenefitsDO::getUserId, userId)
                .eq(UserBenefitsDO::getEnabled, true)
                .le(UserBenefitsDO::getEffectiveTime, now)
                .ge(UserBenefitsDO::getExpirationTime, now);

        List<UserBenefitsDO> resultList = userBenefitsMapper.selectList(wrapper);

        long totalImageCountUsed = 0;
        long totalTokenCountUsed = 0;
        long totalDatasetCountUsed = 0;
        long totalAppCountUsed = 0;
        long totalComputationalPowerRemaining = 0;


        long totalImageCount = 0;
        long totalTokenCount = 0;
        long totalAppCount = 0;
        long totalDatasetCount = 0;
        long totalComputationalPowerCount = 0;

        if (CollUtil.isNotEmpty(resultList)) {

            for (UserBenefitsDO userBenefits : resultList) {
                totalAppCountUsed += userBenefits.getAppRemaining();
                totalDatasetCountUsed += userBenefits.getDatasetRemaining();
                totalImageCountUsed += userBenefits.getImageRemaining();
                totalTokenCountUsed += userBenefits.getTokenRemaining();
                totalComputationalPowerRemaining += userBenefits.getComputationalPowerRemaining();

                totalAppCount += userBenefits.getAppCountInit();
                totalDatasetCount += userBenefits.getDatasetCountInit();
                totalImageCount += userBenefits.getImageCountInit();
                totalTokenCount += userBenefits.getTokenCountInit();
                totalComputationalPowerCount += userBenefits.getComputationalPowerInit();
            }

        }


        userBenefitsInfoResultVO.setQueryTime(now);
        UserLevelEnums userLevelEnums;
        // 根据用户权限判断用户等级
        if (securityFrameworkService.hasRole(UserLevelEnums.PRO.getRoleCode())) {
            userLevelEnums = UserLevelEnums.PRO;
        } else if (securityFrameworkService.hasRole(UserLevelEnums.PLUS.getRoleCode())) {
            userLevelEnums = UserLevelEnums.PLUS;
        } else if (securityFrameworkService.hasRole(UserLevelEnums.BASIC.getRoleCode())) {
            userLevelEnums = UserLevelEnums.BASIC;
        } else {
            userLevelEnums = UserLevelEnums.FREE;
        }
        userBenefitsInfoResultVO.setUserLevel(userLevelEnums.getCode().toLowerCase());


        List<UserBenefitsBaseResultVO> benefitsList = new ArrayList<>();
        // TODO: 2023/6/26
        //  1.暂时取消应用和数据集显示
        //  2.显示顺序 令牌>图片>应用>数据集

        // benefitsList.add(createUserBenefitsBaseResultVO(BenefitsTypeEnums.TOKEN, totalTokenCountUsed, totalTokenCount));
        benefitsList.add(createUserBenefitsBaseResultVO(BenefitsTypeEnums.COMPUTATIONAL_POWER, totalComputationalPowerRemaining, totalComputationalPowerCount));
        benefitsList.add(createUserBenefitsBaseResultVO(BenefitsTypeEnums.IMAGE, totalImageCountUsed, totalImageCount));
        benefitsList.add(createUserBenefitsBaseResultVO(BenefitsTypeEnums.APP, 0, userLevelEnums.getApp()));
        benefitsList.add(createUserBenefitsBaseResultVO(BenefitsTypeEnums.BOT, 0, userLevelEnums.getBot()));
        benefitsList.add(createUserBenefitsBaseResultVO(BenefitsTypeEnums.WECHAT_BOT, 0, userLevelEnums.getWechatBot()));
        benefitsList.add(createUserBenefitsBaseResultVO(BenefitsTypeEnums.BOT_DOCUMENT, 0, userLevelEnums.getBotDocument()));
        benefitsList.add(createUserBenefitsBaseResultVO(BenefitsTypeEnums.SKILL_PLUGIN, 0, userLevelEnums.getSkillPlugin()));
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
     */
    @Override
    @DataPermission(enable = false)
    public void allowExpendBenefits(String benefitsType, Long userId) {
        log.info("[allowExpendBenefits][检测是否存在可扣除的权益：用户ID({})｜权益类型({})", userId, benefitsType);

        UserBaseDTO userInfo = getUserInfo(userId);

        // 校验权益类型是否合法
        BenefitsTypeEnums benefitsTypeEnums = BenefitsTypeEnums.getByCode(benefitsType);
        if (benefitsTypeEnums == null) {
            log.error("[allowExpendBenefits][检测是否存在可扣除的权益失败，权益类型不存在：用户ID({})｜权益类型({})", userId, benefitsType);
            throw exception(USER_BENEFITS_USELESS_INTEREST);
        }

        LocalDateTime now = LocalDateTime.now();

        // 查询条件：当前用户下启用且未过期且权益值大于0的数据
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class);
        wrapper.eq(UserBenefitsDO::getEnabled, true);
        wrapper.le(UserBenefitsDO::getEffectiveTime, now);
        wrapper.ge(UserBenefitsDO::getExpirationTime, now);
        wrapper.eq(UserBenefitsDO::getUserId, userInfo.getUserId());
        wrapper.eq(UserBenefitsDO::getTenantId, userInfo.getTenantId());

        List<UserBenefitsDO> resultList = userBenefitsMapper.selectList(wrapper);
        if (CollUtil.isEmpty(resultList)) {
            log.error("[allowExpendBenefits][不存在可扣除的权益，用户没有可以使用权益 用户ID({})", userId);
            String displayName = benefitsTypeEnums.getDisplayName(LocaleContextHolder.getLocale());
            throw exception(USER_BENEFITS_USELESS_INSUFFICIENT, displayName);

        }


        switch (benefitsTypeEnums) {
            case APP:
                wrapper.gt(UserBenefitsDO::getAppRemaining, 0L);
                break;
            case DATASET:
                wrapper.gt(UserBenefitsDO::getDatasetRemaining, 0L);
                break;
            case IMAGE:
                wrapper.gt(UserBenefitsDO::getImageRemaining, 0L);
                break;
            case TOKEN:
                wrapper.gt(UserBenefitsDO::getTokenRemaining, 0L);
                break;
            case COMPUTATIONAL_POWER:
                wrapper.gt(UserBenefitsDO::getComputationalPowerRemaining, 0L);
                break;
        }
        // 查询数据
        List<UserBenefitsDO> userBenefitsDOList = userBenefitsMapper.selectList(wrapper);
        if (CollUtil.isEmpty(userBenefitsDOList)) {
            log.error("[allowExpendBenefits][不存在可扣除的权益，用户所使用权益为 0：用户ID({})｜权益类型({})", userId, benefitsType);
            String displayName = benefitsTypeEnums.getDisplayName(LocaleContextHolder.getLocale());
            throw exception(USER_BENEFITS_USELESS_INSUFFICIENT, displayName);
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
    @DataPermission(enable = false)
    public void expendBenefits(String benefitsTypeCode, Long amount, Long userId, String outId) {
        log.info("[expendBenefits][权益执行扣减操作：用户ID({})｜权益类型({})|数量({})|外键ID({})", userId, benefitsTypeCode, amount, outId);

        // 根据用户 ID 获取用户信息
        UserBaseDTO userInfo = getUserInfo(userId);

        // 校验权益类型是否合法
        BenefitsTypeEnums benefitsType = BenefitsTypeEnums.getByCode(benefitsTypeCode);
        if (benefitsType == null) {
            log.error("[expendBenefits][权益扣减失败，权益类型不存在：用户ID({})｜权益类型({})|数量({})", userId, benefitsTypeCode, amount);
            throw exception(BENEFITS_TYPE_NOT_EXISTS);
        }

        LocalDateTime now = LocalDateTime.now();
        // 查询条件：当前用户下启用且未过期且权益值大于0的数据
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class)
                .eq(UserBenefitsDO::getEnabled, true)
                .le(UserBenefitsDO::getEffectiveTime, now)
                .ge(UserBenefitsDO::getExpirationTime, now)
                .eq(UserBenefitsDO::getUserId, userInfo.getUserId())
                .eq(UserBenefitsDO::getTenantId, userInfo.getTenantId());

        List<UserBenefitsDO> userBenefitsDOS = userBenefitsMapper.selectList(wrapper);

        List<Long> benefitsIds;

        Function<UserBenefitsDO, Long> getter;
        BiConsumer<UserBenefitsDO, Long> setter;
        switch (benefitsType) {
            case APP:
                wrapper.gt(UserBenefitsDO::getAppRemaining, 0L);
                getter = UserBenefitsDO::getAppRemaining;
                setter = UserBenefitsDO::setAppRemaining;
                break;
            case DATASET:
                wrapper.gt(UserBenefitsDO::getDatasetRemaining, 0L);
                getter = UserBenefitsDO::getDatasetRemaining;
                setter = UserBenefitsDO::setDatasetRemaining;
                break;
            case IMAGE:
                wrapper.gt(UserBenefitsDO::getImageRemaining, 0L);
                getter = UserBenefitsDO::getImageRemaining;
                setter = UserBenefitsDO::setImageRemaining;
                break;
            case TOKEN:
                wrapper.gt(UserBenefitsDO::getTokenRemaining, 0L);
                getter = UserBenefitsDO::getTokenRemaining;
                setter = UserBenefitsDO::setTokenRemaining;
                break;
            case COMPUTATIONAL_POWER:
                wrapper.gt(UserBenefitsDO::getComputationalPowerRemaining, 0L);
                getter = UserBenefitsDO::getComputationalPowerRemaining;
                setter = UserBenefitsDO::setComputationalPowerRemaining;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + benefitsType);
        }
        // 查询数据
        List<UserBenefitsDO> resultList = userBenefitsMapper.selectList(wrapper);
        if (CollUtil.isEmpty(resultList)) {
            log.error("[expendBenefits][权益扣减失败，用户所使用权益为 0：用户ID({})｜权益类型({})|扣除数量({})", userId, benefitsTypeCode, amount);
        }
        // 权益扣除
        List<UserBenefitsDO> usedBenefitsDOS = deduct(resultList, getter, setter, amount);

        // 批量更新数据
        userBenefitsMapper.updateBatch(resultList, resultList.size());

        benefitsIds = usedBenefitsDOS.stream()
                .map(UserBenefitsDO::getId)
                .collect(Collectors.toList());


        UserBenefitsUsageLogCreateReqVO userBenefitsUsageLogCreateReqVO = new UserBenefitsUsageLogCreateReqVO();

        userBenefitsUsageLogCreateReqVO.setUserId(String.valueOf(userId));
        userBenefitsUsageLogCreateReqVO.setBenefitsType(benefitsTypeCode);
        userBenefitsUsageLogCreateReqVO.setAction(BenefitsActionEnums.USED.getCode());
        userBenefitsUsageLogCreateReqVO.setAmount(amount);
        userBenefitsUsageLogCreateReqVO.setUsageTime(LocalDateTime.now());
        userBenefitsUsageLogCreateReqVO.setCreator(String.valueOf(userInfo.getUserId()));
        userBenefitsUsageLogCreateReqVO.setUpdater(String.valueOf(userInfo.getUserId()));
        userBenefitsUsageLogCreateReqVO.setCreateTime(LocalDateTime.now());
        userBenefitsUsageLogCreateReqVO.setTenantId(userInfo.getTenantId());

        if (StrUtil.isNotBlank(outId)) {
            userBenefitsUsageLogCreateReqVO.setOutId(outId);
        }
        userBenefitsUsageLogCreateReqVO.setBenefitsIds(benefitsIds.stream().map(Object::toString).collect(Collectors.joining(",")));

        userBenefitsUsageLogService.createUserBenefitsUsageLog(userBenefitsUsageLogCreateReqVO);
    }

    /**
     * 权益扣除
     *
     * @param resultList 结果 List
     * @param getter     getter
     * @param setter     setter
     * @param amount     增加或者扣减数
     */
    private List<UserBenefitsDO> deduct(List<UserBenefitsDO> resultList, Function<UserBenefitsDO, Long> getter, BiConsumer<UserBenefitsDO, Long> setter, Long amount) {

        long remainingAmount = amount;

        List<UserBenefitsDO> usedBenefitsList = new ArrayList<>(); // 创建用于记录已使用的权益的列表

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
                usedBenefitsList.add(userBenefits); // 将已使用的权益添加到列表中
                break;
            } else {
                usedBenefitsList.add(userBenefits); // 将已使用的权益添加到列表中
            }
        }

        if (remainingAmount > 0) {
            log.warn("[expendBenefits][权益扣减成功，用户剩余权益不足扣除：用户ID({})｜权益类型({})|剩余数量({})", getLoginUserId(), getter.toString(), remainingAmount);
        }
        return usedBenefitsList;

    }

    /**
     * 获取用户权益列表 分页
     *
     * @param pageReqVO 分页查询
     * @return PageResult<UserBenefitsPagInfoResultVO>
     */
    @Override
    public PageResult<UserBenefitsPagInfoResultVO> getUserBenefitsPage(UserBenefitsPageReqVO pageReqVO) {
        PageResult<UserBenefitsPagInfoResultVO> userBenefitsPagInfoResultVOPageResult = new PageResult<>();

        // 获取当前用户权益分页
        pageReqVO.setUserId(Objects.requireNonNull(getLoginUserId()).toString());
        PageResult<UserBenefitsDO> userBenefitsDOPageResult = userBenefitsMapper.selectPage(pageReqVO);

        List<UserBenefitsPagInfoResultVO> resultList = userBenefitsDOPageResult.getList().stream()
                .map(userBenefitsDO -> {
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

                    // 2. 创建UserBenefitsListResultVO对象并添加到结果列表
                    List<UserBenefitsListResultVO> userBenefitsListResultVOS = new ArrayList<>();

                    // 添加Token相关属性
                    String tokenDisplayName = BenefitsTypeEnums.COMPUTATIONAL_POWER.getDisplayName(LocaleContextHolder.getLocale());
                    userBenefitsListResultVOS.add(new UserBenefitsListResultVO(tokenDisplayName, BenefitsTypeEnums.COMPUTATIONAL_POWER.getCode(), userBenefitsDO.getComputationalPowerInit()));

                    // 添加Image相关属性
                    String imageDisplayName = BenefitsTypeEnums.IMAGE.getDisplayName(LocaleContextHolder.getLocale());
                    userBenefitsListResultVOS.add(new UserBenefitsListResultVO(imageDisplayName, BenefitsTypeEnums.IMAGE.getCode(), userBenefitsDO.getImageCountInit()));

                    userBenefitsPagInfoResultVO.setBenefitsList(userBenefitsListResultVOS);
                    return userBenefitsPagInfoResultVO;
                })
                .collect(Collectors.toList());

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
        log.info("[hasSignInBenefitToday][用户签到检测：用户ID({})", userId);

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

        if (CollUtil.isEmpty(userBenefitsDOS)) {
            return false;
        }
        List<String> strategyIds = userBenefitsDOS.stream().map(UserBenefitsDO::getStrategyId).collect(Collectors.toList());
        // 当天新增的是否有签到的权益
        LambdaQueryWrapper<UserBenefitsStrategyDO> userBenefitsStrategyDOLambdaQueryWrapper = Wrappers.lambdaQuery(UserBenefitsStrategyDO.class);
        userBenefitsStrategyDOLambdaQueryWrapper.in(UserBenefitsStrategyDO::getId, strategyIds);
        userBenefitsStrategyDOLambdaQueryWrapper.eq(UserBenefitsStrategyDO::getStrategyType, BenefitsStrategyTypeEnums.USER_ATTENDANCE.getName());
        boolean result = BooleanUtil.isTrue(userBenefitsStrategyMapper.selectCount(userBenefitsStrategyDOLambdaQueryWrapper) > 0);
        log.info("[hasSignInBenefitToday][用户签到检测结果：用户ID({})｜当天是否已经签到({})", userId, result);
        return result;
    }

    /**
     * 新增用户权益
     *
     * @param benefitsType 权益 type
     * @param userId       用户 ID
     * @return 编号
     */
    @Override
    @Transactional
    public Boolean addBenefitsAndRole(String benefitsType, Long userId, String roleCode) {
        // 增加用户权益
        this.addUserBenefitsByStrategyType(benefitsType, userId);
        // 设置用户角色
        permissionService.addUserRole(userId, roleCode);

        return Boolean.TRUE;
    }

    /**
     * 用户有邀请码的情况--增加权益
     *
     * @param inviteUserId  邀请人 ID
     * @param currentUserId 被邀请人 ID
     */
    @Transactional
    public void addUserBenefitsInvitation(Long inviteUserId, Long currentUserId) {
        // 邀请人
        log.info("[addUserBenefitsInvitation][增加邀请注册权益：邀请人用户ID({})｜被邀请人({})],开始为邀请人增加权益", inviteUserId, currentUserId);
        Boolean inviteResult = this.addUserBenefitsByStrategyType(BenefitsStrategyTypeEnums.USER_INVITE.getName(), inviteUserId);
        log.info("[addUserBenefitsInvitation][邀请人增加权益结束：邀请人用户ID({})｜增加状态为({})],开始为被邀请人增加权益", inviteUserId, inviteResult);
        // 被邀请人
        Boolean inviteRegisterResult = this.addUserBenefitsByStrategyType(BenefitsStrategyTypeEnums.INVITE_TO_REGISTER.getName(), currentUserId);
        log.info("[addUserBenefitsInvitation][增加被邀请人权益：邀请人用户ID({})｜被邀请人({})]｜增加状态为({})],权益增加结束", inviteUserId, currentUserId, inviteRegisterResult);
    }

    /**
     * 用户普通注册--增加权益
     *
     * @param userId 用户 ID
     */
    @Transactional
    public void addUserBenefitsSign(Long userId) {
        log.info("[addUserBenefitsSign][增加注册权益：注册人用户ID({})]", userId);
        // 普通注册权益
        Boolean signResult = this.addUserBenefitsByStrategyType(BenefitsStrategyTypeEnums.SIGN_IN.getName(), userId);
        log.info("[addUserBenefitsSign][增加注册权益结束：注册人用户ID({})｜增加状态为({})]", userId, signResult);
    }

    /**
     * 根据类型获取有效的权益总量
     *
     * @param benefitsType
     * @param userId
     */
    @Override
    public UserBenefitsBaseResultVO getBenefitsByType(String benefitsType, Long userId) {

        UserBenefitsBaseResultVO resultVO = new UserBenefitsBaseResultVO();
        // 查询条件：当前用户下启用的权益，并且未过期
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class)
                .eq(UserBenefitsDO::getUserId, userId)
                .eq(UserBenefitsDO::getEnabled, true)
                .le(UserBenefitsDO::getEffectiveTime, LocalDateTime.now())
                .ge(UserBenefitsDO::getExpirationTime, LocalDateTime.now());

        List<UserBenefitsDO> resultList = userBenefitsMapper.selectList(wrapper);

        if (CollUtil.isNotEmpty(resultList)) {
            BenefitsTypeEnums benefitsTypeEnums = BenefitsTypeEnums.valueOf(benefitsType);
            resultVO.setName(benefitsTypeEnums.getChineseName());
            resultVO.setType(benefitsTypeEnums.getCode());
            switch (benefitsTypeEnums) {
                case TOKEN:
                    resultVO.setRemaining(resultList.stream().mapToLong(UserBenefitsDO::getTokenRemaining).sum());
                    resultVO.setTotalNum(resultList.stream().mapToLong(UserBenefitsDO::getTokenCountInit).sum());
                    break;
                case IMAGE:
                    resultVO.setRemaining(resultList.stream().mapToLong(UserBenefitsDO::getImageRemaining).sum());
                    resultVO.setTotalNum(resultList.stream().mapToLong(UserBenefitsDO::getImageRemaining).sum());
                    break;
                case COMPUTATIONAL_POWER:
                    resultVO.setRemaining(resultList.stream().mapToLong(UserBenefitsDO::getComputationalPowerRemaining).sum());
                    resultVO.setTotalNum(resultList.stream().mapToLong(UserBenefitsDO::getComputationalPowerInit).sum());
                    break;
                default:
                    break;
            }
            return resultVO;

        }
        resultVO.setRemaining(0L);
        return resultVO;

    }

    /**
     * 获取七天内即将过期的权益
     */
    @Override
    public ExpiredReminderVO getBenefitsExpired() {
        ExpiredReminderVO expiredReminderVO = new ExpiredReminderVO();

        UserLevelVO userLevel = new UserLevelVO();
        UserBenefits userBenefits = new UserBenefits();
        UserTokenExpiredReminderVO tokenExpiredReminderVO = new UserTokenExpiredReminderVO();
        // 获取用户 7 天内过期的所有权益
        // 查询条件：当前用户下启用的权益，并且未过期
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class)
                .eq(UserBenefitsDO::getUserId, getLoginUserId())
                .eq(UserBenefitsDO::getEnabled, true)
                .le(UserBenefitsDO::getEffectiveTime, now)
                .ge(UserBenefitsDO::getExpirationTime, now);

        List<UserBenefitsDO> resultList = userBenefitsMapper.selectList(wrapper);

        // 获取用户等级过期的数据
        List<UserBenefitsStrategyDO> payBenefitsStrategy = userBenefitsStrategyService.getPayBenefitsStrategy();
        List<Long> payBenefitsStrategyId = payBenefitsStrategy.stream().map(UserBenefitsStrategyDO::getId).collect(Collectors.toList());

        UserBenefitsDO userLevelDO = resultList.stream()
                .filter(obj -> CollUtil.contains(payBenefitsStrategyId, Long.valueOf(obj.getStrategyId()))).max(Comparator.comparing(UserBenefitsDO::getExpirationTime)) // 使用findFirst来获取第一个匹配的元素
                .orElse(null);// 如果没有匹配的元素，返回null


        if (userLevelDO != null && LocalDateTimeUtil.isIn(userLevelDO.getExpirationTime(), now, now.plusDays(7))) {
            // 根据用户权限判断用户等级
            if (securityFrameworkService.hasRole(UserLevelEnums.PRO.getRoleCode())) {
                userLevel.setUserLevel(UserLevelEnums.PRO.getCode());
            } else if (securityFrameworkService.hasRole(UserLevelEnums.PLUS.getRoleCode())) {
                userLevel.setUserLevel(UserLevelEnums.PLUS.getCode());
            } else if (securityFrameworkService.hasRole(UserLevelEnums.BASIC.getRoleCode())) {
                userLevel.setUserLevel(UserLevelEnums.BASIC.getCode());
            } else {
                userLevel.setUserLevel(UserLevelEnums.FREE.getCode());
            }

            userLevel.setName(userBenefitsStrategyService.getUserBenefitsStrategy(Long.valueOf(userLevelDO.getStrategyId())).getStrategyType());
            userLevel.setExpirationTime(userLevelDO.getExpirationTime());
        }
        expiredReminderVO.setUserLevel(userLevel);


        // 计算 Token 不足
        List<UserBenefitsStrategyDO> noPayBenefitsStrategy = userBenefitsStrategyService.getNoPayBenefitsStrategy();
        List<Long> noPayBenefitsStrategyId = noPayBenefitsStrategy.stream().map(UserBenefitsStrategyDO::getId).collect(Collectors.toList());

        long sum = resultList.stream().mapToLong(UserBenefitsDO::getComputationalPowerRemaining).sum();

        tokenExpiredReminderVO.setName(BenefitsTypeEnums.COMPUTATIONAL_POWER.getCode());
        tokenExpiredReminderVO.setIsReminder(sum > 20 ? false : true);
        tokenExpiredReminderVO.setExpiredNum(sum > 20 ? 0 : sum);
        expiredReminderVO.setTokenExpiredReminderVO(tokenExpiredReminderVO);

        UserBenefitsDO userBenefitsDO = resultList.stream()
                .filter(obj -> CollUtil.contains(noPayBenefitsStrategyId, Long.valueOf(obj.getStrategyId())) && obj.getTokenRemaining() > 0).min(Comparator.comparing(UserBenefitsDO::getExpirationTime)) // 使用findFirst来获取第一个匹配的元素
                .orElse(null);// 如果没有匹配的元素，返回null

        // 计算 Token 过期
        if (userBenefitsDO != null && LocalDateTimeUtil.isIn(userBenefitsDO.getExpirationTime(), now, now.plusDays(7))) {
            userBenefits.setName(userBenefitsStrategyService.getUserBenefitsStrategy(Long.valueOf(userBenefitsDO.getStrategyId())).getStrategyType());
            userBenefits.setExpirationTime(userBenefitsDO.getExpirationTime());
        }

        expiredReminderVO.setUserBenefits(userBenefits);

        return expiredReminderVO;
    }

    /**
     * 权益过期处理-返回已处理的数据
     */
    @Override
    public Long userBenefitsExpired() {
        log.info("开始执行权益过期任务");
        // 获取用户支付权益策略
        List<UserBenefitsStrategyDO> payBenefitsStrategyS = userBenefitsStrategyService.getPayBenefitsStrategy();
        // 获取支付权益策略 ID
        List<Long> payBenefitsStrategyId = payBenefitsStrategyS.stream().map(UserBenefitsStrategyDO::getId).collect(Collectors.toList());

        // 获取所有过期权益
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class)
                .eq(UserBenefitsDO::getEnabled, true)
                .le(UserBenefitsDO::getEffectiveTime, now)
                .le(UserBenefitsDO::getExpirationTime, now);


        List<UserBenefitsDO> resultList = userBenefitsMapper.selectList(wrapper);

        if (resultList.isEmpty()) {
            log.info("当前时间段没有获取到已经过期的权益数据，停止执行权益过期任务");
            return 0L;
        }

        // 更新普通权益
        List<Long> noPayBenefitsIds = resultList.stream()
                .filter(obj -> !CollUtil.contains(payBenefitsStrategyId, Long.valueOf(obj.getStrategyId()))).map(UserBenefitsDO::getId).collect(Collectors.toList());

        if (noPayBenefitsIds.size() > 0) {
            log.info("获取到{}条已经过期的普通权益数据，更新权益状态，过期的权益 ID 为{}", noPayBenefitsIds.size(), noPayBenefitsIds);
            userBenefitsMapper.update(null, Wrappers.lambdaUpdate(UserBenefitsDO.class)
                    .in(UserBenefitsDO::getId, noPayBenefitsIds)
                    .set(UserBenefitsDO::getEnabled, 0));
        }


        // 获取支付权益
        List<UserBenefitsDO> payBenefitsS = resultList.stream()
                .filter(obj -> CollUtil.contains(payBenefitsStrategyId, Long.valueOf(obj.getStrategyId()))).collect(Collectors.toList());

        if (!payBenefitsS.isEmpty()) {

            log.info("当前时间段获取到已经过期的【支付权益】数据{}，执行权益过期任务与更新角色任务", payBenefitsS);
            List<Long> basicConfigIds = payBenefitsStrategyS.stream()
                    .filter(obj -> obj.getStrategyType().equals(BenefitsStrategyTypeEnums.PAY_BASIC_MONTH.getName()) ||
                            obj.getStrategyType().equals(BenefitsStrategyTypeEnums.PAY_BASIC_YEAR.getName()))
                    .map(UserBenefitsStrategyDO::getId)
                    .collect(Collectors.toList());

            List<Long> plusConfigIds = payBenefitsStrategyS.stream()
                    .filter(obj -> obj.getStrategyType().equals(BenefitsStrategyTypeEnums.PAY_PLUS_MONTH.getName()) ||
                            obj.getStrategyType().equals(BenefitsStrategyTypeEnums.PAY_PLUS_YEAR.getName()))
                    .map(UserBenefitsStrategyDO::getId)
                    .collect(Collectors.toList());

            List<Long> proConfigIds = payBenefitsStrategyS.stream()
                    .filter(obj -> obj.getStrategyType().equals(BenefitsStrategyTypeEnums.PAY_PRO_MONTH.getName()) ||
                            obj.getStrategyType().equals(BenefitsStrategyTypeEnums.PAY_PRO_YEAR.getName()))
                    .map(UserBenefitsStrategyDO::getId)
                    .collect(Collectors.toList());


            for (UserBenefitsDO obj : resultList) {
                List<UserBenefitsDO> payBenefitsByUserId = getPayBenefitsByUserId(Long.valueOf(obj.getUserId()));

                List<UserBenefitsDO> plusBenefits = payBenefitsByUserId.stream()
                        .filter(o -> plusConfigIds.contains(o.getStrategyId()))
                        .collect(Collectors.toList());

                if (plusBenefits.isEmpty()) {
                    log.info("用户【{}】权益高级版过期，清除 PLUS角色", obj.getUserId());
                    RoleDO roleByCode = roleService.getRoleByCode(UserLevelEnums.PLUS.getRoleCode());
                    permissionService.processRoleDeleted(Long.valueOf(obj.getUserId()), roleByCode.getId());
                    // 设置用户角色
                    permissionService.addUserRole(Long.valueOf(obj.getUserId()), UserLevelEnums.FREE.getRoleCode());
                }

                List<UserBenefitsDO> proBenefits = payBenefitsByUserId.stream()
                        .filter(o -> proConfigIds.contains(o.getStrategyId()))
                        .collect(Collectors.toList());

                if (proBenefits.isEmpty()) {
                    log.info("用户【{}】权益团队版过期，清除 PRO角色", obj.getUserId());
                    RoleDO roleByCode = roleService.getRoleByCode(UserLevelEnums.PRO.getRoleCode());
                    permissionService.processRoleDeleted(Long.valueOf(obj.getUserId()), roleByCode.getId());
                    // 设置用户角色
                    permissionService.addUserRole(Long.valueOf(obj.getUserId()), UserLevelEnums.FREE.getRoleCode());
                }

                List<UserBenefitsDO> basicBenefits = payBenefitsByUserId.stream()
                        .filter(o -> basicConfigIds.contains(o.getStrategyId()))
                        .collect(Collectors.toList());

                if (basicBenefits.isEmpty()) {
                    log.info("用户【{}】权益基础版过期，清除 Basic角色", obj.getUserId());
                    RoleDO roleByCode = roleService.getRoleByCode(UserLevelEnums.BASIC.getRoleCode());
                    permissionService.processRoleDeleted(Long.valueOf(obj.getUserId()), roleByCode.getId());
                    // 设置用户角色
                    permissionService.addUserRole(Long.valueOf(obj.getUserId()), UserLevelEnums.FREE.getRoleCode());
                }

                userBenefitsMapper.update(null, Wrappers.lambdaUpdate(UserBenefitsDO.class)
                        .eq(UserBenefitsDO::getId, obj.getId())
                        .set(UserBenefitsDO::getEnabled, 0));
            }

        } else {
            log.info("当前时间段没有获取到已经过期的【支付权益】数据，停止执行权益过期任务");
        }


        return (long) resultList.size();
    }

    /**
     * 获取用户的支付权益列表
     *
     * @param userId 用户 ID
     * @return
     */
    @Override
    public List<UserBenefitsDO> getPayBenefitList(Long userId) {
        // 获取用户支付权益策略
        List<UserBenefitsStrategyDO> payBenefitsStrategyS = userBenefitsStrategyService.getPayBenefitsStrategy();
        // 获取支付权益策略 ID
        List<Long> payBenefitsStrategyId = payBenefitsStrategyS.stream().map(UserBenefitsStrategyDO::getId).collect(Collectors.toList());
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class)
                .in(UserBenefitsDO::getStrategyId, payBenefitsStrategyId);

        return userBenefitsMapper.selectList(wrapper);

    }

    /**
     * 折扣优惠是否可用
     *
     * @param productCode  产品 code
     * @param discountCode 优惠码
     * @param userId       用户 ID
     * @return true        可用 false 不可用
     */
    @Override
    public Boolean validateDiscount(String productCode, String discountCode, Long userId) {
        log.info("用户【{}】使用优惠码【{}】对应的的产品代码为【{}】", userId, discountCode, productCode);
        Assert.notBlank(productCode, "判断优惠码是否有效失败，产品代码不可以为空");
        Assert.notBlank(discountCode, "判断优惠码是否有效失败,优惠代码不可以为空");

        UserBenefitsStrategyDO benefitsStrategy;
        // 使用限制校验
        try {
            benefitsStrategy = userBenefitsStrategyService.getUserBenefitsStrategy(discountCode);
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
        } catch (RuntimeException e) {
            log.warn("用户【{}】无法使用优惠码【{}】对应的的产品代码为【{}】，当前优惠码超过使用限制", userId, discountCode, productCode);
            return false;
        }

        BenefitsStrategyTypeEnums discountCodeEnums = BenefitsStrategyTypeEnums.getByCode(benefitsStrategy.getStrategyType());
        // 适用产品校验
        BenefitsStrategyTypeEnums[] limitDiscountByCode = ProductEnum.getLimitDiscountByCode(productCode);

        Optional<BenefitsStrategyTypeEnums> anyMatchResult = Arrays.stream(limitDiscountByCode)
                .filter(discountCodeEnums::equals)
                .findFirst();

        if (!anyMatchResult.isPresent()) {
            return false;
        }
        return true;
    }

    /**
     * 计算优惠后的价格
     *
     * @param productCode  产品 code
     * @param discountCode 优惠码
     * @return 优惠后的价格
     */
    @Override
    public Long calculateDiscountPrice(String productCode, String discountCode) {
        //  商品信息
        ProductEnum product = ProductEnum.getByCode(productCode);

        UserBenefitsStrategyDO userBenefitsStrategy = userBenefitsStrategyService.getUserBenefitsStrategy(discountCode);
        // 优惠码信息
        BenefitsStrategyTypeEnums discount = BenefitsStrategyTypeEnums.getByCode(userBenefitsStrategy.getStrategyType());
        Long discountPrice = 0L;
        switch (discount.getDiscountTypeEnums()) {
            case DIRECT_DISCOUNT:
                discountPrice = (long) (product.getPrice() - discount.getDiscountNums() * 100);
                break;
            case PERCENTAGE_DISCOUNT:
                discountPrice = (long) (product.getPrice() * discount.getDiscountNums());
                break;
        }
        if (discountPrice <= 0) {
            log.error("优惠价格计算错误");
            discountPrice = Long.valueOf(product.getPrice());
        }

        return discountPrice;
    }

    /**
     * 根据用户 ID 获取用户信息
     *
     * @param userId 用户 ID
     * @return UserBaseDTO
     */
    private UserBaseDTO getUserInfo(Long userId) {
        Assert.notNull(userId, "用户信息为空，权益操作失败");
        AdminUserDO user = adminUserService.getUser(userId);
        if (user == null) {
            throw exception(USER_BENEFITS_OPERATION_FAIL_NO_USER);
        }
        return new UserBaseDTO().setUserId(userId).setTenantId(user.getTenantId());
    }

    /**
     * 根据用户ID 获取当前生效的支付权益
     *
     * @param userId
     * @return
     */
    private List<UserBenefitsDO> getPayBenefitsByUserId(Long userId) {

        // 获取用户支付权益策略
        List<UserBenefitsStrategyDO> payBenefitsStrategy = userBenefitsStrategyService.getPayBenefitsStrategy();
        // 获取支付权益策略 ID
        List<Long> payBenefitsStrategyIds = payBenefitsStrategy.stream().map(UserBenefitsStrategyDO::getId).collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<UserBenefitsDO> wrapper = Wrappers.lambdaQuery(UserBenefitsDO.class)
                .eq(UserBenefitsDO::getUserId, userId)
                .eq(UserBenefitsDO::getEnabled, true)
                .in(UserBenefitsDO::getStrategyId, payBenefitsStrategyIds)
                .le(UserBenefitsDO::getEffectiveTime, now)
                .ge(UserBenefitsDO::getExpirationTime, now);
        return userBenefitsMapper.selectList(wrapper);
    }

}
