package com.starcloud.ops.business.app.domain.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.util.MessageUtil;

/**
 * 推荐应用Action 包装类工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendedStepWrapperFactory {

    /**
     * Open AI Chat Completion 默认步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defGenerateTextStepWrapper() {
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField("GENERATE_TEXT");
        stepWrapper.setName(MessageUtil.getMessage("WORKFLOW_STEP_GENERATE_TEXT_NAME"));
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_GENERATE_TEXT_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_GENERATE_TEXT_NAME"));
        stepWrapper.setFlowStep(RecommendedActionFactory.defDefaultOpenAiChatStep("Hi.", Boolean.TRUE));
        stepWrapper.setVariable(null);
        return stepWrapper;
    }

}
