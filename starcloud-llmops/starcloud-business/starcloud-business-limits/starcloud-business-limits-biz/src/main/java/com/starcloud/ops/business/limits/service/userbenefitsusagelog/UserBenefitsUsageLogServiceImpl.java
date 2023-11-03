package com.starcloud.ops.business.limits.service.userbenefitsusagelog;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo.UserBenefitsUsageLogCreateReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo.UserBenefitsUsageLogPageReqVO;
import com.starcloud.ops.business.limits.convert.userbenefitsusagelog.UserBenefitsUsageLogConvert;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefits.UserBenefitsDO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsusagelog.UserBenefitsUsageLogDO;
import com.starcloud.ops.business.limits.dal.mysql.userbenefitsusagelog.UserBenefitsUsageLogMapper;
import com.starcloud.ops.business.limits.enums.BenefitsActionEnums;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.limits.enums.ErrorCodeConstants.*;

/**
 * 用户权益使用日志 Service 实现类
 *
 * @author AlanCusack
 */
@Slf4j
@Service
@Validated
public class UserBenefitsUsageLogServiceImpl implements UserBenefitsUsageLogService {

    @Resource
    private UserBenefitsUsageLogMapper userBenefitsUsageLogMapper;

    /**
     *
     * @param createReqVO 创建信息
     *
     * @return
     */
    @Override
    public Long createUserBenefitsUsageLog(UserBenefitsUsageLogCreateReqVO createReqVO) {

        log.info("[createUserBenefitsUsageLog][创建用户权益使用日志，用户ID({})|操作:({})：策略ID({})|数量({})", createReqVO.getUserId(), createReqVO.getAction(), createReqVO.getBenefitsIds(), createReqVO.getAmount());
        // 操作校验
        validateActionTypeExists(createReqVO.getAction());
        // 权益类型校验
        validateBenefitsTypeExists(createReqVO.getBenefitsType());

        // 插入
        UserBenefitsUsageLogDO userBenefitsUsageLog = UserBenefitsUsageLogConvert.INSTANCE.InsertConvert(createReqVO);
        userBenefitsUsageLogMapper.insert(userBenefitsUsageLog);
        // 返回
        return userBenefitsUsageLog.getId();
    }

    /**
     * 批量增加权益日志
     * @param userBenefitsDO
     * @param benefitsStrategy
     *
     * @return
     */
    @Override
    public Boolean batchCreateUserBenefitsUsageBatchLog(UserBenefitsDO userBenefitsDO, UserBenefitsStrategyDO benefitsStrategy) {
        LocalDateTime now = LocalDateTimeUtil.now();

        List<UserBenefitsUsageLogDO> collect = Stream.of(

                createUserBenefitsUsageLog(userBenefitsDO, BenefitsTypeEnums.APP, benefitsStrategy.getAppCount(), now),
                createUserBenefitsUsageLog(userBenefitsDO, BenefitsTypeEnums.DATASET, benefitsStrategy.getDatasetCount(), now),
                createUserBenefitsUsageLog(userBenefitsDO, BenefitsTypeEnums.IMAGE, benefitsStrategy.getImageCount(), now),
                createUserBenefitsUsageLog(userBenefitsDO, BenefitsTypeEnums.TOKEN, benefitsStrategy.getTokenCount(), now)
        ).collect(Collectors.toList());
        userBenefitsUsageLogMapper.insertBatch(collect);
        return true;
    }

    private UserBenefitsUsageLogDO createUserBenefitsUsageLog(UserBenefitsDO userBenefitsDO, BenefitsTypeEnums benefitsType, Long amount, LocalDateTime usageTime) {
        UserBenefitsUsageLogDO usageLog = new UserBenefitsUsageLogDO();
        usageLog.setUserId(userBenefitsDO.getUserId());
        usageLog.setAction(BenefitsActionEnums.ADD.getCode());
        usageLog.setBenefitsIds(String.valueOf(userBenefitsDO.getId()));
        usageLog.setUsageTime(usageTime);
        usageLog.setBenefitsType(benefitsType.getCode());
        usageLog.setAmount(amount);
        usageLog.setCreator(userBenefitsDO.getCreator());
        usageLog.setUpdater(userBenefitsDO.getUpdater());
        usageLog.setTenantId(userBenefitsDO.getTenantId());
        return usageLog;
    }


    private void validateUserBenefitsUsageLogExists(Long id) {
        if (userBenefitsUsageLogMapper.selectById(id) == null) {
            throw exception(BENEFITS_USAGE_LOG_NOT_EXISTS);
        }
    }



    @Override
    public PageResult<UserBenefitsUsageLogDO> getUserBenefitsUsageLogPage(UserBenefitsUsageLogPageReqVO pageReqVO) {
        return userBenefitsUsageLogMapper.selectPage(pageReqVO);
    }

    /**
     * 根据时间和 Action 获取指定的权益记录
     *
     * @param queryTime 查询时间 如果查询时间为空 则查询所有的action 记录，不建议为空
     * @param action
     *
     * @return 用户权益使用日志
     */
    @Override
    public List<UserBenefitsUsageLogDO> getUserBenefitsUsageLog(LocalDateTime queryTime, String action) {
        // 设置查询条件
        LambdaQueryWrapper<UserBenefitsUsageLogDO> wrapper = Wrappers.lambdaQuery(UserBenefitsUsageLogDO.class);

        // 设置 action 查询条件
        wrapper.eq(UserBenefitsUsageLogDO::getAction, action);

        // 设置 queryTime 查询条件
        if (queryTime != null) {
            LocalDateTime startOfDay = queryTime.with(LocalTime.MIN);
            LocalDateTime endOfDay = queryTime.with(LocalTime.MAX);
            wrapper.between(UserBenefitsUsageLogDO::getCreateTime, startOfDay, endOfDay);
        }

        // 执行查询并返回结果
        return userBenefitsUsageLogMapper.selectList(wrapper);
    }

    /**
     * 验证操作类型是否合法
     *
     * @param actionCode 操作枚举 Code
     */
    private void validateActionTypeExists(String actionCode) {
        if (BenefitsActionEnums.getByCode(actionCode) == null) {
            throw exception(BENEFITS_USAGE_LOG_ACTION_TYPE_NOT_EXISTS);
        }
    }

    /**
     * 验证权益类型是否合法
     *
     * @param actionCode 操作枚举 Code
     */
    private void validateBenefitsTypeExists(String actionCode) {
        if (BenefitsTypeEnums.getByCode(actionCode) == null) {
            throw exception(BENEFITS_USAGE_LOG_BENEFITS_TYPE_NOT_EXISTS);
        }
    }

}
