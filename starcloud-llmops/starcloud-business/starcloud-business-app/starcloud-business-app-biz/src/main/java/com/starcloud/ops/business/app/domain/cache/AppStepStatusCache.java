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
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppStepStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@SuppressWarnings("all")
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
     * @param conversation 会话 UID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void init(String conversation, BaseAppEntity appEntity) {
        log.info("【应用执行步骤缓存】初始化步骤状态缓存【开始】，会话UID: {}, 应用UID: {}", conversation, appEntity.getUid());
        // 初始化步骤状态缓存
        if (APP_STEP_STATUS_CACHE.containsKey(conversation)) {
            APP_STEP_STATUS_CACHE.remove(conversation);
        }

        // 保证步骤顺序
        LinkedHashMap<String, AppStepStatusDTO> stepStatusMap = new LinkedHashMap<>();
        Optional<List<WorkflowStepWrapper>> stepWrappersOptional = Optional.ofNullable(appEntity.getWorkflowConfig())
                .map(WorkflowConfigEntity::stepWrapperList);

        if (!stepWrappersOptional.isPresent() || CollectionUtil.isEmpty(stepWrappersOptional.get())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.WORKFLOW_STEP_NOT_EXIST);
        }

        for (WorkflowStepWrapper stepWrapper : stepWrappersOptional.get()) {
            String stepId = stepWrapper.getStepCode();
            String handleCode = stepWrapper.getHandler();

            stepStatusMap.put(stepId, AppStepStatusDTO.initOf(stepId, handleCode));
        }

        // 放入一个后置处理器步骤
        AppStepStatusDTO postProcessorStep = AppStepStatusDTO.initOf(POST_PROCESSOR_HANDLER, POST_PROCESSOR_HANDLER);
        stepStatusMap.put(POST_PROCESSOR_HANDLER, postProcessorStep);

        // 将步骤信息放入缓存
        APP_STEP_STATUS_CACHE.put(conversation, stepStatusMap);

        log.info("【应用执行步骤缓存】初始化步骤状态缓存【结束】，会话UID: {}, 应用UID: {}", conversation, appEntity.getUid());
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
        return APP_STEP_STATUS_CACHE.get(conversationUid);
    }

    /**
     * 获取步骤状态
     *
     * @param conversation 会话 UID
     * @param stepId       步骤 ID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void stepStart(String conversation, String stepId, BaseAppEntity appEntity) {
        log.info("【应用执行步骤缓存】【{}步骤】更新缓存为【执行中】开始: 会话UID: {}, 应用UID: {}", stepId, conversation, appEntity.getUid());
        updateStepStatus(conversation, stepId, appEntity, AppStepStatusEnum.RUNNING);
        log.info("【应用执行步骤缓存】【{}步骤】更新缓存为【执行中】结束: 会话UID: {}, 应用UID: {}", stepId, conversation, appEntity.getUid());
    }

    /**
     * 获取步骤状态
     *
     * @param conversation 会话 UID
     * @param stepId          步骤 ID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void stepSuccess(String conversation, String stepId, BaseAppEntity appEntity) {
        log.info("【应用执行步骤缓存】【{}步骤】更新缓存为【成功】开始: 会话UID: {}, 应用UID: {}", stepId, conversation, appEntity.getUid());
        updateStepStatus(conversation, stepId, appEntity, AppStepStatusEnum.SUCCESS);
        log.info("【应用执行步骤缓存】【{}步骤】更新缓存为【成功】结束: 会话UID: {}, 应用UID: {}", stepId, conversation, appEntity.getUid());
    }

    /**
     * 获取步骤状态
     *
     * @param conversation 会话 UID
     * @param stepId          步骤 ID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void stepFailure(String conversation, String stepId, BaseAppEntity appEntity) {
        log.info("【应用执行步骤缓存】【{}步骤】更新缓存为【失败】开始: 会话UID: {}, 应用UID: {}", stepId, conversation, appEntity.getUid());
        updateStepStatus(conversation, stepId, appEntity, AppStepStatusEnum.FAILED);
        log.info("【应用执行步骤缓存】【{}步骤】更新缓存为【失败】结束: 会话UID: {}, 应用UID: {}", stepId, conversation, appEntity.getUid());
    }

    /**
     * 更新步骤状态
     *
     * @param conversation 会话 UID
     * @param stepId       步骤 ID
     * @param appEntity    应用实体
     * @param status       状态
     */
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    private void updateStepStatus(String conversation, String stepId, BaseAppEntity appEntity, AppStepStatusEnum status) {
        // 如果不存在，则初始化一次
        if (!APP_STEP_STATUS_CACHE.containsKey(conversation) || Objects.isNull(APP_STEP_STATUS_CACHE.get(conversation))
                || Objects.isNull(APP_STEP_STATUS_CACHE.get(conversation).get(stepId))) {
            init(conversation, appEntity);
            LinkedHashMap<String, AppStepStatusDTO> stepStatusMap = get(conversation);
            for (Map.Entry<String, AppStepStatusDTO> entry : stepStatusMap.entrySet()) {
                String stepCode = entry.getKey();
                AppStepStatusDTO appStepStatus = entry.getValue();
                if (stepCode.equals(stepId)) {
                    if (AppStepStatusEnum.RUNNING.equals(status)) {
                        appStepStatus.setStartTime(LocalDateTime.now());
                        appStepStatus.setStatus(AppStepStatusEnum.RUNNING.name());
                    } else {
                        if (Objects.isNull(appStepStatus.getStartTime())) {
                            appStepStatus.setStartTime(LocalDateTime.now());
                        }
                        appStepStatus.setEndTime(LocalDateTime.now());
                        appStepStatus.setElapsed(LocalDateTimeUtil.toEpochMilli(appStepStatus.getEndTime()) - LocalDateTimeUtil.toEpochMilli(appStepStatus.getStartTime()));
                        appStepStatus.setStatus(status.name());
                    }
                    stepStatusMap.put(stepId, appStepStatus);
                    break;
                }
                appStepStatus.setStatus(AppStepStatusEnum.SUCCESS.name());
                appStepStatus.setStartTime(LocalDateTime.now());
                appStepStatus.setEndTime(LocalDateTime.now());
                appStepStatus.setElapsed(0L);
                stepStatusMap.put(stepCode, appStepStatus);
            }
            APP_STEP_STATUS_CACHE.put(conversation, stepStatusMap);
            return;
        }

        LinkedHashMap<String, AppStepStatusDTO> stepStatusMap = APP_STEP_STATUS_CACHE.get(conversation);
        AppStepStatusDTO appStepStatus = stepStatusMap.get(stepId);
        if (AppStepStatusEnum.RUNNING.equals(status)) {
            appStepStatus.setStartTime(LocalDateTime.now());
            appStepStatus.setStatus(AppStepStatusEnum.RUNNING.name());
        } else {
            if (Objects.isNull(appStepStatus.getStartTime())) {
                appStepStatus.setStartTime(LocalDateTime.now());
            }
            appStepStatus.setEndTime(LocalDateTime.now());
            appStepStatus.setElapsed(LocalDateTimeUtil.toEpochMilli(appStepStatus.getEndTime()) - LocalDateTimeUtil.toEpochMilli(appStepStatus.getStartTime()));
            appStepStatus.setStatus(status.name());
        }

        stepStatusMap.put(stepId, appStepStatus);
        APP_STEP_STATUS_CACHE.put(conversation, stepStatusMap);
    }
}
