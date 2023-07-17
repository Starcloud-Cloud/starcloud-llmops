package com.starcloud.ops.business.app.domain.context;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.*;

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

    private String conversationId;

    private String stepId;

    @NotNull
    private AppSceneEnum scene;

    private String user;

    private String endUser;

    /**
     * App 实体， 由 TemplateDTO 转换而来
     */
    @NotNull
    private AppEntity app;


    @JSONField(serialize = false)
    private HttpServletResponse httpServletResponse;


    @JSONField(serialize = false)
    private SseEmitter sseEmitter;

    public AppContext(AppEntity app, AppSceneEnum scene) {
        this.conversationId = IdUtil.simpleUUID();
        this.app = app;
        this.scene = scene;
        this.stepId = app.getWorkflowConfig().getFirstStep().getField();
        this.user = String.valueOf(WebFrameworkUtils.getLoginUserId());
    }


    public Map<String, Object> getContextVariables() {

//        this.app.getConfig().getSteps().forEach(appStepWrapper -> {
//
//            appStepWrapper.get();
//
//        });

        return new HashMap();

    }


    /**
     * 获取当前执行的step
     *
     * @return
     */
    public WorkflowStepWrapper getCurrentStepWrapper() {

        return this.getStepWrapper(this.getStepId());
    }

    /**
     * 获取当前执行的step
     *
     * @return
     */
    public WorkflowStepWrapper getStepWrapper(String stepId) {

        Assert.notBlank(stepId, "AppContext stepId is not blank");

        WorkflowStepWrapper stepWrapper = this.app.getWorkflowConfig().getStepWrapper(stepId);

        return stepWrapper;
    }

    /**
     * 获取当前步骤的变量
     *
     * @return
     */
    @JSONField(serialize = false)
    public String getContextVariablesValue(String field, String def) {

        String val = getContextVariablesValue(field);
        return StrUtil.isNotBlank(val) ? val : def;
    }


    /**
     * 获取当前步骤的变量
     *
     * @return
     */
    @JSONField(serialize = false)
    public String getContextVariablesValue(String field) {


        String prefixKey = "STEP";

        String allKey = this.stepId + "." + field;

        //获取当前步骤前的所有变量的值
        List<WorkflowStepWrapper> workflowStepWrappers = this.app.getWorkflowConfig().getPreStepWrappers(this.stepId);

        Map<String, Object> allVariablesValues = MapUtil.newHashMap();

        Optional.ofNullable(workflowStepWrappers).orElse(new ArrayList<>()).forEach(wrapper -> {

            Map<String, Object> variablesValues = wrapper.getContextVariablesValues(prefixKey);

            Optional.ofNullable(variablesValues).orElse(MapUtil.newHashMap()).entrySet().forEach(stringObjectEntry -> {

                allVariablesValues.put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
            });
        });

        //获取需要替换占位符的字段内容


        WorkflowStepWrapper wrapper = this.getStepWrapper(this.stepId);
        Object value = wrapper.getContextVariablesValue(field);


        //内容中 变量占位符 替换

        return StrUtil.format(String.valueOf(value), allVariablesValues);

    }


    /**
     * 获取当前步骤的所有变量Maps
     *
     * @return
     */
    @JSONField(serialize = false)
    public Map<String, Object> getContextVariablesValues() {

        String prefixKey = "STEP";

        //获取当前步骤前的所有变量的值
        List<WorkflowStepWrapper> workflowStepWrappers = this.app.getWorkflowConfig().getPreStepWrappers(this.stepId);

        Map<String, Object> allVariablesValues = MapUtil.newHashMap();

        Optional.ofNullable(workflowStepWrappers).orElse(new ArrayList<>()).forEach(wrapper -> {

            Map<String, Object> variablesValues = wrapper.getContextVariablesValues(prefixKey);

            Optional.ofNullable(variablesValues).orElse(MapUtil.newHashMap()).entrySet().forEach(stringObjectEntry -> {

                allVariablesValues.put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
            });
        });


        return allVariablesValues;

    }

}
