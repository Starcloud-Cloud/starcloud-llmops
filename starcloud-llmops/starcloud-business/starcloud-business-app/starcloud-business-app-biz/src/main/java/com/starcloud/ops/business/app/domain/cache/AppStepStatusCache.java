package com.starcloud.ops.business.app.domain.cache;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.app.dto.AppExecuteProgress;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppStepStatusEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 应用执行步骤缓存，目前仅针对应用和应用市场多步骤执行时候的场景。
 *
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
     * Redis 模板
     */
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 设置缓存
     *
     * @param conversation 会话UID
     * @return 缓存的Key
     */
    public LinkedHashMap<String, String> get(String conversation) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String value = operations.get(getCacheKey(conversation));
        return StringUtils.isNotBlank(value) ? parse(value) : null;
    }

    /**
     * 设置缓存，30分钟过期
     *
     * @param conversation 会话UID
     * @param value        缓存的值
     */
    public void set(String conversation, LinkedHashMap<String, String> value) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(getCacheKey(conversation), JsonUtils.toJsonString(value), 30, TimeUnit.MINUTES);
    }

    /**
     * 如果缓存存在，则删除，否则不操作
     */
    public void clear(String conversation) {
        String key = getCacheKey(conversation);
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 获取执行进度信息
     *
     * @param conversationUid 会话 UID
     * @return 执行进度信息
     */
    public AppExecuteProgress progress(String conversationUid) {

        // 获取应用状态缓存
        LinkedHashMap<String, String> appStepStatusMap = get(conversationUid);
        if (CollectionUtil.isEmpty(appStepStatusMap)) {
            return null;
        }

        // 计算总的步骤数量
        int totalStepCount = appStepStatusMap.size();
        // 计算成功的步骤数量
        int successStepCount = (int) appStepStatusMap.values().stream()
                .filter(item -> AppStepStatusEnum.SUCCESS.name().equals(item))
                .count();
        // 当前步骤索引值
        int currentStepIndex = successStepCount < totalStepCount ? successStepCount + 1 : totalStepCount;

        // 构建并且返回进度信息
        AppExecuteProgress progress = new AppExecuteProgress();
        // 总的步骤数量
        progress.setTotalStepCount(appStepStatusMap.size());
        // 成功的步骤数量
        progress.setSuccessStepCount(successStepCount);
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
    public void init(String conversation, BaseAppEntity app) {
        log.info("【应用执行步骤缓存】初始化步骤状态缓存【开始】，会话UID: {}, 应用名称：{}, 应用UID: {}, ", conversation, app.getName(), app.getUid());

        // 非矩阵应用，不进行缓存
        if (!AppTypeEnum.MEDIA_MATRIX.name().equals(app.getType())) {
            log.info("【应用执行步骤缓存】非矩阵应用，讲不进行缓存。会话UID：{}, 应用名称：{}, 应用UID：{}", conversation, app.getName(), app.getUid());
            return;
        }

        // 初始化步骤状态缓存
        clear(conversation);

        // 获取应用步骤列表
        List<WorkflowStepWrapper> stepWrappers = stepWrappers(app);

        // 保证步骤顺序
        LinkedHashMap<String, String> stepStatusMap = new LinkedHashMap<>();
        // 初始化步骤状态
        for (WorkflowStepWrapper stepWrapper : stepWrappers) {
            String stepId = stepWrapper.getStepCode();
            stepStatusMap.put(stepId, AppStepStatusEnum.INIT.name());
        }
        // 放入一个后置处理器步骤
        stepStatusMap.put(POST_PROCESSOR_HANDLER, AppStepStatusEnum.INIT.name());

        // 将步骤信息放入缓存
        set(conversation, stepStatusMap);

        log.info("【应用执行步骤缓存】初始化步骤状态缓存【结束】，会话UID: {}, 应用名称：{}, 应用UID: {}", conversation, app.getName(), app.getUid());
    }

    /**
     * 获取步骤状态
     *
     * @param conversation 会话 UID
     * @param stepId       步骤 ID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void stepStart(String conversation, String stepId, BaseAppEntity app) {
        log.info("【应用执行步骤缓存】【{}步骤】更新缓存为【执行中】开始: 会话UID: {}, 应用名称：{}, 应用UID: {}",
                stepId, conversation, app.getName(), app.getUid());
        // 更新步骤状态
        updateStepStatus(conversation, stepId, app, AppStepStatusEnum.EXECUTING);
        log.info("【应用执行步骤缓存】【{}步骤】更新缓存为【执行中】结束: 会话UID: {}, 应用名称：{}, 应用UID: {}",
                stepId, conversation, app.getName(), app.getUid());
    }

    /**
     * 获取步骤状态
     *
     * @param conversation 会话 UID
     * @param stepId       步骤 ID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void stepSuccess(String conversation, String stepId, BaseAppEntity app) {
        log.info("【应用执行步骤缓存】【{}步骤】更新缓存为【成功】开始: 会话UID: {}, 应用名称：{}, 应用UID: {}",
                stepId, conversation, app.getName(), app.getUid());
        // 更新步骤状态
        updateStepStatus(conversation, stepId, app, AppStepStatusEnum.SUCCESS);
        log.info("【应用执行步骤缓存】【{}步骤】更新缓存为【成功】结束: 会话UID: {}, 应用名称：{}, 应用UID: {}",
                stepId, conversation, app.getName(), app.getUid());
    }

    /**
     * 获取步骤状态
     *
     * @param conversation 会话 UID
     * @param stepId       步骤 ID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void stepFailure(String conversation, String stepId, BaseAppEntity app) {
        log.info("【应用执行步骤缓存】【{}步骤】更新缓存为【失败】开始: 会话UID: {}, 应用名称：{}, 应用UID: {}",
                stepId, conversation, app.getName(), app.getUid());
        updateStepStatus(conversation, stepId, app, AppStepStatusEnum.FAILURE);
        log.info("【应用执行步骤缓存】【{}步骤】更新缓存为【失败】结束: 会话UID: {}, 应用名称：{}, 应用UID: {}",
                stepId, conversation, app.getName(), app.getUid());
    }

    /**
     * 更新步骤状态
     *
     * @param conversation 会话 UID
     * @param stepId       步骤 ID
     * @param app          应用实体
     * @param status       状态
     */
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    private void updateStepStatus(String conversation, String stepId, BaseAppEntity app, AppStepStatusEnum status) {
        // 非矩阵应用，不更新缓存
        if (!AppTypeEnum.MEDIA_MATRIX.name().equals(app.getType())) {
            log.info("【应用执行步骤缓存】非矩阵应用，不更新缓存。会话UID：{}, 更新状态：{}, 应用名称：{}, 应用UID：{}",
                    conversation, status.name(), app.getName(), app.getUid());
            return;
        }

        // 获取缓存
        LinkedHashMap<String, String> stepStatusMap = get(conversation);
        if (CollectionUtil.isEmpty(stepStatusMap)) {
            return;
        }

        // 更新步骤状态
        stepStatusMap.put(stepId, status.name());

        // 更新缓存
        set(conversation, stepStatusMap);
    }

    /**
     * 获取缓存的Key
     *
     * @param conversation 会话UID
     * @return 缓存的Key
     */
    private static String getCacheKey(String conversation) {
        return "APP:EXECUTE:PROGRESS:" + conversation;
    }

    /**
     * 解析缓存结果
     *
     * @param value 缓存值
     * @return 解析结果
     */
    private static LinkedHashMap<String, String> parse(String value) {
        try {
            return JsonUtils.parseLinkedHashMap(value, String.class, String.class);
        } catch (Exception exception) {
            // ingore
            return null;
        }
    }

    /**
     * 获取应用步骤列表
     *
     * @param app 应用实体
     * @return 应用步骤列表
     */
    private static List<WorkflowStepWrapper> stepWrappers(BaseAppEntity app) {
        Optional<List<WorkflowStepWrapper>> stepWrappersOptional = Optional.ofNullable(app.getWorkflowConfig())
                .map(WorkflowConfigEntity::stepWrapperList);

        if (!stepWrappersOptional.isPresent() || CollectionUtil.isEmpty(stepWrappersOptional.get())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.WORKFLOW_STEP_NOT_EXIST);
        }

        return stepWrappersOptional.get();
    }
}
