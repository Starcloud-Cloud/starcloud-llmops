package com.starcloud.ops.business.app.service.xhs.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.params.JsonDataVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.ActionResponseRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentExecuteRespVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.content.CreativeContentMapper;
import com.starcloud.ops.business.app.domain.cache.AppStepStatusCache;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.action.AssembleActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.domain.handler.poster.PosterGenerationHandler;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.business.app.feign.dto.PosterImage;
import com.starcloud.ops.business.app.model.content.CopyWritingContent;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteParam;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteResult;
import com.starcloud.ops.business.app.model.content.ImageContent;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.app.service.xhs.content.impl.CreativeContentServiceImpl;
import com.starcloud.ops.business.app.service.xhs.executor.CreativeThreadPoolHolder;
import com.starcloud.ops.business.app.util.MarkdownUtils;
import com.starcloud.ops.business.app.util.UserRightSceneUtils;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.api.rights.dto.ReduceRightsDTO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.USER_RIGHTS_BEAN_NOT_ENOUGH;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.USER_RIGHTS_IMAGE_NOT_ENOUGH;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.USER_RIGHTS_MATRIX_BEAN_NOT_ENOUGH;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.USER_RIGHTS_NOT_ENOUGH;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Component
@Slf4j
public class CreativeExecuteManager {

    /**
     * 快速失败的错误码
     * 1. 快速失败，
     * 2. 任务不再进行重试
     * 3. 失败后直接发送告警信息
     */
    public static final List<Integer> FAILURE_FAST_CODE_LIST = Arrays.asList(
            USER_RIGHTS_BEAN_NOT_ENOUGH.getCode(),
            USER_RIGHTS_IMAGE_NOT_ENOUGH.getCode(),
            USER_RIGHTS_NOT_ENOUGH.getCode(),
            USER_RIGHTS_MATRIX_BEAN_NOT_ENOUGH.getCode(),
            // JSON 解析失败
            ErrorCodeConstants.EXECUTE_JSON_RESULT_PARSE_ERROR.getCode(),
            // 海报执行失败
            ErrorCodeConstants.EXECUTE_POSTER_EXCEPTION.getCode()
    );

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private CreativeContentMapper creativeContentMapper;

    @Resource
    private CreativeThreadPoolHolder creativeThreadPoolHolder;

    @Resource
    private AppLogService appLogService;

    @Resource
    private AdminUserRightsApi adminUserRightsApi;

    @Resource
    private AppStepStatusCache appStepStatusCache;

    @Resource
    private CreativeAlarmManager creativeAlarmManager;

    /**
     * 批量执行小红书应用生成
     *
     * @param requestList 内容列表
     * @return 执行结果
     */
    public List<CreativeContentExecuteRespVO> bathExecute(List<CreativeContentExecuteReqVO> requestList) {

        // 如果创作内容列表为空，说明不需要执行
        if (CollectionUtil.isEmpty(requestList)) {
            if (log.isWarnEnabled()) {
                log.warn("创作计划：创作内容生成执行：参数为空！生成结束");
            }
            return Collections.emptyList();
        }

        // 获取执行线程池
        ThreadPoolExecutor executor = creativeThreadPoolHolder.executor();

        // 组装批量任务
        List<CompletableFuture<CreativeContentExecuteRespVO>> appFutureList = requestList.stream()
                .map(request -> CompletableFuture.supplyAsync(
                        () -> TenantUtils.execute(request.getTenantId(), () -> execute(request)), executor)
                )
                .collect(Collectors.toList());

        // 合并任务
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(appFutureList.toArray(new CompletableFuture[0]));

        // 等待所有任务执行完成并且获取执行结果
        CompletableFuture<List<CreativeContentExecuteRespVO>> allFuture = allOfFuture
                .thenApply(v -> appFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList()));

