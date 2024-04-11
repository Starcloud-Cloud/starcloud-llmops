package com.starcloud.ops.business.app.service.xhs.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.content.dto.CreativeContentExecuteParam;
import com.starcloud.ops.business.app.api.xhs.content.dto.CreativeContentExecuteResult;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentExecuteReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.response.CreativeContentExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.content.CreativeContentMapper;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.app.service.xhs.convert.AppResponseConverter;
import com.starcloud.ops.business.app.service.xhs.executor.CreativeThreadPoolHolder;
import com.starcloud.ops.business.app.util.UserRightSceneUtils;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.api.rights.dto.ReduceRightsDTO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Component
@Slf4j
public class CreativeExecuteManager {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private CreativeContentMapper creativeContentMapper;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private CreativeThreadPoolHolder creativeThreadPoolHolder;

    @Resource
    private AppLogService appLogService;

    @Resource
    private AdminUserRightsApi adminUserRightsApi;

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
            return CreativeContentExecuteRespVO.failure(request.getUid(), "该创作内容正在执行中，请稍后再试");
        }

        log.info("创作内容任务上锁成功({})", lockKey);

        try {

            // 开始时间
            LocalDateTime start = LocalDateTime.now();
            log.info("创作内容任务执行开始：{}，{}", start, request.getUid());

            // 获取最大重试次数
            Integer maxRetry = getMaxRetry(request.getForce());

            // 获取最新的创作内容
            CreativeContentDO latestContent = creativeContentMapper.get(request.getUid());
            AppValidate.notNull(latestContent, "创作内容任务不存在，无法执行！");

            // 如果是正在执行中，则直接抛出异常
            boolean isExecuting = CreativeContentStatusEnum.EXECUTING.name().equals(latestContent.getStatus());
            if (CreativeContentStatusEnum.EXECUTING.name().equals(latestContent.getStatus())) {
                log.info("创作内容任务正在执行中({})！", request.getUid());
                return CreativeContentExecuteRespVO.failure(request.getUid(), "创作内容任务正在执行中！");

            }
            // 如果是不需要强制执行的情况下，则需要判断是否是成功状态和是否是最终失败状态
            if (!request.getForce()) {
                // 是否成功执行，如果已经成功执行，则直接返回
                if (CreativeContentStatusEnum.SUCCESS.name().equals(latestContent.getStatus())) {
                    log.info("创作内容任务已成功执行({})，不需要在执行！", latestContent.getUid());
                    return CreativeContentExecuteRespVO.failure(request.getUid(), "创作内容任务已执行成功！");
                }
                // 是否是最终失败或者超过阈值，如果是的，则直接返回
                if (CreativeContentStatusEnum.ULTIMATE_FAILURE.name().equals(latestContent.getStatus()) || latestContent.getRetryCount() >= maxRetry) {
                    log.info("创作内容任务已经超过最大重试次数，无法执行({})！执行次数：{}， 最大重试次数：{}", latestContent.getUid(), latestContent.getRetryCount(), maxRetry);
                    return CreativeContentExecuteRespVO.failure(request.getUid(), formatErrorMsg("创作内容任务已经超过最大重试次数！执行次数：%s， 最大重试次数：%s", latestContent.getRetryCount(), maxRetry));
                }
            }

            // 获取并且校验执行参数
            CreativeContentExecuteParam executeParams = CreativeContentConvert.INSTANCE.toExecuteParam(latestContent.getExecuteParam());
            if (Objects.isNull(executeParams) || Objects.isNull(executeParams.getAppInformation())) {
                // 执行参数为空，说明该任务数据存在问题。需要更新状态
                log.info("创作内容任务执行参数不存在，请联系管理员(UID: {})！", latestContent.getUid());
                updateContentUltimateFailure(latestContent, start, formatErrorMsg("创作内容任务执行参数不存在，请联系管理员(UID: %s)！", latestContent.getUid()), maxRetry);
                return CreativeContentExecuteRespVO.failure(latestContent.getUid(), "创作内容任务执行参数不存在，请联系管理员！");
            }

            // 校验用户权益，判断是否有足够的权益
            if (!adminUserRightsApi.calculateUserRightsEnough(Long.valueOf(latestContent.getCreator()), AdminUserRightsTypeEnum.MATRIX_BEAN, null)) {
                log.info("用户矩阵权益不足，请及时升级或者充值！(UID: {})！", latestContent.getUid());
                updateContentUltimateFailure(latestContent, start, "用户矩阵权益不足，请及时升级或者充值！", maxRetry);
                return CreativeContentExecuteRespVO.failure(latestContent.getUid(), "用户矩阵权益不足，请及时升级或者充值！");
            }

            // 更新创作内容状态为执行中
            updateContentExecuting(latestContent, start);

            // 获取到带执行的应用
            AppMarketRespVO appResponse = executeParams.getAppInformation();

            // 构建应用执行参数
            AppExecuteReqVO appExecuteRequest = new AppExecuteReqVO();
            appExecuteRequest.setAppUid(appResponse.getUid());
            // appExecuteRequest.setStepId(stepId);
            appExecuteRequest.setContinuous(Boolean.TRUE);
            appExecuteRequest.setScene(AppSceneEnum.XHS_WRITING.name());
            appExecuteRequest.setUserId(Long.valueOf(latestContent.getCreator()));
            appExecuteRequest.setAppReqVO(AppConvert.INSTANCE.convertRequest(appResponse));
            appExecuteRequest.setConversationUid(latestContent.getConversationUid());

            // 执行应用
            AppMarketEntity entity = (AppMarketEntity) AppFactory.factory(appExecuteRequest);
            AppExecuteRespVO response = entity.execute(appExecuteRequest);

            if (!response.getSuccess()) {
                throw new ServiceException(350600110, "生成内容和图片失败，错误码：" + response.getResultCode() + ",错误信息：" + response.getResultDesc());
            }

            LogAppMessagePageReqVO logQuery = new LogAppMessagePageReqVO();
            logQuery.setAppConversationUid(response.getConversationUid());
            AppLogMessageRespVO logAppMessage = appLogService.getLogAppMessageDetail(logQuery);

            // 执行失败
            if (LogStatusEnum.ERROR.name().equals(logAppMessage.getStatus())) {
                throw new ServiceException(350600110, "生成内容和图片失败，错误码：" + logAppMessage.getErrorCode() + ",错误信息：" + logAppMessage.getErrorMessage());
            }

            CreativeContentExecuteRespVO executeResponse = buildResponse(logAppMessage, latestContent);
            CreativeContentExecuteResult executeResult = executeResponse.getResult();

            // 更新执行时间
            LocalDateTime end = LocalDateTime.now();
            long elapsed = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();

            // 构建执行结果
            CreativeContentDO updateContent = new CreativeContentDO();
            updateContent.setId(latestContent.getId());
            updateContent.setExecuteResult(JsonUtils.toJsonString(executeResult));
            updateContent.setStartTime(start);
            updateContent.setEndTime(end);
            updateContent.setElapsed(elapsed);
            updateContent.setStatus(CreativeContentStatusEnum.SUCCESS.name());
            updateContent.setUpdateTime(end);
            updateContent.setUpdater(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
            creativeContentMapper.updateById(updateContent);

            // 权益扣除
            reduceRights(latestContent);

            return executeResponse;

        } catch (ServiceException exception) {
            log.error("创作中心：创作内容任务执行失败：错误码: {}, 错误信息: {}", exception.getCode(), exception.getMessage(), exception);
            return CreativeContentExecuteRespVO.failure(request.getUid(), exception.getMessage());
        } catch (Exception exception) {
            log.error("创作中心：创作内容任务执行失败： 错误信息: {}", exception.getMessage(), exception);
            return CreativeContentExecuteRespVO.failure(request.getUid(), exception.getMessage());
        } finally {
            if (lock != null) {
                lock.unlock();
                log.info("创作中心：创作内容任务解锁成功：{}", lockKey);
            }
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

    private void updateContentExecuting(CreativeContentDO latestContent, LocalDateTime start) {
        CreativeContentDO executing = new CreativeContentDO();
        executing.setId(latestContent.getId());
        executing.setStartTime(start);
        executing.setStatus(CreativeContentStatusEnum.EXECUTING.name());
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
        CreativeContentDO content = new CreativeContentDO();
        content.setId(latestContent.getId());
        content.setErrorMessage(errorMsg);
        content.setRetryCount(retry + 1);
        content.setStatus(CreativeContentStatusEnum.FAILURE.name());
        content.setStartTime(start);
        LocalDateTime end = LocalDateTime.now();
        content.setEndTime(end);
        Long elapsed = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        content.setElapsed(elapsed);
        content.setUpdateTime(end);
        creativeContentMapper.updateById(content);
    }

    /**
     * 执行失败，更新创作内容, 更新为最终失败
     *
     * @param latestContent 创作内容
     * @param start         开始时间
     * @param errorMsg      错误信息
     */
    private void updateContentUltimateFailure(CreativeContentDO latestContent, LocalDateTime start, String errorMsg, Integer maxRetry) {
        CreativeContentDO content = new CreativeContentDO();
        content.setId(latestContent.getId());
        content.setErrorMessage(errorMsg);
        content.setRetryCount(maxRetry);
        content.setStatus(CreativeContentStatusEnum.ULTIMATE_FAILURE.name());
        LocalDateTime end = LocalDateTime.now();
        content.setEndTime(end);
        Long executeTime = end.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - start.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        content.setElapsed(executeTime);
        content.setUpdateTime(end);
        creativeContentMapper.updateById(content);
    }

    /**
     * 获取最大重试次数
     *
     * @param force 是否强制执行
     * @return 最大失败次数
     */
    private Integer getMaxRetry(Boolean force) {
        if (BooleanUtils.isTrue(force)) {
            return Integer.MAX_VALUE;
        }
        try {
            DictDataDO dictDataDO = dictDataService.parseDictData("xhs", "max_retry");
            if (dictDataDO == null || dictDataDO.getValue() == null) {
                return 3;
            } else {
                return Integer.valueOf(dictDataDO.getValue());
            }
        } catch (Exception exception) {
            return 3;
        }
    }

    private static CreativeContentExecuteRespVO buildResponse(AppLogMessageRespVO logAppMessage, CreativeContentDO content) {
        AppRespVO appInfo = logAppMessage.getAppInfo();
        List<String> tags = appInfo.getTags();
        if (tags.contains("PracticalConverter")) {
            CreativeContentExecuteRespVO response = AppResponseConverter.practicalConverter(appInfo);
            response.setUid(content.getUid());
            response.setPlanUid(content.getPlanUid());
            response.setBatchUid(content.getBatchUid());
            return response;
        }
        throw ServiceExceptionUtil.exception(new ErrorCode(350600110, "应用结果转换场景结果异常！"));
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
