package com.starcloud.ops.business.app.service.limit.impl;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.api.limit.dto.AppLimitConfigDTO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitQuery;
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
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
    public void appLimit(AppLimitRequest request, SseEmitter emitter) {
        this.doLimitSse(request, emitter, this::appLimit);
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
     * 应用限流，应用市场执行限流，走系统默认限流
     *
     * @param request 请求数据
     * @param emitter sse
     */
    @Override
    public void marketLimit(AppLimitRequest request, SseEmitter emitter) {
        this.doLimitSse(request, emitter, this::marketLimit);
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
        log.info("应用发布渠道限流开始：应用渠道媒介ID: {}, 执行场景: {}, 游客ID: {}", request.getMediumUid(), request.getFromScene(), request.getEndUser());
        doChannelLimit(request.getMediumUid(), request.getFromScene(), request.getEndUser());
        log.info("应用发布渠道限流结束");
    }

    /**
     * 应用限流，应用发布渠道执行限流。走用户配置限流。系统默认限流兜底
     *
     * @param request 请求数据
     * @param emitter sse
     */
    @Override
    public void channelLimit(AppLimitRequest request, SseEmitter emitter) {
        this.doLimitSse(request, emitter, this::channelLimit);
    }

    /**
     * 系统默认限流
     *
     * @param appUid 应用唯一标识
     */
    private void doSystemLimit(String keyPrefix, String appUid, String fromScene) {
        List<AppLimitConfigDTO> limitConfigList = appDictionaryService.appSystemLimitConfig();
        String loginUserId = Optional.ofNullable(SecurityFrameworkUtils.getLoginUserId()).map(String::valueOf).orElse(null);
        if (StringUtils.isBlank(loginUserId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        for (AppLimitConfigDTO config : limitConfigList) {
            // 未开启直接不进行限流
            if (!config.getEnable()) {
                continue;
            }
            if (config.getLimit() == null || config.getLimit() <= 0) {
                throw ServiceExceptionUtil.exception(new ErrorCode(300900000, "系统错误, 请联系管理员"));
            }
            // 日期判断，日期配置不能小于 1
            if (config.getTimeInterval() == null || config.getTimeInterval() < 1) {
                throw ServiceExceptionUtil.exception(new ErrorCode(300900000, "系统错误, 请联系管理员"));
            }
            AppLimitContext context = new AppLimitContext();
            context.setAppUid(appUid);
            context.setFromScene(fromScene);
            context.setUserId(loginUserId);
            String limitKey;
            if (LimitByEnum.USER.name().equals(config.getLimitBy())) {
                limitKey = getLimitKey(keyPrefix, appUid, fromScene, loginUserId);
            } else if (LimitByEnum.ADVERTISING.name().equals(config.getLimitBy())) {
                limitKey = getLimitKey(ADVERTISING_PREFIX, appUid, fromScene, loginUserId);
            } else {
                limitKey = getLimitKey(keyPrefix, appUid, fromScene);
            }
            log.info("限流：Key值: {} ", limitKey);
            context.setLimitKey(limitKey);
            context.setConfig(config);
            doLimit(context);
        }
    }

    /**
     * 执行渠道限流
     *
     * @param mediumUid 媒介UID
     * @param fromScene 执行场景
     * @param endUser   游客ID
     */
    private void doChannelLimit(String mediumUid, String fromScene, String endUser) {
        AppPublishChannelRespVO channel = appPublishChannelService.getByMediumUid(mediumUid);
        AppValidate.notNull(channel, ErrorCodeConstants.APP_CHANNEL_NOT_EXIST, mediumUid);
        log.info("应用渠道发布限流: 应用UID: {}, 发布UID: {}，渠道UID: {}, 渠道名称: {}", channel.getAppUid(), channel.getPublishUid(), channel.getAppUid(), channel.getName());
        // 用户配置限流
        List<AppLimitConfigDTO> limitConfigList = this.defaultIfNullPublishLimitList(channel.getAppUid(), channel.getPublishUid(), channel.getUid());
        // 系统配置限流
        List<AppLimitConfigDTO> systemLimitConfigList = appDictionaryService.appSystemLimitConfig();
        // 循环执行限流
        for (final AppLimitConfigDTO config : systemLimitConfigList) {
            // 判断是否使用渠道限流的配置
            Optional<AppLimitConfigDTO> optional = limitConfigList.stream().filter(item -> item.getCode().equals(config.getCode())).findAny();
            // 使用渠道限流的配置
            if (optional.isPresent()) {
                AppLimitContext context = getChannelLimitContext(channel.getAppUid(), fromScene, endUser, optional.get());
                doLimit(context);
                continue;
            }
            // 未开启直接不进行限流
            if (!config.getEnable()) {
                continue;
            }
            if (config.getLimit() == null || config.getLimit() <= 0) {
                throw ServiceExceptionUtil.exception(new ErrorCode(300900000, "系统错误, 请联系管理员"));
            }
            // 日期判断，日期配置不能小于 1
            if (config.getTimeInterval() == null || config.getTimeInterval() < 1) {
                throw ServiceExceptionUtil.exception(new ErrorCode(300900000, "系统错误, 请联系管理员"));
            }
            AppLimitContext context = getChannelLimitContext(channel.getAppUid(), fromScene, endUser, config);
            doLimit(context);
        }
    }

    /**
     * 执行限流
     *
     * @param request  请求
     * @param emitter  sse
     * @param consumer 限流执行器
     */
    private void doLimitSse(AppLimitRequest request, SseEmitter emitter, Consumer<AppLimitRequest> consumer) {
        if (Objects.isNull(emitter)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(300900002, "系统异常！"));
        }
        try {
            consumer.accept(request);
        } catch (ServiceException exception) {
            // 广告情况
            if (300900006 == exception.getCode()) {
                String adsMessage = "&adsMessageStar&" + exception.getMessage() + "&adsMessageEnd&";
                StreamingSseCallBackHandler callBackHandler = new StreamingSseCallBackHandler(emitter);
                callBackHandler.onLLMNewToken(adsMessage);
                return;
            }
            // 其余异常
            emitter.completeWithError(exception);
        } catch (Exception exception) {
            emitter.completeWithError(exception);
        }
    }

    /**
     * 限流
     *
     * @param context 限流上下文
     */
    private void doLimit(AppLimitContext context) {
        // 获取限流配置
        AppLimitConfigDTO config = context.getConfig();
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
            long millisecond = transformMillisecond(config.getTimeInterval(), timeUnit);
            // 如果 Key 存在。则直接进行处理
            if (bucket.isExists()) {
                // 获取当前的限流数量和过期时间
                Integer currentLimit = bucket.get();
                long expire = bucket.remainTimeToLive();
                // 如果没有超过限流数量，说明已经超过限流数量，直接抛出异常
                if (currentLimit >= config.getLimit()) {
                    // 如果是打广告的话，超出限制时候需要删除 key,下次重新计数
                    if (LimitByEnum.ADVERTISING.name().equals(config.getLimitBy())) {
                        bucket.set(0, expire, TimeUnit.MILLISECONDS);
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
            Page<LogAppMessageRespVO> page = this.pageAppLogMessage(context);
            log.info("限流：Redis Key 不存在，查询已经执行次数: {}", page.getTotal());
            // 如果已经执行的数量大于 0，则设置已经执行的数量，限流数量恢复
            if (page.getTotal() > 0) {

                // 获取第一条的执行记录，过期时间 = millisecond - (现在时间 - 第一次的执行时间)
                LocalDateTime firstTime = Optional.ofNullable(page.getRecords()).map(records -> records.get(0)).map(LogAppMessageRespVO::getCreateTime).orElse(LocalDateTime.now());
                long expire = millisecond - (System.currentTimeMillis() - firstTime.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
                //  如果过期时间小于等于 0 ，则重置为 millisecond
                expire = expire <= 0L ? millisecond : expire;

                // 如果已经执行的数量大于等于限流数量，说明已经超过限流数量，直接抛出异常
                if (page.getTotal() >= config.getLimit()) {
                    // 如果是打广告的话，超出限制时候需要删除 key,下次重新计数
                    if (LimitByEnum.ADVERTISING.name().equals(config.getLimitBy())) {
                        bucket.set(0, expire, TimeUnit.MILLISECONDS);
                        throw exceptionAdvertising(config.getMessage());
                    }
                    throw exceptionLimit(config.getMessage());
                }

                // 如果已经执行的数量小于限流数量，说明已经超过限流数量，恢复限流数量
                int limit = Long.valueOf(page.getTotal()).intValue() + 1;

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
     * 获取发布限流配置
     *
     * @param appUid     应用 UID
     * @param publishUid 发布UID
     * @param channelUid 渠道UID
     * @return 发布限流配置
     */
    private List<AppLimitConfigDTO> defaultIfNullPublishLimitList(String appUid, String publishUid, String channelUid) {
        AppPublishLimitQuery query = new AppPublishLimitQuery();
        query.setAppUid(appUid);
        query.setPublishUid(publishUid);
        query.setChannelUid(channelUid);
        AppPublishLimitRespVO publishLimit = appPublishLimitService.defaultIfNull(query);

        List<AppLimitConfigDTO> list = new ArrayList<>();

        AppLimitConfigDTO rateConfig = publishLimit.getRateConfig();
        if (rateConfig.getEnable() && rateConfig.getLimit() != null && rateConfig.getLimit() > 0
                && rateConfig.getTimeInterval() != null && rateConfig.getTimeInterval() > 0) {
            list.add(rateConfig);
        }

        AppLimitConfigDTO userRateConfig = publishLimit.getUserRateConfig();
        if (userRateConfig.getEnable() && userRateConfig.getLimit() != null && userRateConfig.getLimit() > 0
                && userRateConfig.getTimeInterval() != null && userRateConfig.getTimeInterval() > 0) {
            list.add(userRateConfig);
        }

        AppLimitConfigDTO advertisingConfig = publishLimit.getAdvertisingConfig();
        if (advertisingConfig.getEnable() && advertisingConfig.getLimit() != null && advertisingConfig.getLimit() > 0
                && advertisingConfig.getTimeInterval() != null && advertisingConfig.getTimeInterval() > 0) {
            list.add(advertisingConfig);
        }

        return list;
    }

    /**
     * 分页查询日志列表
     *
     * @param context 上下文
     * @return 分页日志消息
     */
    private Page<LogAppMessageRespVO> pageAppLogMessage(AppLimitContext context) {
        AppLimitConfigDTO config = context.getConfig();
        // 查询日志消息表中的已经执行的数量
        AppLogMessageQuery appLogMessageQuery = new AppLogMessageQuery();
        appLogMessageQuery.setAppUid(context.getAppUid());
        //  目前非 APP 的情况需要添加用户 ID 和 游客 ID
        if (!LimitByEnum.APP.name().equals(config.getLimitBy())) {
            appLogMessageQuery.setUserId(context.getUserId());
            appLogMessageQuery.setEndUser(context.getEndUser());
        }
        appLogMessageQuery.setFromScene(context.getFromScene());
        appLogMessageQuery.setTimeInterval(config.getTimeInterval());
        appLogMessageQuery.setTimeUnit(ChronoUnit.valueOf(config.getTimeUnit()));
        appLogMessageQuery.setPageNo(1);
        appLogMessageQuery.setPageSize(1);
        appLogMessageQuery.setSorts(Collections.singletonList(SortQuery.of("create_time", SortType.ASC.name())));
        return appLogService.pageAppLogMessage(appLogMessageQuery);
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
    private AppLimitContext getChannelLimitContext(String appUid, String fromScene, String endUser, AppLimitConfigDTO limitConfig) {
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
