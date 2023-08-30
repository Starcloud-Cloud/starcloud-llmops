package com.starcloud.ops.business.app.service.limit.impl;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.limit.dto.LimitConfigDTO;
import com.starcloud.ops.business.app.api.log.vo.request.AppLogMessageQuery;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.limit.AppLimitContext;
import com.starcloud.ops.business.app.service.limit.AppLimitService;
import com.starcloud.ops.business.app.service.limit.AppPublishLimitService;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageRespVO;
import com.starcloud.ops.framework.common.api.dto.SortQuery;
import com.starcloud.ops.framework.common.api.enums.SortType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-29
 */
@Slf4j
@Service
public class AppLimitServiceImpl implements AppLimitService {

    private static final String SYSTEM_LIMIT_APP_PREFIX = "APP:";

    private static final String SYSTEM_LIMIT_MARKET_PREFIX = "MARKET:";


    @Resource
    private AppDictionaryService appDictionaryService;

    @Resource
    private AppPublishLimitService appPublishLimitService;

    @Resource
    private AppLogService appLogService;

    @Resource
    private RedissonClient redissonClient;


    /**
     * 应用限流，应用执行限流，走系统默认限流
     *
     * @param appUid 应用唯一标识
     */
    @Override
    public void appLimit(String appUid) {

    }

    /**
     * 应用限流，应用市场执行限流，走系统默认限流
     *
     * @param marketUid 应用唯一标识
     */
    @Override
    public void marketLimit(String marketUid) {

    }

    /**
     * 应用限流，应用发布渠道执行限流。走用户配置限流。系统默认限流兜底
     *
     * @param mediumUid 应用唯一标识
     */
    @Override
    public void channelLimit(String mediumUid) {

    }

    /**
     * 系统默认限流
     *
     * @param appUid 应用唯一标识
     */
    private void doSystemLimit(String appUid, String fromScene) {
        List<LimitConfigDTO> limitConfigList = appDictionaryService.appSystemLimitConfig();
        List<AppLimitContext> limitContextList = new ArrayList<>();

        AppLimitContext quotaLimitContext = new AppLimitContext();
        quotaLimitContext.setLimitKey("");
        quotaLimitContext.setAppUid(appUid);
        limitContextList.add(quotaLimitContext);


    }


    private void doLimit(AppLimitContext context) {
        // 获取总量定额限流配置
        LimitConfigDTO config = context.getConfig();
        // 如果没有开启限流 或者没有配置限流数量，或者限流数量小于等于 0 。则不限流
        if (!config.getEnable() || config.getLimit() == null || config.getLimit() <= 0) {
            return;
        }

        RLock lock = redissonClient.getLock(context.getLimitKey());
        // 如果获取锁失败，直接抛出异常。
        if (!lock.tryLock()) {
            throw exception("系统繁忙，请稍后再试！");
        }
        try {
            // 从 Redis 中获取当前的限流数量
            RBucket<Integer> bucket = redissonClient.getBucket(context.getLimitKey());
            ChronoUnit timeUnit = ChronoUnit.valueOf(config.getTimeUnit());
            // 将配置中的时间转换成毫秒
            long millisecond = transformMillisecond(config.getTimeout(), config.getTimeUnit());

            // 如果 Key 不存在，则设置初始值
            if (!bucket.isExists()) {
                // 查询日志消息表中的已经执行的数量
                AppLogMessageQuery appLogMessageQuery = new AppLogMessageQuery();
                appLogMessageQuery.setAppUid(context.getAppUid());
                appLogMessageQuery.setUserId(context.getUserId());
                appLogMessageQuery.setEndUser(context.getEndUser());
                appLogMessageQuery.setFromScene(context.getFromScene());
                appLogMessageQuery.setTimeInterval(config.getTimeout());
                appLogMessageQuery.setTimeUnit(timeUnit);
                appLogMessageQuery.setPageNo(1);
                appLogMessageQuery.setPageSize(1);
                appLogMessageQuery.setSorts(Collections.singletonList(SortQuery.of("create_time", SortType.ASC.name())));
                Page<LogAppMessageRespVO> page = appLogService.pageAppLogMessage(appLogMessageQuery);

                // 如果已经执行的数量大于 0，则设置已经执行的数量，限流数量恢复
                if (page.getTotal() > 0) {
                    // 如果已经执行的数量大于等于限流数量，说明已经超过限流数量，直接抛出异常
                    if (page.getTotal() >= config.getLimit()) {
                        throw exception(config.getMessage());
                    }

                    // 如果已经执行的数量小于限流数量，说明已经超过限流数量，恢复限流数量
                    int quota = Long.valueOf(page.getTotal()).intValue() + 1;

                    // 获取第一条的执行记录，过期时间 = millisecond - (现在时间 - 第一次的执行时间)
                    LocalDateTime firstTime = Optional.ofNullable(page.getRecords()).map(records -> records.get(0))
                            .map(LogAppMessageRespVO::getCreateTime).orElse(LocalDateTime.now());
                    long expire = millisecond - (System.currentTimeMillis() - firstTime.toInstant(ZoneOffset.UTC).toEpochMilli());
                    //  如果过期时间小于等于 0 ，则重置为 millisecond
                    expire = expire <= 0L ? millisecond : expire;

                    // 恢复限流数量
                    bucket.set(quota, expire, TimeUnit.MILLISECONDS);
                    return;
                }
                // 说明未执行过，初始化限流数量。
                bucket.set(1, millisecond, TimeUnit.MILLISECONDS);
                return;
            }

            // 获取当前的限流数量和过期时间
            Integer currentQuota = bucket.get();
            long expire = bucket.remainTimeToLive();
            // 如果没有超过限流数量，说明已经超过限流数量，直接抛出异常
            if (currentQuota >= config.getLimit()) {
                throw exception(config.getMessage());
            }

            // 增加 1 个限流数量
            bucket.set(currentQuota + 1, expire, TimeUnit.MILLISECONDS);
        } catch (Exception exception) {
            log.error("定额限流异常：{}", exception.getMessage());
            throw exception;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 转换毫秒
     *
     * @param timeout  时间
     * @param timeUnit 时间单位
     * @return 毫秒
     */
    private Long transformMillisecond(Long timeout, String timeUnit) {
        Duration duration = Duration.of(timeout, ChronoUnit.valueOf(timeUnit));
        return duration.toMillis();
    }


    private static String getLimitKey(String prefix, String appUid, String userId, String fromScene) {
        StringBuilder sb = new StringBuilder(prefix);
        sb.append(appUid);
        sb.append(":");
        sb.append(fromScene);
        sb.append(":");
        if (StringUtils.isNotBlank(userId)) {
            sb.append(userId);
        }
        return sb.toString();
    }

    /**
     * 异常
     *
     * @param message 异常信息
     * @return 异常
     */
    private static ServiceException exception(String message) {
        return ServiceExceptionUtil.exception(new ErrorCode(300900000, message));
    }
}
