package com.starcloud.ops.business.app.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.action.ActionResponseRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.xhs.material.XhsNoteDTO;
import com.starcloud.ops.business.app.domain.entity.workflow.action.AssembleActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.CustomActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ImageOcrActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ImitateActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.OpenAIChatActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ParagraphActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.TitleActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.VariableActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.XhsParseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.VariableDefInterface;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.AppStepTypeEnum;
import com.starcloud.ops.business.app.model.content.ImageContent;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
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

    public static WorkflowStepRespVO defXhsOcrStep() {
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName("小红书ocr");
        step.setDescription("小红书ocr");
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(XhsParseActionHandler.class.getSimpleName());
        String jsonSchema = JsonSchemaUtils.generateJsonSchemaStr(XhsNoteDTO.class);
        step.setResponse(RecommendResponseFactory.defJsonResponse(Boolean.TRUE, Boolean.TRUE, jsonSchema));
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("xhs-ocr");
        step.setTags(Arrays.asList("xhs", "ocr"));
        step.setScenes(AppUtils.DEFAULT_SCENES);
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Collections.emptyList());
        step.setVariable(variable);
        return step;
    }

    /**
     * 图片ocr
     *
     * @return
     */
    public static WorkflowStepRespVO defImageOcrStep() {

        VariableDefInterface handler = (VariableDefInterface) BaseActionHandler.of(ImageOcrActionHandler.class.getSimpleName());

        assert handler != null;
        WorkflowStepRespVO step = handler.defWorkflowStepResp();

        return step;
    }

    /**
     * 默认生成内容步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defGlobalVariableActionStep() {
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName(MessageUtil.getMessage("WORKFLOW_STEP_GLOBAL_VARIABLE_NAME"));
        step.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_GLOBAL_VARIABLE_DESCRIPTION"));
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(VariableActionHandler.class.getSimpleName());
        step.setResponse(RecommendResponseFactory.defTextResponse(Boolean.FALSE));
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("variable");
        step.setTags(Collections.singletonList("Variable"));
        step.setScenes(AppUtils.DEFAULT_SCENES);
        step.setVariable(RecommendVariableFactory.defGlobalVariableVariable());
        return step;
    }

    /**
     * 默认生成内容步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defMaterialActionStep() {
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName(MessageUtil.getMessage("WORKFLOW_STEP_MATERIAL_NAME"));
        step.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_MATERIAL_DESCRIPTION"));
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(MaterialActionHandler.class.getSimpleName());
        step.setResponse(RecommendResponseFactory.defTextResponse(Boolean.FALSE));
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("material");
        step.setTags(Collections.singletonList("Material"));
        step.setScenes(AppUtils.DEFAULT_SCENES);
        step.setVariable(RecommendVariableFactory.defGlobalVariableVariable());
        return step;
    }

    /**
     * 默认生成内容步骤
     *
     * @param defaultPrompt 默认提示
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defTitleActionStep(String defaultPrompt) {
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName(MessageUtil.getMessage("WORKFLOW_STEP_TITLE_NAME"));
        step.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_TITLE_DESCRIPTION"));
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(TitleActionHandler.class.getSimpleName());
        step.setResponse(RecommendResponseFactory.defTextResponse());
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("title");
        step.setTags(Collections.singletonList("Title"));
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
    public static WorkflowStepRespVO defCustomActionStep(String defaultPrompt) {
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName(MessageUtil.getMessage("WORKFLOW_STEP_CUSTOM_NAME"));
        step.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_CUSTOM_DESCRIPTION"));
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(CustomActionHandler.class.getSimpleName());
        step.setResponse(RecommendResponseFactory.defTextResponse());
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("content");
        step.setTags(Collections.singletonList("Custom"));
        step.setScenes(AppUtils.DEFAULT_SCENES);
        step.setVariable(RecommendVariableFactory.defCustomVariable(defaultPrompt, Boolean.FALSE));
        return step;
    }

    public static WorkflowStepRespVO defImitateActionStep(String defaultPrompt) {
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName("笔记仿写");
        step.setDescription("仿写笔记标题和内容");
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(ImitateActionHandler.class.getSimpleName());
        step.setResponse(RecommendResponseFactory.defTextResponse());
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("content");
        step.setTags(Collections.singletonList("Imitate"));
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
        step.setName(MessageUtil.getMessage("WORKFLOW_STEP_PARAGRAPH_NAME"));
        step.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_PARAGRAPH_DESCRIPTION"));
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(ParagraphActionHandler.class.getSimpleName());
        step.setResponse(RecommendResponseFactory.defTextResponse());
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("paragraph");
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
        // 固定的 jsonSchema，不可编辑。
        String jsonSchema = JsonSchemaUtils.generateCopyWritingJsonSchemaStr();
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName(MessageUtil.getMessage("WORKFLOW_STEP_ASSEMBLE_NAME"));
        step.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_ASSEMBLE_DESCRIPTION"));
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(AssembleActionHandler.class.getSimpleName());
        step.setResponse(RecommendResponseFactory.defReadOnlyResponse(jsonSchema));
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("assemble");
        step.setTags(Collections.singletonList("Assemble"));
        step.setScenes(AppUtils.DEFAULT_SCENES);
        step.setVariable(RecommendVariableFactory.defGlobalVariableVariable());
        return step;
    }

    /**
     * 默认生成内容步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepRespVO defPosterActionStep(String defaultPrompt) {
        String jsonSchema = JsonSchemaUtils.generateJsonSchemaStr(ImageContent.class);
        WorkflowStepRespVO step = new WorkflowStepRespVO();
        step.setName(MessageUtil.getMessage("WORKFLOW_STEP_POSTER_NAME"));
        step.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_POSTER_DESCRIPTION"));
        step.setType(AppStepTypeEnum.WORKFLOW.name());
        step.setHandler(PosterActionHandler.class.getSimpleName());
        step.setResponse(RecommendResponseFactory.defReadOnlyResponse(jsonSchema));
        step.setIsAuto(Boolean.TRUE);
        step.setIsCanEditStep(Boolean.TRUE);
        step.setVersion(AppConstants.DEFAULT_VERSION);
        step.setIcon("poster");
        step.setTags(Collections.singletonList("Poster"));
        step.setScenes(AppUtils.DEFAULT_SCENES);
        step.setVariable(RecommendVariableFactory.defPosterStepVariable(defaultPrompt, Boolean.FALSE));
        return step;
    }

}
