package com.starcloud.ops.business.app.service.limit.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.api.limit.dto.AppLimitRuleDTO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitQuery;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;
import com.starcloud.ops.business.app.api.log.vo.request.AppLogMessageQuery;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.limit.AppLimitByEnum;
import com.starcloud.ops.business.app.enums.limit.AppLimitRuleEnum;
import com.starcloud.ops.business.app.exception.AppLimitException;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.limit.AppLimitContext;
import com.starcloud.ops.business.app.service.limit.AppLimitRequest;
import com.starcloud.ops.business.app.service.limit.AppLimitService;
import com.starcloud.ops.business.app.service.limit.AppPublishLimitService;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageRespVO;
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

    private static final String SYSTEM_LIMIT_PREFIX = "SYSTEM:";

    private static final String ADVERTISING_PREFIX = "ADVERTISING:";

    private static final String APP_LIMIT_TIME_INTERVAL_PREDIX = "TIME_INTERVAL:";

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

        // 限流总开关
        Boolean limitSwitch = appDictionaryService.appLimitSwitch();
        if (!limitSwitch) {
            log.info("应用限流开关：{}, 将不会执行限流逻辑！", Boolean.FALSE);
            return;
        }

        // 用户 ID
        String loginUserId = Optional.ofNullable(SecurityFrameworkUtils.getLoginUserId()).map(String::valueOf).orElse(null);
        AppValidate.notBlank(loginUserId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);

        // 用户白名单, 当前登录用户在白名单中，不进行限流
        List<String> userWhiteList = appDictionaryService.appLimitUserWhiteList();
        if (CollectionUtil.isNotEmpty(userWhiteList) && userWhiteList.contains(loginUserId)) {
            return;
        }

        log.info("应用限流开始：应用UID: {}, 执行场景: {}, 用户ID: {}", request.getAppUid(), request.getFromScene(), loginUserId);

        request.setUserId(loginUserId);
        request.setIsLoginLimit(Boolean.TRUE);

        // 应用配置限流
        doAppLimit(request);
        // 系统限流
        doSystemLimit(request);
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
        // 限流总开关
        Boolean limitSwitch = appDictionaryService.appLimitSwitch();
        if (!limitSwitch) {
            log.info("应用限流开关：{}, 将不会执行限流逻辑！", false);
            return;
        }

        // 用户 ID
        String loginUserId = Optional.ofNullable(SecurityFrameworkUtils.getLoginUserId()).map(String::valueOf).orElse(null);
        AppValidate.notBlank(loginUserId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);

        // 用户白名单, 当前登录用户在白名单中，不进行限流
        List<String> userWhiteList = appDictionaryService.appLimitUserWhiteList();
        if (CollectionUtil.isNotEmpty(userWhiteList) && userWhiteList.contains(loginUserId)) {
            return;
        }

        log.info("应用市场限流开始：应用市场UID: {}, 执行场景: {}, 用户ID: {}", request.getAppUid(), request.getFromScene(), loginUserId);
        request.setUserId(loginUserId);
        request.setIsLoginLimit(Boolean.TRUE);

        // 系统限流
        doSystemLimit(request);
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

        // 限流总开关
        Boolean limitSwitch = appDictionaryService.appLimitSwitch();
        if (!limitSwitch) {
            log.info("应用限流开关：{}, 将不会执行限流逻辑！", false);
            return;
        }

        log.info("应用发布渠道限流开始：应用渠道媒介ID: {}, 执行场景: {}, 游客ID: {}", request.getMediumUid(), request.getFromScene(), request.getEndUser());
        request.setIsLoginLimit(Boolean.FALSE);
        // 用户配置限流
        doAppLimit(request);
        // 系统限流
        doSystemLimit(request);
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
     * 执行渠道限流
     *
     * @param request 请求数据
     */
    private void doAppLimit(AppLimitRequest request) {
        if (request.getIsLoginLimit()) {
            // 白名单，不进行限流
            List<String> appLimitWhiteList = appDictionaryService.appLimitWhiteList();
            if (appLimitWhiteList.contains(request.getAppUid())) {
                return;
            }
        } else {
            // 查询渠道信息
            AppPublishChannelRespVO channel = appPublishChannelService.getByMediumUid(request.getMediumUid());
            AppValidate.notNull(channel, ErrorCodeConstants.CHANNEL_NON_EXISTENT);
            request.setAppUid(channel.getAppUid());
        }

        // 获取限流信息
        AppPublishLimitQuery query = new AppPublishLimitQuery();
        query.setAppUid(request.getAppUid());
        List<AppLimitRuleDTO> limitRuleList = this.listAppLimitRule(query);
        // 获取不需要打广告的场景
        List<String> noAdsScenes = appDictionaryService.appLimitNoAdsScenes();
        for (AppLimitRuleDTO rule : limitRuleList) {
            // 当规则名称为 ADVERTISING_RULE 时，且当前场景在不需要打广告的场景中，直接跳过
            if (AppLimitRuleEnum.ADVERTISING_RULE.name().equals(rule.getCode()) && noAdsScenes.contains(request.getFromScene())) {
                continue;
            }
            // 执行限流
            AppLimitContext context = new AppLimitContext();
            context.setAppUid(request.getAppUid());
            context.setUserId(request.getUserId());
            context.setEndUser(request.getEndUser());
            context.setFromScene(request.getFromScene());
            context.setLimitKey(limitKey(APP_LIMIT_PREFIX, rule, request));
            context.setRule(rule);
            doLimit(context);
        }
    }

    /**
     * 系统默认限流
     *
     * @param request 请求数据
     */
    private void doSystemLimit(AppLimitRequest request) {
        List<AppLimitRuleDTO> limitRuleList = appDictionaryService.systemLimitRuleList();
        for (AppLimitRuleDTO rule : limitRuleList) {
            // 匹配App, includeApps 为空，对所有应用生肖， APP 不为空，则该条规则只对匹配到的 App 生效
            if (CollectionUtil.isNotEmpty(rule.getIncludeApps()) && !rule.getIncludeApps().contains(request.getAppUid())) {
                continue;
            }

            // 需要忽略限流的应用 UID
            if (CollectionUtil.isNotEmpty(rule.getExcludeApps()) && rule.getExcludeApps().contains(request.getAppUid())) {
                continue;
            }
            AppLimitContext context = new AppLimitContext();
            context.setAppUid(request.getAppUid());
            context.setFromScene(request.getFromScene());
            context.setUserId(request.getUserId());
            context.setEndUser(request.getEndUser());
            context.setLimitKey(limitKey(SYSTEM_LIMIT_PREFIX, rule, request));
            context.setRule(rule);
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
            throw exception("系统限流异常，请联系管理员或稍后重试！");
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
        AppLimitRuleDTO config = context.getRule();
        // 限流计数 Key
        String limitKey = context.getLimitKey();
        RLock lock = redissonClient.getLock(getLockKey(limitKey));
        try {
            // 如果获取锁失败，直接抛出异常。
            if (!lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                throw exceptionLimit("系统繁忙，请稍后再试！");
            }
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
                Integer currentThreshold = limitBucket.get();
                // 如果时间存在的情况，说明未过期
                if (timeBucket.isExists()) {
                    // 超出限制，说明需要进行限流，直接抛出异常即可
                    if (currentThreshold >= config.getThreshold()) {
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
                    int threshold = currentThreshold + 1;
                    limitBucket.set(threshold);
                    log.info("限流：未超出阈值，计时信息为：Key：{}，Expire：{}", timeKey, timeBucket.remainTimeToLive());
                    log.info("限流：未超出阈值，计数自增 1：Key：{}，Value：{}", limitKey, threshold);
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
                if (page.getTotal() >= config.getThreshold()) {
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

                // 此时说明未超出限流阈值，计数 +1 即可
                int threshold = Long.valueOf(page.getTotal()).intValue() + 1;
                limitBucket.set(threshold);
                log.info("限流：恢复计时：计时信息为：Key：{}，Expire：{}", timeKey, expire);
                log.info("限流：恢复计数：计数信息未：Key：{}, Value：{}", limitKey, threshold);
                return;
            }

            // 说明未执行过，初始化限流数量。
            timeBucket.set(timeout, timeout, TimeUnit.MICROSECONDS);
            limitBucket.set(1);
            log.info("限流：初始化计时：计时信息为：Key：{}，Expire：{}", timeKey, timeout);
            log.info("限流：初始化计数：计数信息为：Key: {}, Value: {}", limitKey, 1);
        } catch (InterruptedException exception) {
            log.error("限流异常：{}", exception.getMessage());
            throw exceptionLimit("系统繁忙，请稍后再试！");
        } catch (Exception exception) {
            log.error("限流异常：{}", exception.getMessage());
            throw exception;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取 应用限流规则
     *
     * @param query 查询条件
     * @return 限流规则集合
     */
    private List<AppLimitRuleDTO> listAppLimitRule(AppPublishLimitQuery query) {
        List<AppLimitRuleDTO> limitRuleList = new ArrayList<>();
        AppPublishLimitRespVO limitResponse = appPublishLimitService.get(query);
        // 处理限流规则
        AppLimitRuleDTO appRateLimit = limitResponse.getAppLimitRule();
        if (validateLimitRule(appRateLimit)) {
            limitRuleList.add(appRateLimit);
        }
        AppLimitRuleDTO userLimitRule = limitResponse.getUserLimitRule();
        if (validateLimitRule(userLimitRule)) {
            limitRuleList.add(userLimitRule);
        }
        AppLimitRuleDTO advertisingLimitRule = limitResponse.getAdvertisingRule();
        if (validateLimitRule(advertisingLimitRule)) {
            limitRuleList.add(advertisingLimitRule);
        }

        if (CollectionUtil.isEmpty(limitRuleList)) {
            return Collections.emptyList();
        }
        return limitRuleList;
    }

    /**
     * 校验规则
     *
     * @param rule 校验规则
     * @return 校验规则
     */
    private static boolean validateLimitRule(AppLimitRuleDTO rule) {
        return Objects.nonNull(rule) && rule.getEnable() && rule.getThreshold() > 0 && rule.getTimeInterval() >= 1;
    }

    /**
     * 分页查询日志列表
     *
     * @param context 上下文
     * @return 分页日志消息
     */
    private Page<LogAppMessageRespVO> pageAppLogMessage(AppLimitContext context) {
        AppLimitRuleDTO config = context.getRule();
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
     * 生成限流 key
     *
     * @param prefix  前缀
     * @param rule    限流规则
     * @param request 请求数据
     * @return 限流 key
     */
    private static String limitKey(String prefix, AppLimitRuleDTO rule, AppLimitRequest request) {
        if (request.getIsLoginLimit()) {
            if (AppLimitByEnum.USER.name().equals(rule.getLimitBy())) {
                return getLimitKey(prefix, request.getAppUid(), request.getFromScene(), request.getUserId());
            } else if (AppLimitByEnum.ADVERTISING.name().equals(rule.getLimitBy())) {
                return getLimitKey(ADVERTISING_PREFIX, request.getAppUid(), request.getFromScene(), request.getUserId());
            } else {
                return getLimitKey(prefix, request.getAppUid(), request.getFromScene());
            }
        } else {
            if (AppLimitByEnum.USER.name().equals(rule.getLimitBy())) {
                return getLimitKey(prefix, request.getAppUid(), request.getFromScene(), request.getEndUser());
            } else if (AppLimitByEnum.ADVERTISING.name().equals(rule.getLimitBy())) {
                return getLimitKey(ADVERTISING_PREFIX, request.getAppUid(), request.getFromScene(), request.getEndUser());
            } else {
                return getLimitKey(prefix, request.getAppUid(), request.getFromScene());
            }
        }
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
    @SuppressWarnings("all")
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

}
