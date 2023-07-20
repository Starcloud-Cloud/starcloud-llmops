package com.starcloud.ops.business.app.domain.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.action.ActionResponseRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.domain.handler.textgeneration.OpenAIChatHandler;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.AppStepTypeEnum;
import com.starcloud.ops.business.app.util.MessageUtil;
import com.starcloud.ops.business.app.util.app.AppUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 推荐应用Action工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendedActionFactory {

    /**
     * 默认生成文本步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defOpenAiChatCompletionStep() {
        return defOpenAiChatCompletionStep("Hi.", Boolean.TRUE);
    }

    /**
     * 默认生成文本步骤
     *
     * @param defaultPrompt 默认提示
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defOpenAiChatCompletionStep(String defaultPrompt) {
        return defOpenAiChatCompletionStep(defaultPrompt, Boolean.FALSE);
    }

    /**
     * 默认生成文本步骤
     *
     * @param defaultPrompt 默认提示
     * @param isShow        是否显示
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defOpenAiChatCompletionStep(String defaultPrompt, Boolean isShow) {
        return defOpenAiChatCompletionStep(defaultPrompt, isShow, RecommendedResponseFactory.defTextResponse());
    }

    /**
     * 默认生成文本步骤
     *
     * @param defaultPrompt 默认提示
     * @param isShow        是否显示
     * @param response      响应
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defOpenAiChatCompletionStep(String defaultPrompt, Boolean isShow, ActionResponseRespVO response) {
        return defOpenAiChatCompletionStep(defaultPrompt, isShow, response, AppUtils.DEFAULT_SCENES);
    }

    /**
     * 默认生成文本步骤
     *
     * @param defaultPrompt 默认提示
     * @param isShow        是否显示
     * @param response      响应
     * @param scenes        场景
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defOpenAiChatCompletionStep(String defaultPrompt,
                                                                 Boolean isShow,
                                                                 ActionResponseRespVO response,
                                                                 List<String> scenes) {
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName(MessageUtil.getMessage("OPEN_AI_CHAT_COMPLETION_NAME"));
        step.setDescription(MessageUtil.getMessage("OPEN_AI_CHAT_COMPLETION_DESCRIPTION"));
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(OpenAIChatHandler.class.getSimpleName());
        step.setResponse(response);
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("open-ai");
        step.setTags(Arrays.asList("Open AI", "Completion", "Chat"));
        step.setScenes(scenes);
        step.setVariable(RecommendedVariableFactory.defOpenAiVariable(defaultPrompt, isShow));
        return step;
    }

}
