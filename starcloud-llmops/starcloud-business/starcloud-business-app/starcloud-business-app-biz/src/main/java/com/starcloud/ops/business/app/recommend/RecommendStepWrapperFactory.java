package com.starcloud.ops.business.app.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.MessageUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 推荐应用Action 包装类工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendStepWrapperFactory {

    /**
     * 默认生成文本步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defDefaultTextCompletionStepWrapper() {
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(AppUtils.obtainField(MessageUtil.getMessage("WORKFLOW_STEP_GENERATE_TEXT_NAME")));
        stepWrapper.setName(MessageUtil.getMessage("WORKFLOW_STEP_GENERATE_TEXT_NAME"));
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_GENERATE_TEXT_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_GENERATE_TEXT_NAME"));
        stepWrapper.setFlowStep(RecommendActionFactory.defOpenAiChatCompletionStep());
        stepWrapper.setVariable(null);
        return stepWrapper;
    }

    /**
     * 默认生成文章标题步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defArticleTitleStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_TITLE_NAME");
        String field = AppUtils.obtainField(name);
        String defaultPrompt = "Write a title for an article about \"{STEP." + field + ".TOPIC}\" in {STEP." + field + ".LANGUAGE}. Style: {STEP." + field + ".WRITING_STYLE}. Tone: {STEP." + field + ".WRITING_TONE}. Must be between 40 and 60 characters";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_TITLE_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_TITLE_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defOpenAiChatCompletionStep(defaultPrompt, Boolean.FALSE, RecommendResponseFactory.defInputResponse()));
        stepWrapper.setVariable(RecommendVariableFactory.defArticleGlobalVariable());
        return stepWrapper;
    }

    /**
     * 默认生成文章描述步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defArticleSectionsStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_SECTIONS_NAME");
        String field = AppUtils.obtainField(name);
        String titleField = AppUtils.obtainField(MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_TITLE_NAME"));
        String defaultPrompt = "Write {STEP." + field + ".SECTIONS} consecutive headings for an article about \"{STEP." + titleField + "._OUT}\", in {STEP." + titleField + ".LANGUAGE}. Style: {STEP." + titleField + ".WRITING_STYLE}. Tone: {STEP." + titleField + ".WRITING_TONE}. Each heading is between 40 and 60 characters. Use Markdown for the headings (## ).";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_SECTIONS_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_SECTIONS_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defOpenAiChatCompletionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defArticleSectionsVariable());
        return stepWrapper;
    }

    /**
     * 默认生成文章内容步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defArticleContentStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_CONTENT_NAME");
        String field = AppUtils.obtainField(name);
        String titleField = AppUtils.obtainField(MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_TITLE_NAME"));
        String selectionField = AppUtils.obtainField(MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_SECTIONS_NAME"));
        String defaultPrompt = "Write an article about \"{STEP." + titleField + "._OUT}\" in {STEP." + titleField + ".LANGUAGE}. The article is organized by the following headings:\n{STEP." + selectionField + "._OUT}\nWrite {STEP." + field + "._IN.PARAGRAPHS} paragraphs per heading.\nUse Markdown for formatting.\nAdd an introduction prefixed by \"<!--- ===INTRO: --->\", and a conclusion prefixed by \"<!--- ===OUTRO: --->\".\nStyle: {STEP." + titleField + ".WRITING_STYLE}. Tone: {STEP." + titleField + ".WRITING_TONE}.";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_CONTENT_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_CONTENT_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defOpenAiChatCompletionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defArticleParagraphs());
        return stepWrapper;
    }

    /**
     * 默认生成文章摘要步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defArticleExcerptStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_EXCERPT_NAME");
        String field = AppUtils.obtainField(name);
        String titleField = AppUtils.obtainField(MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_TITLE_NAME"));
        String defaultPrompt = "Write an excerpt for an article about \"{STEP." + titleField + "._OUT}\" in {STEP." + titleField + ".LANGUAGE}. Style: {STEP." + titleField + ".WRITING_STYLE}. Tone: {STEP." + titleField + ".WRITING_TONE}. Must be between 40 and 60 characters.";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_EXCERPT_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_ARTICLE_EXCERPT_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defOpenAiChatCompletionStep(defaultPrompt));
        stepWrapper.setVariable(null);
        return stepWrapper;
    }

    /**
     * 默认生成文章摘要步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defMediaMatrixTitleStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_TITLE_NAME");
        String field = AppUtils.obtainField(name);
        String titleField = AppUtils.obtainField(MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_TITLE_NAME"));
        String defaultPrompt = "媒体矩阵标题";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_TITLE_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_TITLE_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defOpenAiChatCompletionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defMediaMatrixTitleVariable());
        return stepWrapper;
    }

    /**
     * 默认生成文章摘要步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defMediaMatrixStartStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_START_NAME");
        String field = AppUtils.obtainField(name);
        String titleField = AppUtils.obtainField(MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_START_NAME"));
        String defaultPrompt = "媒体矩阵开头";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_START_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_START_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defOpenAiChatCompletionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defMediaMatrixStartVariable());
        return stepWrapper;
    }

    /**
     * 默认生成文章摘要步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defMediaMatrixContentStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_CONTENT_NAME");
        String field = AppUtils.obtainField(name);
        String titleField = AppUtils.obtainField(MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_CONTENT_NAME"));
        String defaultPrompt = "媒体矩阵内容";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_CONTENT_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_CONTENT_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defOpenAiChatCompletionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defMediaMatrixContentVariable());
        return stepWrapper;
    }

    /**
     * 默认生成文章摘要步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defMediaMatrixEndStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_END_NAME");
        String field = AppUtils.obtainField(name);
        String titleField = AppUtils.obtainField(MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_END_NAME"));
        String defaultPrompt = "媒体矩阵结尾";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_END_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_MEDIA_MATRIX_END_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defOpenAiChatCompletionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defMediaMatrixEndVariable());
        return stepWrapper;
    }

    /**
     * 通用步骤
     *
     * @return 通用步骤
     */
    public static List<WorkflowStepWrapperRespVO> defCommonStepWrapperList() {
        return Collections.singletonList(defDefaultTextCompletionStepWrapper());
    }

    /**
     * 媒体矩阵步骤
     *
     * @return 媒体矩阵步骤
     */
    public static List<WorkflowStepWrapperRespVO> defMediaMatrixStepWrapperList() {
        return Arrays.asList(
                defDefaultTextCompletionStepWrapper(),
                defMediaMatrixTitleStepWrapper(),
                defMediaMatrixStartStepWrapper(),
                defMediaMatrixContentStepWrapper(),
                defMediaMatrixEndStepWrapper()
        );
    }

}
