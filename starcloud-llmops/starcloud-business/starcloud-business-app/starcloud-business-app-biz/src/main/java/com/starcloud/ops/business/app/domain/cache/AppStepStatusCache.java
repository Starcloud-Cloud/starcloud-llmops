package com.starcloud.ops.business.app.domain.cache;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.app.dto.AppExecuteProgressDTO;
import com.starcloud.ops.business.app.api.app.dto.AppStepStatusDTO;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.workflow.WorkflowStepEntity;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppStepStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@Component
public class AppStepStatusCache {

    /**
     * 后置处理器步骤
     */
    public static final String POST_PROCESSOR_HANDLER = "POST_PROCESSOR_HANDLER";

    /**
     * 步骤状态缓存
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static final TimedCache<String, LinkedHashMap<String, AppStepStatusDTO>> APP_STEP_STATUS_CACHE = CacheUtil.newTimedCache(1000 * 60 * 30);

    /**
     * 获取执行进度信息
     *
     * @param conversationUid 会话 UID
     * @return 执行进度信息
     */
    public AppExecuteProgressDTO getProgress(String conversationUid) {
        // 获取步骤状态缓存
        LinkedHashMap<String, AppStepStatusDTO> appStepStatusMap = get(conversationUid);
        if (CollectionUtil.isEmpty(appStepStatusMap)) {
            return null;
        }

        AppExecuteProgressDTO progress = new AppExecuteProgressDTO();
        // 总的步骤数量
        progress.setTotalStepCount(appStepStatusMap.size());
        // 成功的步骤数量
        int successCount = (int) appStepStatusMap.values().stream().filter(stepItem -> AppStepStatusEnum.SUCCESS.name().equals(stepItem.getStatus())).count();
        progress.setSuccessStepCount(successCount);

        int currentStepIndex;
        if (successCount < appStepStatusMap.size()) {
            // 当前步骤索引值，直接去成功数量，因为是顺序执行的。
            currentStepIndex = successCount + 1;
        } else {
            // 所有步骤都成功，设置为总的步骤数量
            currentStepIndex = appStepStatusMap.size();
        }
        // 当前步骤索引值，直接去成功数量，因为是顺序执行的。
        progress.setCurrentStepIndex(currentStepIndex);

        return progress;
    }

    /**
     * 初始化步骤状态缓存
     *
     * @param conversationUid 会话 UID
     */
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    public void init(String conversationUid, BaseAppEntity baseAppEntity) {
        log.info("[init][conversationUid({}) 初始化步骤状态缓存]", conversationUid);
        // 初始化步骤状态缓存
        if (APP_STEP_STATUS_CACHE.containsKey(conversationUid)) {
            APP_STEP_STATUS_CACHE.remove(conversationUid);
        }
        // 保证步骤顺序
        LinkedHashMap<String, AppStepStatusDTO> stepStatusMap = new LinkedHashMap<>();
        Optional<List<WorkflowStepWrapper>> stepWrappersOptional = Optional.ofNullable(baseAppEntity.getWorkflowConfig()).map(WorkflowConfigEntity::getSteps);
        if (!stepWrappersOptional.isPresent()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.WORKFLOW_STEP_NOT_EXIST);
        }
        for (WorkflowStepWrapper stepWrapper : CollectionUtil.emptyIfNull(stepWrappersOptional.get())) {
            String stepId = stepWrapper.getStepCode();
            WorkflowStepEntity flowStep = stepWrapper.getFlowStep();
            String handleCode = flowStep.getHandler();
            stepStatusMap.put(stepWrapper.getStepCode(), AppStepStatusDTO.initOf(stepId, handleCode));
        }

        // 放入一个后置处理器步骤
        AppStepStatusDTO postProcessorStep = AppStepStatusDTO.initOf(POST_PROCESSOR_HANDLER, POST_PROCESSOR_HANDLER);
        stepStatusMap.put(POST_PROCESSOR_HANDLER, postProcessorStep);

