package com.starcloud.ops.business.app.domain.entity.workflow.context;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.poster.PosterStepEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.validate.AppValidate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * App 上下文
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppContext {

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
     * AI模型
     */
    @Schema(description = "AI模型")
    private String aiModel;

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
     * 获取当前步骤的所有变量值 Maps
     *
     * @return 当前步骤的所有变量值 Maps
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Map<String, Object> getContextVariablesValues() {

        // 获取当前步骤前的所有变量的值
        List<WorkflowStepWrapper> workflowStepWrappers = this.app.getWorkflowConfig().getPreStepWrappers(this.stepId);

        Map<String, Object> allVariablesValues = MapUtil.newHashMap();

        Optional.ofNullable(workflowStepWrappers).orElse(new ArrayList<>()).forEach(wrapper -> {

            Map<String, Object> variablesValues = wrapper.getContextVariablesValues(STEP_PREFIX);

            allVariablesValues.putAll(Optional.ofNullable(variablesValues).orElse(MapUtil.newHashMap()));
        });

        WorkflowStepWrapper wrapper = this.getStepWrapper(this.stepId);
        //当前步骤的所有变量
        Map<String, Object> variables = wrapper.getContextVariablesValues(null);

        Map<String, Object> fieldVariables = new HashMap<>();
        Optional.ofNullable(variables.entrySet()).orElse(new HashSet<>()).forEach(entrySet -> {

            String filedKey = StrUtil.replace(entrySet.getKey(), this.stepId + ".", "");
            filedKey = StrUtil.replace(filedKey, this.stepId, "");

            //做一次字符串替换， {}会被替换掉
            String val = StrUtil.format(String.valueOf(entrySet.getValue()), allVariablesValues);

            //做一次spel，spel 语法会被替换掉

            //allVariablesValues 转换为 #root

            fieldVariables.put(filedKey, val);

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
    public void setActionResponse(ActionResponse response) {
        this.app.setActionResponse(this.stepId, response);
    }

}
