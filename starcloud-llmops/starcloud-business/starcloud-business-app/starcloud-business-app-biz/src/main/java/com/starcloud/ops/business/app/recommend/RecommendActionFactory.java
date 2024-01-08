package com.starcloud.ops.business.app.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.action.ActionResponseRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.domain.entity.workflow.action.AssembleActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ContentActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.OpenAIChatActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ParagraphActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.AppStepTypeEnum;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.MessageUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 推荐应用Action工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendActionFactory {

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
        return defOpenAiChatCompletionStep(defaultPrompt, isShow, RecommendResponseFactory.defTextResponse());
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
        step.setHandler(OpenAIChatActionHandler.class.getSimpleName());
        step.setResponse(response);
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("open-ai");
        step.setTags(Arrays.asList("Open AI", "Completion", "Chat"));
        step.setScenes(scenes);
        step.setVariable(RecommendVariableFactory.defOpenAiVariable(defaultPrompt, isShow));
        return step;
    }

    /**
     * 默认生成内容步骤
     *
     * @param defaultPrompt 默认提示
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defContentActionStep(String defaultPrompt) {
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName(MessageUtil.getMessage("CONTENT_ACTION_NAME"));
        step.setDescription(MessageUtil.getMessage("CONTENT_ACTION_DESCRIPTION"));
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(ContentActionHandler.class.getSimpleName());
        step.setResponse(RecommendResponseFactory.defTextResponse());
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("open-ai");
        step.setTags(Collections.singletonList("Content"));
        step.setScenes(AppUtils.DEFAULT_SCENES);
        step.setVariable(RecommendVariableFactory.defOpenAiVariable(defaultPrompt, Boolean.FALSE));
        return step;
    }

    /**
     * 默认生成内容步骤
     *
     * @param defaultPrompt 默认提示
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defParagraphActionStep(String defaultPrompt) {
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName(MessageUtil.getMessage("PARAGRAPH_ACTION_NAME"));
        step.setDescription(MessageUtil.getMessage("PARAGRAPH_ACTION_DESCRIPTION"));
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(ParagraphActionHandler.class.getSimpleName());
        step.setResponse(RecommendResponseFactory.defTextResponse());
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("open-ai");
        step.setTags(Collections.singletonList("Paragraph"));
        step.setScenes(AppUtils.DEFAULT_SCENES);
        step.setVariable(RecommendVariableFactory.defOpenAiVariable(defaultPrompt, Boolean.FALSE));
        return step;
    }

    /**
     * 默认生成内容步骤
     *
     * @param defaultPrompt 默认提示
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defAssembleActionStep(String defaultPrompt) {
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName(MessageUtil.getMessage("ASSEMBLE_ACTION_NAME"));
        step.setDescription(MessageUtil.getMessage("ASSEMBLE_ACTION_DESCRIPTION"));
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(AssembleActionHandler.class.getSimpleName());
        step.setResponse(RecommendResponseFactory.defTextResponse());
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("open-ai");
        step.setTags(Collections.singletonList("Assemble"));
        step.setScenes(AppUtils.DEFAULT_SCENES);
        step.setVariable(RecommendVariableFactory.defOpenAiVariable(defaultPrompt, Boolean.FALSE));
        return step;
    }

    /**
     * 默认生成内容步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defPosterActionStep() {
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName(MessageUtil.getMessage("POSTER_ACTION_NAME"));
        step.setDescription(MessageUtil.getMessage("POSTER_ACTION_DESCRIPTION"));
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(PosterActionHandler.class.getSimpleName());
        step.setResponse(RecommendResponseFactory.defTextResponse());
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("poster");
        step.setTags(Collections.singletonList("Poster"));
        step.setScenes(AppUtils.DEFAULT_SCENES);
        step.setVariable(RecommendVariableFactory.defEmptyVariable());
        return step;
    }


}
