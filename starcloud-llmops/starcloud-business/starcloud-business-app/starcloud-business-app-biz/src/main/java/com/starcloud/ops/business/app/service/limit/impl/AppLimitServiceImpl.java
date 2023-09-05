package com.starcloud.ops.business.app.service.limit.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.api.limit.dto.AppLimitConfigDTO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitQuery;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;
import com.starcloud.ops.business.app.api.log.vo.request.AppLogMessageQuery;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.limit.AppLimitByEnum;
import com.starcloud.ops.business.app.enums.limit.AppLimitConfigEnum;
import com.starcloud.ops.business.app.exception.AppLimitException;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.limit.AppLimitContext;
import com.starcloud.ops.business.app.service.limit.AppLimitRequest;
import com.starcloud.ops.business.app.service.limit.AppLimitService;
import com.starcloud.ops.business.app.service.limit.AppPublishLimitService;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageRespVO;
import com.starcloud.ops.framework.common.api.dto.BaseStreamResult;
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
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
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

    private static final String APP_LIMIT_TIME_INTERVAL_PREDIX = "TIME_INTERVAL:";

    private static final List<String> NO_ADS_SCENES = Arrays.asList(AppSceneEnum.WEB_ADMIN.name(), AppSceneEnum.WEB_MARKET.name(), AppSceneEnum.WEB_IMAGE.name(), AppSceneEnum.OPTIMIZE_PROMPT.name());

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
        // 基础校验
        validateRequest(request, Boolean.TRUE);

        // 用户 ID
        String loginUserId = Optional.ofNullable(SecurityFrameworkUtils.getLoginUserId()).map(String::valueOf).orElse(null);
        if (StringUtils.isBlank(loginUserId)) {
            throw exception("you may be not login, please try again or contact the administrator");
        }

        // 限流总开关
        Boolean limitSwitch = appDictionaryService.appLimitSwitch();
        if (!limitSwitch) {
            log.info("应用限流开关：{}, 将不会执行限流逻辑！", false);
            return;
        }

        // 用户白名单, 当前登录用户在白名单中，不进行限流
        List<String> userWhiteList = appDictionaryService.appLimitUserWhiteList();
        if (CollectionUtil.isNotEmpty(userWhiteList) && userWhiteList.contains(loginUserId)) {
            return;
        }

        log.info("应用限流开始：应用UID: {}, 执行场景: {}, 用户ID: {}", request.getAppUid(), request.getFromScene(), loginUserId);
        request.setUserId(loginUserId);
        doSystemLimit(APP_LIMIT_PREFIX, request);
        log.info("应用限流结束");
    }

    /**
     * 应用限流，应用执行限流，走系统默认限流
     *
     * @param request 请求数据
     * @param emitter sse
     * @return true 通过限流，false 未通过限流
     */
    @Override
    public boolean appLimit(AppLimitRequest request, SseEmitter emitter) {
        return this.doLimitSse(request, emitter, this::appLimit);
    }

    /**
     * 应用限流，应用市场执行限流，走系统默认限流
     *
     * @param request 请求数据
     */
    @Override
    public void marketLimit(AppLimitRequest request) {
        // 基础校验
        validateRequest(request, Boolean.TRUE);

        // 用户 ID
        String loginUserId = Optional.ofNullable(SecurityFrameworkUtils.getLoginUserId()).map(String::valueOf).orElse(null);
        if (StringUtils.isBlank(loginUserId)) {
            throw exception("you may be not login, please try again or contact the administrator");
        }

        // 限流总开关
        Boolean limitSwitch = appDictionaryService.appLimitSwitch();
        if (!limitSwitch) {
            log.info("应用限流开关：{}, 将不会执行限流逻辑！", false);
            return;
        }

        // 用户白名单, 当前登录用户在白名单中，不进行限流
        List<String> userWhiteList = appDictionaryService.appLimitUserWhiteList();
        if (CollectionUtil.isNotEmpty(userWhiteList) && userWhiteList.contains(loginUserId)) {
            return;
        }

        log.info("应用市场限流开始：应用市场UID: {}, 执行场景: {}, 用户ID: {}", request.getAppUid(), request.getFromScene(), loginUserId);
        request.setUserId(loginUserId);
        doSystemLimit(MARKET_LIMIT_PREFIX, request);
        log.info("应用市场限流结束");
    }

    /**
     * 应用限流，应用市场执行限流，走系统默认限流
     *
     * @param request 请求数据
     * @param emitter sse
     * @return true 通过限流，false 未通过限流
     */
    @Override
    public boolean marketLimit(AppLimitRequest request, SseEmitter emitter) {
        return this.doLimitSse(request, emitter, this::marketLimit);
    }

    /**
     * 应用限流，应用发布渠道执行限流。走用户配置限流。系统默认限流兜底
     *
     * @param request 请求数据
     */
    @Override
    public void channelLimit(AppLimitRequest request) {
        // 基础校验
        validateRequest(request, Boolean.FALSE);

        // 限流总开关
        Boolean limitSwitch = appDictionaryService.appLimitSwitch();
        if (!limitSwitch) {
            log.info("应用限流开关：{}, 将不会执行限流逻辑！", false);
            return;
        }

        log.info("应用发布渠道限流开始：应用渠道媒介ID: {}, 执行场景: {}, 游客ID: {}", request.getMediumUid(), request.getFromScene(), request.getEndUser());
        doChannelLimit(request);
        log.info("应用发布渠道限流结束");
    }

    /**
     * 应用限流，应用发布渠道执行限流。走用户配置限流。系统默认限流兜底
     *
     * @param request 请求数据
     * @param emitter sse
     * @return true 通过限流，false 未通过限流
     */
    @Override
    public boolean channelLimit(AppLimitRequest request, SseEmitter emitter) {
        return this.doLimitSse(request, emitter, this::channelLimit);
    }

    /**
     * 系统默认限流
     *
     * @param keyPrefix key 前缀
     * @param request   请求数据
     */
    private void doSystemLimit(String keyPrefix, AppLimitRequest request) {
        List<AppLimitConfigDTO> limitConfigList = appDictionaryService.appSystemLimitConfig();
        for (AppLimitConfigDTO config : limitConfigList) {
            // 校验配置信息
            validateConfig(config);

            // 未开启直接不进行限流
            if (!config.getEnable()) {
                continue;
            }

            // NO_ADS_SCENES 中的场景不需要打广告
            if (AppLimitConfigEnum.ADVERTISING.name().equals(config.getCode()) && NO_ADS_SCENES.contains(request.getFromScene())) {
                continue;
            }

            // 匹配App, matchApps 为空，对所有应用生肖， APP 不为空，则该条规则只对匹配到的 App 生效
            if (CollectionUtil.isNotEmpty(config.getMatchApps()) && !config.getMatchApps().contains(request.getAppUid())) {
                continue;
            }

            // 需要忽略限流的应用 UID
            if (CollectionUtil.isNotEmpty(config.getIgnoreApps()) && config.getIgnoreApps().contains(request.getAppUid())) {
                continue;
            }

            AppLimitContext context = new AppLimitContext();
            context.setAppUid(request.getAppUid());
            context.setFromScene(request.getFromScene());
            context.setUserId(request.getUserId());
            String limitKey;
            if (AppLimitByEnum.USER.name().equals(config.getLimitBy())) {
                limitKey = getLimitKey(keyPrefix, request.getAppUid(), request.getFromScene(), request.getUserId());
            } else if (AppLimitByEnum.ADVERTISING.name().equals(config.getLimitBy())) {
                limitKey = getLimitKey(ADVERTISING_PREFIX, request.getAppUid(), request.getFromScene(), request.getUserId());
            } else {
                limitKey = getLimitKey(keyPrefix, request.getAppUid(), request.getFromScene());
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
     * @param request 请求数据
     */
    private void doChannelLimit(AppLimitRequest request) {
        AppPublishChannelRespVO channel = appPublishChannelService.getByMediumUid(request.getMediumUid());
        AppValidate.notNull(channel, ErrorCodeConstants.APP_CHANNEL_NOT_EXIST, request.getMediumUid());
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
                AppLimitContext context = getChannelLimitContext(channel.getAppUid(), request.getFromScene(), request.getEndUser(), optional.get());
                doLimit(context);
                continue;
            }

            // 校验配置信息
            validateConfig(config);

            // 未开启直接不进行限流
            if (!config.getEnable()) {
                continue;
            }

            // 匹配App, matchApps 为空，对所有应用生肖， APP 不为空，则该条规则只对匹配到的 App 生效
            if (CollectionUtil.isNotEmpty(config.getMatchApps()) && !config.getMatchApps().contains(request.getAppUid())) {
                continue;
            }

            // 需要忽略限流的应用 UID
            if (CollectionUtil.isNotEmpty(config.getIgnoreApps()) && config.getIgnoreApps().contains(request.getAppUid())) {
                continue;
            }

            if (config.getLimit() == null || config.getLimit() <= 0) {
                throw exception("system error, please try again or contact the administrator");
            }
            // 日期判断，日期配置不能小于 1
            if (config.getTimeInterval() == null || config.getTimeInterval() < 1) {
                throw exception("system error, please try again or contact the administrator");
            }
            AppLimitContext context = getChannelLimitContext(channel.getAppUid(), request.getFromScene(), request.getEndUser(), config);
            doLimit(context);
        }
    }

    /**
     * 执行限流
     *
     * @param request  请求
     * @param emitter  sse
     * @param consumer 限流执行器
     * @return true 通过限流，false 未通过限流
     */
    private boolean doLimitSse(AppLimitRequest request, SseEmitter emitter, Consumer<AppLimitRequest> consumer) {
        if (Objects.isNull(emitter)) {
            throw exception("system error, please try again or contact the administrator");
        }
        try {
            consumer.accept(request);
            return Boolean.TRUE;
        } catch (AppLimitException exception) {
            // 广告情况
            if (300900006 == exception.getCode()) {
                BaseStreamResult adsResult = BaseStreamResult.of(Boolean.TRUE, 200, exception.getMessage());
                adsResult.setType("ads-msg");
                try {
                    emitter.send(adsResult);
                } catch (IOException e) {
                    // 其余异常
                    emitter.completeWithError(exception);
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            }
            // 其余异常
            emitter.completeWithError(exception);
            return Boolean.FALSE;
        } catch (Exception exception) {
            emitter.completeWithError(exception);
            return Boolean.FALSE;
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
        // 限流计数Key
        String limitKey = context.getLimitKey();
        RLock lock = redissonClient.getLock(getLockKey(limitKey));
        // 如果获取锁失败，直接抛出异常。
        if (!lock.tryLock()) {
            throw exceptionLimit("系统繁忙，请稍后再试！");
        }
        try {
            // 获取时间单位
            ChronoUnit timeUnit = ChronoUnit.valueOf(config.getTimeUnit());
            // 将配置中的时间转换成毫秒
            long timeout = transformMillisecond(config.getTimeInterval(), timeUnit);

            // 从 Redis 中获取当前的限流数量
            RBucket<Integer> limitBucket = redissonClient.getBucket(limitKey);
            // 从 Redis 中取出当前的时间值
            String timeKey = APP_LIMIT_TIME_INTERVAL_PREDIX + limitKey;
            RBucket<Long> timeBucket = redissonClient.getBucket(timeKey);

            // 如果 Key 存在。则直接进行处理
            if (limitBucket.isExists()) {
                // 获取当前的限流数量
                Integer currentLimit = limitBucket.get();
                // 如果时间存在的情况，说明未过期
                if (timeBucket.isExists()) {
                    // 超出限制，说明需要进行限流，直接抛出异常即可
                    if (currentLimit >= config.getLimit()) {
                        // 如果是打广告的话，超出限制时候需要删除 key,下次重新计数
                        if (AppLimitByEnum.ADVERTISING.name().equals(config.getLimitBy())) {
                            limitBucket.set(0);
                            log.info("限流：广告超出阈值，计时信息为：Key：{}，Expire：{}", timeKey, timeBucket.remainTimeToLive());
                            log.info("限流：广告超出阈值，计数重置 0：Key: {}, Value: 0", limitKey);
                            throw exceptionAdvertising(config.getMessage());
                        }
                        log.info("限流：超出阈值，计时信息为：Key：{}，Expire：{}", timeKey, timeBucket.remainTimeToLive());
                        log.info("限流：超出阈值，计数信息为：Key: {}, Value：{}", limitKey, limitBucket.get());
                        throw exceptionLimit(config.getMessage());
                    }
                    // 说明未超出限流配置，限流数量 + 1
                    int limit = currentLimit + 1;
                    limitBucket.set(limit);
                    log.info("限流：未超出阈值，计时信息为：Key：{}，Expire：{}", timeKey, timeBucket.remainTimeToLive());
                    log.info("限流：未超出阈值，计数自增 1：Key：{}，Value：{}", limitKey, limit);
                    return;
                }

                // 不存在，说明已经过期。时间重置，限流数量重置
                timeBucket.set(timeout, timeout, TimeUnit.MILLISECONDS);
                log.info("限流：重置计时 Key：{}, Value: {}", timeKey, timeout);
                limitBucket.set(1);
                log.info("限流：重置计数 Key：{}, Value：1", limitKey);
                return;
            }

            // 查询日志消息表中的已经执行的数量
            Page<LogAppMessageRespVO> page = this.pageAppLogMessage(context);
            log.info("限流: 计数Key不存在，查询已经执行次数: {}", page.getTotal());
            // 如果已经执行的数量大于 0，则设置已经执行的数量，限流数量恢复
            if (page.getTotal() > 0) {
                // 获取第一条的执行记录，过期时间 = timeout - (现在时间 - 第一次的执行时间)
                LocalDateTime firstTime = Optional.ofNullable(page.getRecords()).map(records -> records.get(0)).map(LogAppMessageRespVO::getCreateTime).orElse(LocalDateTime.now());
                long expire = timeout - (System.currentTimeMillis() - firstTime.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
                //  如果过期时间小于等于 0 ，则重置为 timeout
                expire = expire <= 0L ? timeout : expire;

                // 限流计时设置为 expire
                timeBucket.set(expire, expire, TimeUnit.MICROSECONDS);

                // 超出限制，说明需要进行限流，直接抛出异常即可
                if (page.getTotal() >= config.getLimit()) {
                    // 如果是打广告的话，超出限制重置广告计数
                    if (AppLimitByEnum.ADVERTISING.name().equals(config.getLimitBy())) {
                        limitBucket.set(0);
                        log.info("限流：广告超出阈值，计时信息为：Key：{}，Expire：{}", timeKey, timeBucket.remainTimeToLive());
                        log.info("限流：广告超出阈值，计数重置 0：Key: {}, Value: 0", limitKey);
                        throw exceptionAdvertising(config.getMessage());
                    }
                    log.info("限流：超出阈值，计时信息为：Key：{}，Expire：{}", timeKey, timeBucket.remainTimeToLive());
                    log.info("限流：超出阈值，计数信息为：Key: {}, Value：{}", limitKey, limitBucket.get());
                    throw exceptionLimit(config.getMessage());
                }

                // 此时说明未超出限流，计数 +1 即可
                int limit = Long.valueOf(page.getTotal()).intValue() + 1;
                limitBucket.set(limit);
                log.info("限流：恢复计时：计时信息为：Key：{}，Expire：{}", timeKey, expire);
                log.info("限流：恢复计数：计数信息未：Key：{}, Value：{}", limitKey, limit);
                return;
            }

            // 说明未执行过，初始化限流数量。
            timeBucket.set(timeout, timeout, TimeUnit.MICROSECONDS);
            limitBucket.set(1);
            log.info("限流：初始化计时：计时信息为：Key：{}，Expire：{}", timeKey, timeout);
            log.info("限流：初始化计数：计数信息为：Key: {}, Value: {}", limitKey, 1);
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
        if (!AppLimitByEnum.APP.name().equals(config.getLimitBy())) {
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
        if (AppLimitByEnum.USER.name().equals(limitConfig.getLimitBy())) {
            limitKey = getLimitKey(APP_LIMIT_PREFIX, appUid, fromScene, endUser);
        } else if (AppLimitByEnum.ADVERTISING.name().equals(limitConfig.getLimitBy())) {
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
     * 通用限流异常
     *
     * @param message 异常信息
     * @return 限流异常
     */
    private static AppLimitException exception(String message) {
        return AppLimitException.exception(500, message);
    }

    /**
     * 限流超出异常
     *
     * @param message 异常信息
     * @return 异常
     */
    private static AppLimitException exceptionLimit(String message) {
        return AppLimitException.exception(300900005, message);
    }

    /**
     * 广告配置超过异常
     *
     * @param message 异常信息
     * @return 异常信息
     */
    private static AppLimitException exceptionAdvertising(String message) {
        return AppLimitException.exception(300900006, message);
    }

    /**
     * 基础校验
     *
     * @param request 请求数据
     * @param isUser  是否是用户态
     */
    private void validateRequest(AppLimitRequest request, boolean isUser) {
        if (isUser) {
            if (StringUtils.isBlank(request.getAppUid())) {
                throw exception("the app uid this required");
            }
        } else {
            if (StringUtils.isBlank(request.getMediumUid())) {
                throw exception("the mediumUid is required");
            }
            if (StringUtils.isBlank(request.getEndUser())) {
                throw exception("the endUser is required");
            }
        }

        if (StringUtils.isBlank(request.getFromScene())) {
            throw exception("the from scene is required");
        }
    }

    /**
     * 校验配置信息
     *
     * @param config 配置信息
     */
    private void validateConfig(AppLimitConfigDTO config) {
        if (config.getLimit() == null || config.getLimit() <= 0) {
            throw exception("system error, please try again or contact the administrator");
        }
        // 日期判断，日期配置不能小于 1
        if (config.getTimeInterval() == null || config.getTimeInterval() < 1) {
            throw exception("system error, please try again or contact the administrator");
        }
    }
}