        // 获取执行结果
        List<CreativeContentExecuteRespVO> responseList = allFuture.join();
        log.info("创作中心：创作内容批量执行完成");
        return responseList;
    }

    /**
     * 执行创作内容生成
     *
     * @param request 执行请求
     * @return 执行结果
     */
    public CreativeContentExecuteRespVO execute(CreativeContentExecuteReqVO request) {

        String lockKey = "creative-content-" + request.getUid();
        RLock lock = redissonClient.getLock(lockKey);
        if (lock != null && !lock.tryLock()) {
            log.warn("创作内容任务正在执行中(内容UID：{})！", request.getUid());
            return CreativeContentExecuteRespVO.failure(request.getUid(), request.getPlanUid(), request.getPlanUid(), "该创作内容正在执行中，请稍后再试");
        }

        log.info("创作内容任务上锁成功({})", lockKey);
        try {
            LocalDateTime start = LocalDateTime.now();
            log.info("创作内容任务执行开始：{}，{}", start, request.getUid());
            // 获取最大重试次数
            Integer maxRetry = getMaxRetry(request);
            // 获取最新的创作内容
            CreativeContentDO latestContent = getLatestContent(request, maxRetry, start);
            CreativeContentExecuteParam executeParams = CreativeContentConvert.INSTANCE.toExecuteParam(latestContent.getExecuteParam());
            AppMarketEntity appMarketEntity = AppMarketConvert.INSTANCE.convertEntity(executeParams.getAppInformation());

            // 用户权益检测，校验用户权益是否足够
            calculateUserRightsEnough(latestContent, start, maxRetry);
            try {
                // 更新创作内容状态为执行中
                updateContentExecuting(latestContent, start);
                // 执行应用，并且获取执行结果
                AppExecuteRespVO response = appExecute(latestContent, maxRetry);
                // 应用执行完成再次查询创作内容
                CreativeContentDO recheckContent = creativeContentMapper.get(latestContent.getUid());
                // 后置处理步骤缓存状态更新
                appStepStatusCache.stepStart(response.getConversationUid(), AppStepStatusCache.POST_PROCESSOR_HANDLER, appMarketEntity);
                // 查询日志信息
                AppLogMessageRespVO logAppMessage = getAppLogMessageRespVO(response);
                // 构造执行结果
                CreativeContentExecuteRespVO executeResponse = buildResponse(logAppMessage, latestContent);
                // 获取到结果内容
                CreativeContentExecuteResult executeResult = executeResponse.getResult();

                // 如果已经是取消的话，则直接将状态更新为取消状态
                if (CreativeContentStatusEnum.CANCELED.name().equals(recheckContent.getStatus())) {
                    // 后置处理步骤缓存状态更新
                    appStepStatusCache.stepFailure(response.getConversationUid(), AppStepStatusCache.POST_PROCESSOR_HANDLER, appMarketEntity);
                    updateContentCanceled(recheckContent, start);
                    return CreativeContentExecuteRespVO.failure(latestContent.getUid(), latestContent.getPlanUid(), latestContent.getBatchUid(), "创作内容已取消");
                }

                // 权益扣除
                reduceRights(latestContent);
                // 后置处理步骤缓存状态更新
                appStepStatusCache.stepSuccess(response.getConversationUid(), AppStepStatusCache.POST_PROCESSOR_HANDLER, appMarketEntity);
                // 更新创作内容状态
                updateContentSuccess(latestContent, executeResult, start);
                // 返回结果
                return executeResponse;
            } catch (Throwable throwable) {
                // 根据异常更新创作内容状态
                updateContentFailureByThrowable(latestContent, start, maxRetry, throwable);
                // 后置处理步骤缓存状态更新
                appStepStatusCache.stepFailure(latestContent.getConversationUid(), AppStepStatusCache.POST_PROCESSOR_HANDLER, appMarketEntity);
                throw throwable;
            }
        } catch (ServiceException exception) {
            log.error("创作中心：创作内容任务执行失败：错误码: {}, 错误信息: {}", exception.getCode(), exception.getMessage(), exception);
            // 报警
            creativeAlarmManager.executeAlarm(request.getUid(), request.getForce(), getMaxRetry(request), exception);
            return CreativeContentExecuteRespVO.failure(request.getUid(), request.getPlanUid(), request.getBatchUid(), exception.getMessage());
        } catch (Throwable exception) {
            log.error("创作中心：创作内容任务执行失败： 错误信息: {}", exception.getMessage(), exception);
            // 报警
            creativeAlarmManager.executeAlarm(request.getUid(), request.getForce(), getMaxRetry(request), exception);
            return CreativeContentExecuteRespVO.failure(request.getUid(), request.getPlanUid(), request.getBatchUid(), exception.getMessage());
        } finally {
            if (lock != null) {
                lock.unlock();
                log.info("创作中心：创作内容任务解锁成功：{}", lockKey);
            }
        }
    }

    /**
     * 获取最大重试次数
     *
     * @param request 请求
     * @return 最大失败次数
     */
    private Integer getMaxRetry(CreativeContentExecuteReqVO request) {
        if (BooleanUtils.isTrue(request.getForce())) {
            return Integer.MAX_VALUE;
        }
        return Objects.isNull(request.getMaxRetry()) ? 3 : request.getMaxRetry();
    }

    /**
     * 获取最新的创作内容
     *
     * @param request  请求
     * @param maxRetry 最大重试次数
     * @param start    开始时间
     * @return 最新创作内容
     */
    private CreativeContentDO getLatestContent(CreativeContentExecuteReqVO request, Integer maxRetry, LocalDateTime start) {
        // 获取最新的创作内容
        CreativeContentDO latestContent = creativeContentMapper.get(request.getUid());

        // 校验创作内容是否存在，如果不存在，则直接抛出异常
        AppValidate.notNull(latestContent, "创作内容任务不存在，无法执行！");

        // 如果是正在执行中，则直接抛出异常
        boolean isExecuting = CreativeContentStatusEnum.EXECUTING.name().equals(latestContent.getStatus());
        AppValidate.isFalse(isExecuting, "创作内容任务正在执行中！请稍后重试！任务状态：{}", latestContent.getStatus());

        // 如果是不需要强制执行的情况下，则需要判断是否是成功状态和是否是最终失败状态
        if (!request.getForce()) {
            // 是否成功执行，如果已经成功执行，则直接抛出异常
            boolean isSuccess = CreativeContentStatusEnum.SUCCESS.name().equals(latestContent.getStatus());
            AppValidate.isFalse(isSuccess, "创作内容任务已执行成功！任务状态：{}", latestContent.getStatus());

            // 是否是最终失败或者超过阈值，如果是的，则直接抛出异常
            boolean isUltimateFailure = CreativeContentStatusEnum.ULTIMATE_FAILURE.name().equals(latestContent.getStatus()) || latestContent.getRetryCount() >= maxRetry;
            AppValidate.isFalse(isUltimateFailure, "创作内容任务: 任务状态：{}, 已执行次数：{}, 最大重试次数: {}", latestContent.getStatus(), latestContent.getRetryCount(), maxRetry);
        }

        // 获取并且校验执行参数
        CreativeContentExecuteParam executeParams = CreativeContentConvert.INSTANCE.toExecuteParam(latestContent.getExecuteParam());
        if (Objects.isNull(executeParams) || Objects.isNull(executeParams.getAppInformation())) {
            // 执行参数为空，说明该任务数据存在问题。需要更新状态
            updateContentUltimateFailure(latestContent, start, "创作内容任务执行参数不存在！", maxRetry);
            throw exception(ErrorCodeConstants.PARAMETER_EXCEPTION.getCode(), "创作内容任务执行参数不存在！");
        }

        return latestContent;
    }

    /**
     * 执行应用
     *
     * @param latestContent 创作内容
     * @return 应用执行结果
     */
    private AppExecuteRespVO appExecute(CreativeContentDO latestContent, Integer maxRetry) {
        // 获取到待执行的应用
        CreativeContentExecuteParam executeParams = CreativeContentConvert.INSTANCE.toExecuteParam(latestContent.getExecuteParam());
        AppMarketRespVO appResponse = executeParams.getAppInformation();

        // 执行扩展信息
        int retry = latestContent.getRetryCount() + 1;
        Map<String, Object> extended = new HashMap<>();
        extended.put("planUid", latestContent.getPlanUid());
        extended.put("batchUid", latestContent.getBatchUid());
        extended.put("contentUid", latestContent.getUid());
        extended.put("contentRetryCount", retry >= maxRetry ? maxRetry : retry);
        extended.put("contentMaxRetry", maxRetry);
        extended.put("contentStatus", latestContent.getStatus());
        extended.put("contentSource", latestContent.getSource());
        extended.put("isSendAlarm", false);

        // 构建应用执行参数
        AppExecuteReqVO appExecuteRequest = new AppExecuteReqVO();
        appExecuteRequest.setAppUid(appResponse.getUid());
        // appExecuteRequest.setStepId(stepId);
        appExecuteRequest.setContinuous(Boolean.TRUE);
        appExecuteRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        appExecuteRequest.setUserId(Long.valueOf(latestContent.getCreator()));
        appExecuteRequest.setAppReqVO(AppConvert.INSTANCE.convertRequest(appResponse));
        appExecuteRequest.setConversationUid(latestContent.getConversationUid());
        appExecuteRequest.setExtended(extended);

        AppExecuteRespVO response;
        // 执行应用
        if (CreativePlanSourceEnum.isApp(latestContent.getSource())) {
            AppEntity entity = AppFactory.factoryApp(appExecuteRequest);
            response = entity.execute(appExecuteRequest);
        } else {
            AppMarketEntity entity = AppFactory.factoryMarket(appExecuteRequest);
            response = entity.execute(appExecuteRequest);
        }

        return response;
    }

    /**
     * 查询应用日志信息
     *
     * @param response 结果
     * @return 应用日志信息
     */
    private AppLogMessageRespVO getAppLogMessageRespVO(AppExecuteRespVO response) {
        // 查询生成日志
        LogAppMessagePageReqVO logQuery = new LogAppMessagePageReqVO();
        logQuery.setAppConversationUid(response.getConversationUid());
        AppLogMessageRespVO logAppMessage = appLogService.getLogAppMessageDetail(logQuery);
        // 校验日志信息状态
        if (LogStatusEnum.ERROR.name().equals(logAppMessage.getStatus())) {
            throw exception(350600110, "创作内容执行失败，错误码：" + logAppMessage.getErrorCode() + ",错误信息：" + logAppMessage.getErrorMessage());
        }
        return logAppMessage;
    }

    /**
     * 更新创作内容为执行中状态
     *
     * @param latestContent 创作内容
     * @param start         开始时间
     */
    private void updateContentExecuting(CreativeContentDO latestContent, LocalDateTime start) {
        CreativeContentDO executing = new CreativeContentDO();
        executing.setId(latestContent.getId());
        executing.setStartTime(start);
        executing.setStatus(CreativeContentStatusEnum.EXECUTING.name());
        executing.setUpdater(latestContent.getUpdater());
        creativeContentMapper.updateById(executing);
    }


    /**
     * 执行失败，更新创作内容
     *
     * @param latestContent 创作内容ID
     * @param start         开始时间
     * @param errorMsg      错误信息
     * @param retry         重试次数
     * @param maxRetry      最大重试次数
     */
    private void updateContentFailure(CreativeContentDO latestContent, LocalDateTime start, String errorMsg, Integer retry, Integer maxRetry) {
        // 重试次数大于阈值，更新为最终失败
        if (retry >= (maxRetry - 1)) {
            updateContentUltimateFailure(latestContent, start, errorMsg, maxRetry);
            return;
        }

        LocalDateTime end = LocalDateTime.now();
        long elapsed = Duration.between(start, end).toMillis();

        CreativeContentDO content = new CreativeContentDO();
        content.setId(latestContent.getId());
        content.setErrorMessage(errorMsg);
        content.setRetryCount(retry + 1);
        content.setStatus(CreativeContentStatusEnum.FAILURE.name());
        content.setStartTime(start);
        content.setEndTime(end);
        content.setElapsed(elapsed);
        content.setUpdateTime(end);
        content.setUpdater(latestContent.getUpdater());
        creativeContentMapper.updateById(content);
    }

    /**
     * 根据异常更新创作内容状态
     *
     * @param latestContent 创作内容
     * @param start         开始时间
     * @param throwable     异常
     * @param maxRetry      最大重试次数
     */
    private void updateContentFailureByThrowable(CreativeContentDO latestContent, LocalDateTime start, Integer maxRetry, Throwable throwable) {

        CreativeContentDO recheck = creativeContentMapper.get(latestContent.getUid());
        if (CreativeContentStatusEnum.CANCELED.name().equals(recheck.getStatus())) {
            updateContentCanceled(recheck, start);
            return;
        }

        // 如果是 Error，说明是系统异常，直接更新为最终失败即可。
        if (throwable instanceof Error) {
            updateContentUltimateFailure(latestContent, start, throwable.getMessage(), maxRetry);
            return;
        }

        // 如果是 ServiceException，且在快速失败名单内，直接更新为最终失败即可。
        if (throwable instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) throwable;
            if (FAILURE_FAST_CODE_LIST.contains(serviceException.getCode())) {
                updateContentUltimateFailure(latestContent, start, throwable.getMessage(), maxRetry);
                return;
            }
        }

        // 如果是 ServiceException 的 cause 是 ServiceException，且在快速失败名单内，直接更新为最终失败即可。
        if (Objects.nonNull(throwable.getCause()) && throwable.getCause() instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) throwable.getCause();
            if (FAILURE_FAST_CODE_LIST.contains(serviceException.getCode())) {
                updateContentUltimateFailure(latestContent, start, throwable.getMessage(), maxRetry);
                return;
            }
        }

        // 其他情况，更新为失败
        updateContentFailure(latestContent, start, throwable.getMessage(), latestContent.getRetryCount(), maxRetry);
    }

    /**
     * 执行失败，更新创作内容, 更新为最终失败
     *
     * @param latestContent 创作内容
     * @param start         开始时间
     * @param errorMsg      错误信息
     */
    private void updateContentUltimateFailure(CreativeContentDO latestContent, LocalDateTime start, String errorMsg, Integer maxRetry) {
        LocalDateTime end = LocalDateTime.now();
        long elapsed = Duration.between(start, end).toMillis();

        CreativeContentDO content = new CreativeContentDO();
        content.setId(latestContent.getId());
        content.setErrorMessage(errorMsg);
        content.setRetryCount(maxRetry);
        content.setStatus(CreativeContentStatusEnum.ULTIMATE_FAILURE.name());
        content.setEndTime(end);
        content.setElapsed(elapsed);
        content.setUpdateTime(end);
        content.setUpdater(latestContent.getUpdater());
        creativeContentMapper.updateById(content);
    }

    /**
     * 更新创作内容结果
     *
     * @param start         开始时间
     * @param latestContent 创作内容
     * @param executeResult 执行结果
     */
    private void updateContentSuccess(CreativeContentDO latestContent, CreativeContentExecuteResult executeResult, LocalDateTime start) {
        LocalDateTime end = LocalDateTime.now();
        long elapsed = Duration.between(start, end).toMillis();
        CreativeContentExecuteResult result = CreativeContentServiceImpl.getExecuteResult(latestContent);
        executeResult.setVideo(result.getVideo());
        executeResult.setResource(result.getResource());

        // 更新执行结果
        CreativeContentDO updateContent = new CreativeContentDO();
        updateContent.setId(latestContent.getId());
        updateContent.setExecuteResult(JsonUtils.toJsonString(executeResult));
        updateContent.setExecuteTitle(executeResult.getCopyWriting().getTitle());
        updateContent.setExecuteTags(StringUtil.toString(executeResult.getCopyWriting().getTagList()));
        updateContent.setStartTime(start);
        updateContent.setEndTime(end);
        updateContent.setElapsed(elapsed);
        updateContent.setStatus(CreativeContentStatusEnum.SUCCESS.name());
        updateContent.setUpdateTime(end);
        updateContent.setUpdater(latestContent.getUpdater());
        creativeContentMapper.updateById(updateContent);
    }

    private void updateContentCanceled(CreativeContentDO latestContent, LocalDateTime start) {
        LocalDateTime end = LocalDateTime.now();
        long elapsed = Duration.between(start, end).toMillis();

        // 更新执行结果
        CreativeContentDO updateContent = new CreativeContentDO();
        updateContent.setId(latestContent.getId());
        updateContent.setExecuteResult(null);
        updateContent.setExecuteTitle(null);
        updateContent.setExecuteTags(null);
        updateContent.setStartTime(start);
        updateContent.setEndTime(end);
        updateContent.setElapsed(elapsed);
        updateContent.setStatus(CreativeContentStatusEnum.CANCELED.name());
        updateContent.setUpdateTime(end);
        updateContent.setUpdater(latestContent.getUpdater());
        creativeContentMapper.updateById(updateContent);
    }

    /**
     * 用户权益检测
     *
     * @param latestContent 创作内容
     * @param start         开始时间
     * @param maxRetry      最大重试次数
     */
    private void calculateUserRightsEnough(CreativeContentDO latestContent, LocalDateTime start, Integer maxRetry) {
        // 校验用户权益，判断是否有足够的权益
        if (!adminUserRightsApi.calculateUserRightsEnough(Long.valueOf(latestContent.getCreator()), AdminUserRightsTypeEnum.MATRIX_BEAN, null)) {
            updateContentUltimateFailure(latestContent, start, "用户矩阵权益不足，请及时升级或者充值！", maxRetry);
            throw exception(USER_RIGHTS_MATRIX_BEAN_NOT_ENOUGH.getCode(), "用户矩阵权益不足，请及时升级或者充值！");
        }
    }

    /**
     * 权益扣除
     *
     * @param latestContent 创作内容
     */
    private void reduceRights(CreativeContentDO latestContent) {
        ReduceRightsDTO reduceRights = new ReduceRightsDTO();
        reduceRights.setUserId(Long.valueOf(latestContent.getCreator()));
        reduceRights.setTeamOwnerId(null);
        reduceRights.setTeamId(null);
        reduceRights.setRightType(AdminUserRightsTypeEnum.MATRIX_BEAN.getType());
        reduceRights.setReduceNums(1);
        reduceRights.setBizType(UserRightSceneUtils.getUserRightsBizType(AppSceneEnum.XHS_WRITING.name()).getType());
        reduceRights.setBizId(latestContent.getConversationUid());
        adminUserRightsApi.reduceRights(reduceRights);
    }

    /**
     * 构造结果
     *
     * @param logAppMessage 应用日志
     * @param content       创作内容
     * @return 返回结果
     */
    private static CreativeContentExecuteRespVO buildResponse(AppLogMessageRespVO logAppMessage, CreativeContentDO content) {
        AppRespVO appInfo = logAppMessage.getAppInfo();
        CreativeContentExecuteRespVO response = practicalConverter(appInfo);
        response.setUid(content.getUid());
        response.setPlanUid(content.getPlanUid());
        response.setBatchUid(content.getBatchUid());
        return response;
    }

    /**
     * 构造结果
     *
     * @param appResponse 应用信息
     * @return 执行结果
     */
    public static CreativeContentExecuteRespVO practicalConverter(AppRespVO appResponse) {
        // 获取到组装步骤
        WorkflowStepWrapperRespVO assembleWrapper = appResponse.getStepByHandler(AssembleActionHandler.class.getSimpleName());
        AppValidate.notNull(assembleWrapper, "生成笔记步骤未找到！请联系管理员！");

        WorkflowStepRespVO assembleStep = assembleWrapper.getFlowStep();
        AppValidate.notNull(assembleStep, "生成笔记步骤配置异常，请联系管理员！");

        ActionResponseRespVO assembleResponse = assembleStep.getResponse();
        if (Objects.isNull(assembleResponse) || !assembleResponse.getSuccess()) {
            throw exception(ErrorCodeConstants.PARAMETER_EXCEPTION.getCode(), "生成笔记结果异常！请联系管理员！");
        }

        JsonDataVO assembleOutput = assembleResponse.getOutput();
        if (Objects.isNull(assembleOutput) || Objects.isNull(assembleOutput.getData())) {
            throw exception(ErrorCodeConstants.PARAMETER_EXCEPTION.getCode(), "生成笔记结果不存在！请联系管理员！");
        }

        // 获取到图片生成步骤
        WorkflowStepWrapperRespVO posterWrapper = appResponse.getStepByHandler(PosterActionHandler.class.getSimpleName());
        AppValidate.notNull(posterWrapper, "图片生成步骤未找到！请联系管理员！");

        WorkflowStepRespVO posterStep = posterWrapper.getFlowStep();
        AppValidate.notNull(posterStep, "图片生成步骤配置异常，请联系管理员！");

        ActionResponseRespVO posterResponse = posterStep.getResponse();
        if (Objects.isNull(posterResponse) || !posterResponse.getSuccess() || StringUtil.isBlank(posterResponse.getAnswer())) {
            throw exception(ErrorCodeConstants.PARAMETER_EXCEPTION.getCode(), "图片生成结果异常！请联系管理员！");
        }

        // 文案生成结果
        CopyWritingContent copyWriting = JsonUtils.parseObject(String.valueOf(assembleOutput.getData()), CopyWritingContent.class);
        copyWriting = Optional.ofNullable(copyWriting).orElseThrow(() -> exception(ErrorCodeConstants.PARAMETER_EXCEPTION.getCode(), "文案生成结果异常！请联系管理员！"));
        // 处理文案标题
        String title = copyWriting.getTitle();
        if (StringUtils.isNotBlank(title)) {
            title = MarkdownUtils.clean(title);
        }
        copyWriting.setTitle(title);

        // 处理文案内容
        String content = copyWriting.getContent();
        if (StringUtils.isNotBlank(content)) {
            // 清除html标签
            content = MarkdownUtils.clean(content);
        }
        copyWriting.setContent(content);

        // 图片生成结果
        List<PosterGenerationHandler.Response> posterList = JsonUtils.parseArray(posterResponse.getAnswer(), PosterGenerationHandler.Response.class);

        List<ImageContent> imageList = new ArrayList<>();
        for (PosterGenerationHandler.Response response : posterList) {
            List<PosterImage> urlList = CollectionUtil.emptyIfNull(response.getUrlList());
            for (PosterImage posterImage : urlList) {
                ImageContent imageContent = new ImageContent();
                imageContent.setCode(response.getCode());
                imageContent.setIndex(response.getIndex());
                imageContent.setName(response.getName());
                imageContent.setIsMain(response.getIsMain());
                imageContent.setUrl(posterImage.getUrl());
                imageContent.setFinalParams(posterImage.getFinalParams());
                imageList.add(imageContent);
            }
        }
        // 组装结果
        CreativeContentExecuteResult result = new CreativeContentExecuteResult();
        result.setCopyWriting(copyWriting);
        result.setImageList(imageList);

        CreativeContentExecuteRespVO response = new CreativeContentExecuteRespVO();
        response.setSuccess(Boolean.TRUE);
        response.setResult(result);
        return response;
    }

    /**
     * 执行失败，更新创作内容
     *
     * @param code    错误码
     * @param message 错误信息
     * @param args    参数
     * @return {@link ServiceException}
     */
    private static ServiceException exception(Integer code, String message, Object... args) {
        return ServiceExceptionUtil.exception(ofError(code, message, args));
    }

    /**
     * 执行失败，更新创作内容
     *
     * @param code    错误码
     * @param message 错误信息
     * @param args    参数
     * @return {@link ErrorCode}
     */
    private static ErrorCode ofError(Integer code, String message, Object... args) {
        return new ErrorCode(code, formatErrorMsg(message, args));
    }

    /**
     * 格式化错误信息
     *
     * @param message 错误信息
     * @param args    参数
     * @return 格式化后的错误信息
     */
    private static String formatErrorMsg(String message, Object... args) {
        return String.format(message, args);
    }

}
