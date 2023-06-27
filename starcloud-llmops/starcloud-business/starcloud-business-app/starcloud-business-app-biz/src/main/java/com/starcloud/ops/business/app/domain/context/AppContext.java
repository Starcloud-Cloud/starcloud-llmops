package com.starcloud.ops.business.app.domain.context;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

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

    public AppContext(AppEntity app, AppSceneEnum scene) {
        this.conversationId = IdUtil.simpleUUID();
        this.app = app;
        this.scene = scene;
        this.stepId = app.getWorkflowConfig().getFirstStep().getField();
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

}
