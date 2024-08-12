package com.starcloud.ops.business.app.domain.entity.workflow.context;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.util.QLExpressUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * App 上下文
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppContext {


    private static ExpressionParser SpelParser = new SpelExpressionParser();


    private static ParserContext ParserContext = new ParserContext() {
        @Override
        public boolean isTemplate() {
            return true;
        }

        @Override
        public String getExpressionPrefix() {
            return "#{";
        }

        @Override
        public String getExpressionSuffix() {
            return "}";
        }
    };

    private static RedissonClient redissonClient = SpringUtil.getBean(RedissonClient.class);


    /**
     * 步骤前缀
     */
    private static final String STEP_PREFIX = "STEP";

    /**
     * 应用 UID, 每个应用的唯一标识
     */
    private String uid;

    /**
     * 会话 UID
     */
    @NotBlank(message = "会话ID不能为空")
    private String conversationUid;

    /**
     * 执行场景
     */
    @NotNull(message = "执行场景不能为空")
    private AppSceneEnum scene;

    /**
     * AI模型返回的条数
     */
    @Schema(description = "AI模型返回的条数")
    private Integer n = 1;

    /**
     * 当前执行的步骤
     */
    private String stepId;

    /**
     * 是否连续执行
     */
    private Boolean continuous;

    /**
     * 当前执行人，权益扣除的用户。
     */
    private Long userId;

    /**
     * 游客用户
     */
    private String endUser;

    /**
     * 渠道媒介 UID
     */
    private String mediumUid;

    /**
     * App 实体
     */
    @NotNull
    private AppEntity app;

    /**
     * 流程执行入口新入参，有数据和参数定义
     * 对之前的大对象 AppEntity 进行精简处理
     */
    private JsonData jsonData;

    /**
     * SSE
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private SseEmitter sseEmitter;

    public Long getEndUserId() {
        return StrUtil.isNotBlank(this.endUser) ? Long.valueOf(this.endUser) : null;
    }

    /**
     * 构造函数, 用于创建新的会话
     *
     * @param app   App 实体
     * @param scene 执行场景
     */
    @SuppressWarnings("all")
    public AppContext(AppEntity app, AppSceneEnum scene) {
        if (app == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_EXECUTE_APP_IS_NULL);
        }
        this.conversationUid = IdUtil.simpleUUID();

        this.app = app;
        this.uid = app.getUid();
        this.scene = scene;
        this.stepId = app.getWorkflowConfig().getFirstStepWrapper().getStepCode();
        this.userId = WebFrameworkUtils.getLoginUserId();
    }

    /**
     * 根据 stepId 获取 actionResponse
     *
     * @return 根据 stepId 获取 step
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public ActionResponse getStepResponse(String stepId) {

        return this.getStepWrapper(stepId).getFlowStep().getResponse();
    }


    /**
     * 根据当前stepWrapper
     *
     * @return 根据 stepId 获取 step
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapper getStepWrapper() {

        // 获取应用配置信息
        WorkflowConfigEntity config = Optional.ofNullable(this.app)
                .map(AppEntity::getWorkflowConfig)
                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_CONFIG_REQUIRED));
        return config.getStepWrapper(this.getStepId());
    }

    /**
     * 根据 stepId 获取 stepWrapper
     *
     * @return 根据 stepId 获取 step
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapper getStepWrapper(String stepId) {
        // 校验 stepId 是否存在
        AppValidate.notBlank(stepId, ErrorCodeConstants.EXECUTE_APP_STEP_ID_REQUIRED);
        // 获取应用配置信息
        WorkflowConfigEntity config = Optional.ofNullable(this.app)
                .map(AppEntity::getWorkflowConfig)
                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_CONFIG_REQUIRED));
        return config.getStepWrapper(stepId);
    }


    /**
     * 根据 actionHandler 获取 stepWrapper
     *
     * @return 根据 stepId 获取 step
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapper getStepWrapper(Class<? extends BaseActionHandler> classz) {

        // 获取应用配置信息
        WorkflowConfigEntity config = Optional.ofNullable(this.app)
                .map(AppEntity::getWorkflowConfig)
                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_CONFIG_REQUIRED));

        return config.getStepWrapper(classz);
    }


    /**
     * 获取当前步骤的指定变量值
     *
     * @param key   字段名
     * @param parse 是否解析变量占位符
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getContextVariablesValue(String key, Boolean parse) {

        return this.getContextVariablesValues(this.getStepId(), parse).getOrDefault(key, null);
    }


    /**
     * 获取当前步骤的所有变量值 Maps
     *
     * @return 当前步骤的所有变量值 Maps
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Map<String, Object> getContextVariablesValues() {

        return this.getContextVariablesValues(this.getStepId());
    }

    /**
     * 获取当前步骤的所有变量值 Maps
     *
     * @return 当前步骤的所有变量值 Maps
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Map<String, Object> getContextVariablesValues(String stepId) {

        return this.getContextVariablesValues(stepId, true);
    }

    /**
     * 获取当前步骤的所有变量值 Maps
     *
     * @return 当前步骤的所有变量值 Maps
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Map<String, Object> getContextVariablesValues(String stepId, Boolean parse) {
        Lock lock = redissonClient.getLock("get-context-variables-" + this.getApp().getUid() + stepId);
        try {
            if (!lock.tryLock(1, TimeUnit.MINUTES)) {
                log.warn("{} 正在获取变量中", stepId);
                return Collections.emptyMap();
            }
            Map<String, Object> allVariablesValues = this.getAllVariablesValues(stepId);

            //当前步骤的所有变量 kv，不加前缀
            WorkflowStepWrapper wrapper = this.getStepWrapper(stepId);
            Map<String, Object> variables = wrapper.getContextVariablesValues(null, false);

            if (parse) {
                variables = parseMapFromVariablesValues(variables, allVariablesValues);
            }

            return variables;
        } catch (InterruptedException exception) {
            log.error("获取变量失败", exception);
            return Collections.emptyMap();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取当前步骤的所有变量值 Maps
     *
     * @return 当前步骤的所有变量值 Maps
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Map<String, Object> getContextVariablesValues(Class<? extends BaseActionHandler> classz) {

        WorkflowStepWrapper posterWrapper = this.getStepWrapper(classz);
        String setpCode = posterWrapper.getStepCode();
        final Map<String, Object> posterParams = this.getContextVariablesValues(setpCode);
        return posterParams;
    }

    /**
     * 获取当前步骤的所有变量值 Maps
     *
     * @return 当前步骤的所有变量值 Maps
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Map<String, Object> getContextVariablesValues(Class<? extends BaseActionHandler> classz, Boolean parse) {

        WorkflowStepWrapper posterWrapper = this.getStepWrapper(classz);
        String setpCode = posterWrapper.getStepCode();
        final Map<String, Object> posterParams = this.getContextVariablesValues(setpCode, parse);
        return posterParams;
    }


    /**
     * 获取当前步骤的所有变量值 Maps
     *
     * @return 当前步骤的所有变量值 Maps
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Map<String, Object> parseMapFromVariables(Map<String, Object> values, String stepId) {

        Map<String, Object> allVariablesValues = this.getAllVariablesValues(stepId);

        Map<String, Object> fieldVariables = parseMapFromVariablesValues(values, allVariablesValues);

        return fieldVariables;
    }


    /**
     * 获取步骤的变量内容
     *
     * @param stepId
     */
    private Map<String, Object> getAllVariablesValues(String stepId) {

        // 获取当前步骤前的所有步骤
        List<WorkflowStepWrapper> workflowStepWrappers = this.app.getWorkflowConfig().getPreStepWrappers(stepId);

        Map<String, Object> allVariablesValues = MapUtil.newHashMap();

        //获取所有节点变量的值 kv
        Optional.ofNullable(workflowStepWrappers).orElse(new ArrayList<>()).forEach(wrapper -> {

            Map<String, Object> variablesValues = wrapper.getContextVariablesValues(STEP_PREFIX);
            allVariablesValues.putAll(Optional.ofNullable(variablesValues).orElse(MapUtil.newHashMap()));

            //新版本的变量
            Map<String, Object> variablesValuesV2 = wrapper.getContextVariablesValues(null, true);
            allVariablesValues.putAll(Optional.ofNullable(variablesValuesV2).orElse(MapUtil.newHashMap()));

            //再生成无前缀的占位符表示当前节点变量（可以引用自己节点的变量）
            if (wrapper.getStepCode().equalsIgnoreCase(stepId) || wrapper.getName().equalsIgnoreCase(stepId)) {
                Map<String, Object> variablesValuesV3 = wrapper.getContextVariablesValues(null, false);
                allVariablesValues.putAll(Optional.ofNullable(variablesValuesV3).orElse(MapUtil.newHashMap()));
            }

        });

        return allVariablesValues;
    }

    /**
     * 解析传入的value，替换其中的变量占位符
     */
    public static Map<String, Object> parseMapFromVariablesValues(Map<String, Object> values, Map<String, Object> allVariablesValues) {

        return parseMapFromVariablesValues(values, allVariablesValues, true);
    }

    /**
     * 解析传入的value，替换其中的变量占位符
     */
    public static Map<String, Object> parseMapFromVariablesValues(Map<String, Object> values, Map<String, Object> allVariablesValues, Boolean defEmpty) {

        Map<String, Object> fieldVariables = new HashMap<>();
        //遍历当前变量
        Optional.ofNullable(values.entrySet()).orElse(new HashSet<>()).forEach(entrySet -> {

            String filedKey = entrySet.getKey();
            Object value = entrySet.getValue();
            if (value != null) {

                String val = String.valueOf(value);
                value = QLExpressUtils.execute(val, allVariablesValues, defEmpty);

                if (value instanceof String) {
                    //处理老的变量占位符 {}
                    value = StrUtil.format(String.valueOf(value), allVariablesValues);
                }
            }
            fieldVariables.put(filedKey, value);
        });

        return fieldVariables;
    }

    /**
     * 执行成功后，响应更新
     *
     * @param response 响应
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void setActionResponse(String stepId, ActionResponse response) {
        this.app.setActionResponse(stepId, response);
    }

    /**
     * 执行成功后，响应更新
     *
     * @param response 响应
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void setActionResponse(ActionResponse response) {
        this.app.setActionResponse(this.stepId, response);
    }

    /**
     * 放入变量
     *
     * @param key   key
     * @param value value
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(String key, Object value) {
        this.app.putVariable(this.stepId, key, value);
    }

    /**
     * 放入变量
     *
     * @param key   key
     * @param value value
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putModelVariable(String key, Object value) {
        this.app.putModelVariable(this.stepId, key, value);
    }

    /**
     * 放入变量
     *
     * @param key   key
     * @param value value
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariableForce(String key, Object value) {
        this.app.addVariable(this.stepId, key, value);
    }
}
