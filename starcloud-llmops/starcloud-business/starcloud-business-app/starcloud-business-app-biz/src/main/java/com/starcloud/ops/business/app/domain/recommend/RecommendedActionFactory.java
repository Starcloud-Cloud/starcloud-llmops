package com.starcloud.ops.business.app.domain.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.domain.handler.textgeneration.OpenAIChatActionHandler;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.app.AppStepTypeEnum;
import com.starcloud.ops.business.app.util.MessageUtil;

import java.util.Arrays;

/**
 * 推荐应用Action工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendedActionFactory {

    /**
     * Open AI Chat Completion 默认步骤
     *
     * @param defaultPrompt 默认提示
     * @param isShow        是否显示
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defDefaultOpenAiChatStep(String defaultPrompt, Boolean isShow) {
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName(MessageUtil.getMessage("OPEN_AI_CHAT_COMPLETION_NAME"));
        step.setDescription(MessageUtil.getMessage("OPEN_AI_CHAT_COMPLETION_DESCRIPTION"));
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(OpenAIChatActionHandler.class.getSimpleName());
        step.setResponse(RecommendedResponseFactory.defTextResponse());
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("open-ai");
        step.setTags(Arrays.asList("Open AI", "Completion", "Chat"));
        step.setScenes(Arrays.asList(AppSceneEnum.WEB_ADMIN.name(), AppSceneEnum.WEB_MARKET.name()));
        step.setVariable(RecommendedVariableFactory.defOpenAiVariable(defaultPrompt, isShow));
        return step;
    }
}
