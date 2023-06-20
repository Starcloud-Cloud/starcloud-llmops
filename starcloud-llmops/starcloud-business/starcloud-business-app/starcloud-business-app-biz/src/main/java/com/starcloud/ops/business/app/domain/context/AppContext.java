package com.starcloud.ops.business.app.domain.context;

import cn.hutool.core.lang.Assert;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

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
@Validated
public class AppContext {


    private String conversationId;

    private String stepId;

    @NotNull
    private String scene;

    private String endUser;

    /**
     * App 实体， 由 TemplateDTO 转换而来
     */
    @NotNull
    private AppEntity app;

    private HttpServletResponse httpServletResponse;

    public AppContext(AppEntity app) {
        this.app = app;
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
    public WorkflowStepWrapper getCurrentAppStepWrapper() {

        Assert.notBlank(this.getStepId(), "AppContext stepId is not blank");

        WorkflowStepWrapper stepWrapper = this.app.getWorkflowConfig().getStep(this.getStepId());

        return stepWrapper;
    }

    /**
     * 获取当前执行的step
     *
     * @return
     */
    public WorkflowStepWrapper getCurrentAppStepWrapper(String stepId) {

        Assert.notBlank(stepId, "AppContext stepId is not blank");

        WorkflowStepWrapper stepWrapper = this.app.getWorkflowConfig().getStep(this.getStepId());

        return stepWrapper;
    }

}
