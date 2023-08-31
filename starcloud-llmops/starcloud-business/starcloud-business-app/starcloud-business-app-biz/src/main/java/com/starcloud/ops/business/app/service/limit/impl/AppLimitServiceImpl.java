package com.starcloud.ops.business.app.service.limit.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.api.limit.dto.LimitConfigDTO;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;
import com.starcloud.ops.business.app.api.log.vo.request.AppLogMessageQuery;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.limit.LimitByEnum;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.limit.AppLimitContext;
import com.starcloud.ops.business.app.service.limit.AppLimitRequest;
import com.starcloud.ops.business.app.service.limit.AppLimitService;
import com.starcloud.ops.business.app.service.limit.AppPublishLimitService;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageRespVO;
import com.starcloud.ops.framework.common.api.dto.SortQuery;
import com.starcloud.ops.framework.common.api.enums.SortType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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

    private static final String APP_LIMIT_PREFIX = "APP:";

    private static final String MARKET_LIMIT_PREFIX = "MARKET:";

    private static final String ADVERTISING_PREFIX = "ADVERTISING:";

    @Resource
    private AppDictionaryService appDictionaryService;

    @Resource
    private AppPublishLimitService appPublishLimitService;

    @Resource
    private AppPublishChannelService appPublishChannelService;

    @Resource
    private AppLogService appLogService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 应用限流，应用执行限流，走系统默认限流
     *
     * @param request 请求数据
     */
    @Override
    public void appLimit(AppLimitRequest request) {
        if (StringUtils.isBlank(request.getAppUid())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_UID_IS_REQUIRED);
        }
        if (StringUtils.isBlank(request.getFromScene())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_SCENE_IS_REQUIRED);
        }
        log.info("应用限流开始：应用UID: {}, 执行场景: {}", request.getAppUid(), request.getFromScene());
        doSystemLimit(APP_LIMIT_PREFIX, request.getAppUid(), request.getFromScene());
        log.info("应用限流结束");
    }

    /**
     * 应用限流，应用执行限流，走系统默认限流
     *
     * @param request 请求数据
     * @param emitter sse
     */
    @Override
    public void appLimitSse(AppLimitRequest request, SseEmitter emitter) {

    }

    /**
     * 应用限流，应用市场执行限流，走系统默认限流
     *
     * @param request 请求数据
     */
    @Override
    public void marketLimit(AppLimitRequest request) {
        if (StringUtils.isBlank(request.getAppUid())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_UID_IS_REQUIRED);
        }
        if (StringUtils.isBlank(request.getFromScene())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_SCENE_IS_REQUIRED);
        }
        log.info("应用市场限流开始：应用市场UID: {}, 执行场景: {}", request.getAppUid(), request.getFromScene());
        doSystemLimit(MARKET_LIMIT_PREFIX, request.getAppUid(), request.getFromScene());
        log.info("应用市场限流结束");
    }

    /**
     * 应用限流，应用发布渠道执行限流。走用户配置限流。系统默认限流兜底
     *
     * @param request 请求数据
     */
    @Override
    public void channelLimit(AppLimitRequest request) {
        if (StringUtils.isBlank(request.getMediumUid())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MEDIUM_UID_IS_REQUIRED);
        }
        if (StringUtils.isBlank(request.getFromScene())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_SCENE_IS_REQUIRED);
        }
        if (StringUtils.isBlank(request.getEndUser())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_END_USER_IS_REQUIRED);
        }
        log.info("应用渠道发布限流开始：应用渠道媒介ID: {}, 执行场景: {}, 游客ID: {}", request.getMediumUid(), request.getFromScene(), request.getEndUser());
        AppPublishChannelRespVO channel = appPublishChannelService.getByMediumUid(request.getMediumUid());
        AppValidate.notNull(channel, ErrorCodeConstants.APP_CHANNEL_NOT_EXIST, request.getMediumUid());
        log.info("应用渠道发布限流: 应用UID: {}, 发布UID: {}，渠道UID: {}, 渠道名称: {}", channel.getAppUid(), channel.getPublishUid(), channel.getAppUid(), channel.getName());
        // 用户配置限流
        AppPublishLimitRespVO publishLimit = appPublishLimitService.defaultIfNullByPublishUid(channel.getPublishUid());
        // 系统配置限流
        List<LimitConfigDTO> limitConfigList = appDictionaryService.appSystemLimitConfig();

        for (LimitConfigDTO limitConfig : limitConfigList) {
            // 应用使用率限流
            LimitConfigDTO rateConfig = publishLimit.getRateConfig();
            if (ObjectUtil.equals(rateConfig.getCode(), limitConfig.getCode()) &&
                    rateConfig.getEnable() && rateConfig.getLimit() != null && rateConfig.getLimit() > 0) {
                limitConfig = rateConfig;
            }

            // 用户使用率限流
            LimitConfigDTO userRateConfig = publishLimit.getUserRateConfig();
            if (ObjectUtil.equals(userRateConfig.getCode(), limitConfig.getCode()) &&
                    userRateConfig.getEnable() && userRateConfig.getLimit() != null && userRateConfig.getLimit() > 0) {
                limitConfig = userRateConfig;
            }

            // 广告配置
            LimitConfigDTO advertisingConfig = publishLimit.getAdvertisingConfig();
            if (ObjectUtil.equals(advertisingConfig.getCode(), limitConfig.getCode()) &&
                    advertisingConfig.getEnable() && advertisingConfig.getLimit() != null && advertisingConfig.getLimit() > 0) {
                limitConfig = advertisingConfig;
            }

            AppLimitContext context = getChannelLimitContext(channel.getAppUid(), request.getFromScene(), request.getEndUser(), limitConfig);
            doLimit(context);
        }

    }

    /**
     * 系统默认限流
     *
     * @param appUid 应用唯一标识
     */
    private void doSystemLimit(String keyPrefix, String appUid, String fromScene) {
        List<LimitConfigDTO> limitConfigList = appDictionaryService.appSystemLimitConfig();
        String loginUserId = Optional.ofNullable(SecurityFrameworkUtils.getLoginUserId()).map(String::valueOf).orElse(null);
        if (StringUtils.isBlank(loginUserId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        for (LimitConfigDTO limitConfig : limitConfigList) {
            AppLimitContext context = new AppLimitContext();
            context.setAppUid(appUid);
            context.setFromScene(fromScene);
            context.setUserId(loginUserId);
            String limitKey;
            if (LimitByEnum.USER.name().equals(limitConfig.getLimitBy())) {
                limitKey = getLimitKey(keyPrefix, appUid, fromScene, loginUserId);
            } else if (LimitByEnum.ADVERTISING.name().equals(limitConfig.getLimitBy())) {
                limitKey = getLimitKey(ADVERTISING_PREFIX, appUid, fromScene, loginUserId);
            } else {
                limitKey = getLimitKey(keyPrefix, appUid, fromScene);
            }
            log.info("限流：Key值: {} ", limitKey);
            context.setLimitKey(limitKey);
            context.setConfig(limitConfig);
            doLimit(context);
        }
    }

    /**
     * 限流
     *
     * @param context 限流上下文
     */
    private void doLimit(AppLimitContext context) {
        // 获取限流配置
        LimitConfigDTO config = context.getConfig();
        // 如果没有开启限流 或者没有配置限流数量，或者限流数量小于等于 0 。则不限流
        if (!config.getEnable() || config.getLimit() == null || config.getLimit() <= 0) {
            return;
        }
        // 时间校验
        if (config.getTimeout() == null || config.getTimeout() <= 0) {
            throw ServiceExceptionUtil.exception(new ErrorCode(300900000, "限流配置错误, 时间间隔必须大于 0"));
        }

        RLock lock = redissonClient.getLock(getLockKey(context.getLimitKey()));
        // 如果获取锁失败，直接抛出异常。
        if (!lock.tryLock()) {
            throw exceptionLimit("系统繁忙，请稍后再试！");
        }
        try {
            // 从 Redis 中获取当前的限流数量
            RBucket<Integer> bucket = redissonClient.getBucket(context.getLimitKey());
            // 获取时间单位
            ChronoUnit timeUnit = ChronoUnit.valueOf(config.getTimeUnit());
            // 将配置中的时间转换成毫秒
            long millisecond = transformMillisecond(config.getTimeout(), timeUnit);
            // 如果 Key 存在。则直接进行处理
            if (bucket.isExists()) {
                // 获取当前的限流数量和过期时间
                Integer currentLimit = bucket.get();
                long expire = bucket.remainTimeToLive();
                // 如果没有超过限流数量，说明已经超过限流数量，直接抛出异常
                if (currentLimit >= config.getLimit()) {
                    // 如果是打广告的话，超出限制时候需要删除 key,下次重新计数
                    if (LimitByEnum.ADVERTISING.name().equals(config.getLimitBy())) {
                        bucket.delete();
                        throw exceptionAdvertising(config.getMessage());
                    }
                    throw exceptionLimit(config.getMessage());
                }

                // 增加 1 个限流数量
                int limit = currentLimit + 1;
                bucket.set(limit, expire, TimeUnit.MILLISECONDS);
                log.info("限流: 增加 Redis 数据: Key: {}, Value: {}, Expire: {}", context.getLimitKey(), limit, millisecond);
                return;
            }
            // 查询日志消息表中的已经执行的数量
            AppLogMessageQuery appLogMessageQuery = new AppLogMessageQuery();
            appLogMessageQuery.setAppUid(context.getAppUid());
            //  目前非 APP 的情况需要添加用户 ID 和 游客 ID
            if (!LimitByEnum.APP.name().equals(config.getLimitBy())) {
                appLogMessageQuery.setUserId(context.getUserId());
                appLogMessageQuery.setEndUser(context.getEndUser());
            }
            appLogMessageQuery.setFromScene(context.getFromScene());
            appLogMessageQuery.setTimeInterval(config.getTimeout());
            appLogMessageQuery.setTimeUnit(timeUnit);
            appLogMessageQuery.setPageNo(1);
            appLogMessageQuery.setPageSize(1);
            appLogMessageQuery.setSorts(Collections.singletonList(SortQuery.of("create_time", SortType.ASC.name())));
            Page<LogAppMessageRespVO> page = appLogService.pageAppLogMessage(appLogMessageQuery);
            log.info("限流：Redis Key 不存在，查询已经执行次数: {}", page.getTotal());
            // 如果已经执行的数量大于 0，则设置已经执行的数量，限流数量恢复
            if (page.getTotal() > 0) {
                // 如果已经执行的数量大于等于限流数量，说明已经超过限流数量，直接抛出异常
                if (page.getTotal() >= config.getLimit()) {
                    // 如果是打广告的话，超出限制时候需要删除 key,下次重新计数
                    if (LimitByEnum.ADVERTISING.name().equals(config.getLimitBy())) {
                        bucket.delete();
                        throw exceptionAdvertising(config.getMessage());
                    }
                    throw exceptionLimit(config.getMessage());
                }

                // 如果已经执行的数量小于限流数量，说明已经超过限流数量，恢复限流数量
                int limit = Long.valueOf(page.getTotal()).intValue() + 1;
                // 获取第一条的执行记录，过期时间 = millisecond - (现在时间 - 第一次的执行时间)
                LocalDateTime firstTime = Optional.ofNullable(page.getRecords()).map(records -> records.get(0)).map(LogAppMessageRespVO::getCreateTime).orElse(LocalDateTime.now());
                long expire = millisecond - (System.currentTimeMillis() - firstTime.toInstant(ZoneOffset.UTC).toEpochMilli());
                //  如果过期时间小于等于 0 ，则重置为 millisecond
                expire = expire <= 0L ? millisecond : expire;

                // 恢复限流数量
                bucket.set(limit, expire, TimeUnit.MILLISECONDS);
                log.info("限流：恢复 Redis 数据: Key: {}, Value: {}, Expire: {}", context.getLimitKey(), limit, expire);
                return;
            }
            // 说明未执行过，初始化限流数量。
            bucket.set(1, millisecond, TimeUnit.MILLISECONDS);
            log.info("限流: 初始化 Redis 数据: Key: {}, Value: {}, Expire: {}", context.getLimitKey(), 1, millisecond);
        } catch (Exception exception) {
            log.error("限流异常：{}", exception.getMessage());
            throw exception;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取渠道限流上下文
     *
     * @param appUid      应用 UID
     * @param fromScene   执行场景
     * @param endUser     游客ID
     * @param limitConfig 限流配置
     * @return 限流上下文
     */
    private AppLimitContext getChannelLimitContext(String appUid, String fromScene, String endUser, LimitConfigDTO limitConfig) {
        AppLimitContext context = new AppLimitContext();
        context.setAppUid(appUid);
        context.setFromScene(fromScene);
        context.setEndUser(endUser);
        String limitKey;
        if (LimitByEnum.USER.name().equals(limitConfig.getLimitBy())) {
            limitKey = getLimitKey(APP_LIMIT_PREFIX, appUid, fromScene, endUser);
        } else if (LimitByEnum.ADVERTISING.name().equals(limitConfig.getLimitBy())) {
            limitKey = getLimitKey(ADVERTISING_PREFIX, appUid, fromScene, endUser);
        } else {
            limitKey = getLimitKey(APP_LIMIT_PREFIX, appUid, fromScene);
        }
        log.info("渠道限流：Key值: {} ", limitKey);
        context.setLimitKey(limitKey);
        context.setConfig(limitConfig);
        return context;
    }

    /**
     * 转换毫秒
     *
     * @param timeout  时间
     * @param timeUnit 时间单位
     * @return 毫秒
     */
    private Long transformMillisecond(Long timeout, ChronoUnit timeUnit) {
        Duration duration = Duration.of(timeout, timeUnit);
        return duration.toMillis();
    }

    /**
     * 生成 key
     *
     * @param prefix    前缀
     * @param fromScene 执行场景
     * @return 生产的 Key
     */
    private static String getLimitKey(String prefix, String appUid, String fromScene) {
        return prefix + appUid + ":" + fromScene;
    }

    /**
     * 生成 key
     *
     * @param prefix    前缀
     * @param fromScene 执行场景
     * @param user      当前登录用户 ID，或者 游客唯一标识
     * @return 生产的 Key
     */
    private static String getLimitKey(String prefix, String appUid, String fromScene, String user) {
        StringBuilder sb = new StringBuilder(prefix);
        sb.append(appUid);
        sb.append(":");
        sb.append(fromScene);
        if (StringUtils.isNotBlank(user)) {
            sb.append(":");
            sb.append(user);
        }
        return sb.toString();
    }

    /**
     * 获取上锁的 key
     *
     * @param limitKey 限流 key
     * @return 锁 key
     */
    private static String getLockKey(String limitKey) {
        return "LOCK:" + limitKey;
    }

    /**
     * 限流超出异常
     *
     * @param message 异常信息
     * @return 异常
     */
    private static ServiceException exceptionLimit(String message) {
        return ServiceExceptionUtil.exception(new ErrorCode(300900005, message));
    }

    /**
     * 广告配置超过异常
     *
     * @param message 异常信息
     * @return 异常信息
     */
    private static ServiceException exceptionAdvertising(String message) {
        return ServiceExceptionUtil.exception(new ErrorCode(300900006, message));
    }
}