        // 将步骤信息放入缓存
        APP_STEP_STATUS_CACHE.put(conversationUid, stepStatusMap);
        log.info("[init][conversationUid({}) 初始化步骤状态缓存 完成]", conversationUid);
    }

    /**
     * 根据会话ID获取步骤状态
     *
     * @param conversationUid 会话 UID
     * @return 步骤状态
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public LinkedHashMap<String, AppStepStatusDTO> get(String conversationUid) {
        if (APP_STEP_STATUS_CACHE.containsKey(conversationUid)) {
            if (APP_STEP_STATUS_CACHE.get(conversationUid) != null) {
                return APP_STEP_STATUS_CACHE.get(conversationUid);
            }
        }
        return null;
    }

    /**
     * 获取步骤状态
     *
     * @param conversationUid 会话 UID
     * @param stepId          步骤 ID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void stepStart(String conversationUid, String stepId) {
        log.info("[appStepStart][conversationUid({}) stepId({}) 开始]", conversationUid, stepId);
        if (APP_STEP_STATUS_CACHE.containsKey(conversationUid)) {
            LinkedHashMap<String, AppStepStatusDTO> stepStatusMap = APP_STEP_STATUS_CACHE.get(conversationUid);
            if (stepStatusMap == null) {
                log.warn("[appStepStart][conversationUid({}) stepId({}) 不存在]", conversationUid, stepId);
                return;
            }
            if (stepStatusMap.containsKey(stepId)) {
                AppStepStatusDTO appStepStatus = stepStatusMap.get(stepId);
                if (appStepStatus == null) {
                    log.warn("[appStepStart][conversationUid({}) stepId({}) 不存在]", conversationUid, stepId);
                    return;
                }
                if (!AppStepStatusEnum.WAITING.name().equals(appStepStatus.getStatus())) {
                    log.warn("[appStepStart][conversationUid({}) stepId({}) 状态({}) 不是 WAITING，不允许开始]", conversationUid, stepId, appStepStatus.getStatus());
                    return;
                }
                appStepStatus.setStartTime(LocalDateTime.now());
                appStepStatus.setStatus(AppStepStatusEnum.RUNNING.name());

                stepStatusMap.put(stepId, appStepStatus);
                APP_STEP_STATUS_CACHE.put(conversationUid, stepStatusMap);
            }
        }
        log.info("[appStepStart][conversationUid({}) stepId({}) 结束]", conversationUid, stepId);
    }

    /**
     * 获取步骤状态
     *
     * @param conversationUid 会话 UID
     * @param stepId          步骤 ID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void stepSuccess(String conversationUid, String stepId) {
        log.info("[appStepSuccess][conversationUid({}) stepId({}) 开始]", conversationUid, stepId);
        if (APP_STEP_STATUS_CACHE.containsKey(conversationUid)) {
            LinkedHashMap<String, AppStepStatusDTO> stepStatusMap = APP_STEP_STATUS_CACHE.get(conversationUid);
            if (stepStatusMap == null) {
                log.warn("[appStepSuccess][conversationUid({}) stepId({}) 不存在]", conversationUid, stepId);
                return;
            }
            if (stepStatusMap.containsKey(stepId)) {
                AppStepStatusDTO appStepStatus = stepStatusMap.get(stepId);
                if (appStepStatus == null) {
                    log.warn("[appStepSuccess][conversationUid({}) stepId({}) 不存在]", conversationUid, stepId);
                    return;
                }
                if (!AppStepStatusEnum.RUNNING.name().equals(appStepStatus.getStatus())) {
                    log.warn("[appStepSuccess][conversationUid({}) stepId({}) 状态({}) 不是 RUNNING，不允许结束]", conversationUid, stepId, appStepStatus.getStatus());
                    return;
                }
                appStepStatus.setEndTime(LocalDateTime.now());
                appStepStatus.setElapsed(LocalDateTimeUtil.toEpochMilli(appStepStatus.getEndTime()) - LocalDateTimeUtil.toEpochMilli(appStepStatus.getStartTime()));
                appStepStatus.setStatus(AppStepStatusEnum.SUCCESS.name());
                stepStatusMap.put(stepId, appStepStatus);
                APP_STEP_STATUS_CACHE.put(conversationUid, stepStatusMap);
            }
        }
        log.info("[appStepSuccess][conversationUid({}) stepId({}) 结束]", conversationUid, stepId);
    }

    /**
     * 获取步骤状态
     *
     * @param conversationUid 会话 UID
     * @param stepId          步骤 ID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void stepFailure(String conversationUid, String stepId, String errorCode, String errorMessage) {
        log.info("[appStepFailure][conversationUid({}) stepId({}) 开始]", conversationUid, stepId);
        if (APP_STEP_STATUS_CACHE.containsKey(conversationUid)) {
            LinkedHashMap<String, AppStepStatusDTO> stepStatusMap = APP_STEP_STATUS_CACHE.get(conversationUid);

            if (stepStatusMap == null) {
                log.warn("[appStepFailure][conversationUid({}) stepId({}) 不存在]", conversationUid, stepId);
                return;
            }

            if (stepStatusMap.containsKey(stepId)) {
                AppStepStatusDTO appStepStatus = stepStatusMap.get(stepId);
                if (appStepStatus == null) {
                    log.warn("[appStepFailure][conversationUid({}) stepId({}) 不存在]", conversationUid, stepId);
                    return;
                }
                if (!AppStepStatusEnum.RUNNING.name().equals(appStepStatus.getStatus())) {
                    log.warn("[appStepFailure][conversationUid({}) stepId({}) 状态({}) 不是 RUNNING，不允许结束]", conversationUid, stepId, appStepStatus.getStatus());
                    return;
                }
                appStepStatus.setEndTime(LocalDateTime.now());
                appStepStatus.setElapsed(LocalDateTimeUtil.toEpochMilli(appStepStatus.getEndTime()) - LocalDateTimeUtil.toEpochMilli(appStepStatus.getStartTime()));
                appStepStatus.setStatus(AppStepStatusEnum.FAILED.name());
                appStepStatus.setErrorCode(errorCode);
                appStepStatus.setErrorMessage(errorMessage);

                stepStatusMap.put(stepId, appStepStatus);
                APP_STEP_STATUS_CACHE.put(conversationUid, stepStatusMap);
            }
        }
        log.info("[appStepFailure][conversationUid({}) stepId({}) 结束]", conversationUid, stepId);
    }

}
