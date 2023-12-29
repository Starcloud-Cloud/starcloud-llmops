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
     * 默认生成内容步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defContentStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_CONTENT_NAME");
        String field = AppUtils.obtainField(name);
        String titleField = AppUtils.obtainField(MessageUtil.getMessage("WORKFLOW_STEP_CONTENT_NAME"));
        String defaultPrompt = "内容";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_CONTENT_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_CONTENT_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defContentActionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defContentVariable());
        return stepWrapper;
    }

    /**
     * 默认生成文章摘要步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defParagraphStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_PARAGRAPH_NAME");
        String field = AppUtils.obtainField(name);
        String titleField = AppUtils.obtainField(MessageUtil.getMessage("WORKFLOW_STEP_PARAGRAPH_NAME"));
        String defaultPrompt = "内容";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_PARAGRAPH_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_PARAGRAPH_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defParagraphActionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defParagraphVariable());
        return stepWrapper;
    }

    /**
     * 默认生成文章摘要步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defAssembleStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_ASSEMBLE_NAME");
        String field = AppUtils.obtainField(name);
        String titleField = AppUtils.obtainField(MessageUtil.getMessage("WORKFLOW_STEP_ASSEMBLE_NAME"));
        String defaultPrompt = "内容";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_ASSEMBLE_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_ASSEMBLE_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defAssembleActionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defAssembleVariable());
        return stepWrapper;
    }

    /**
     * 默认生成文章摘要步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defPosterStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_POSTER_NAME");
        String field = AppUtils.obtainField(name);
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_POSTER_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_POSTER_BUTTON_LABEL"));
        stepWrapper.setFlowStep(null);
        stepWrapper.setPosterStep(RecommendActionFactory.defPosterActionStep());
        stepWrapper.setVariable(RecommendVariableFactory.defPosterVariable());
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
                defContentStepWrapper(),
                defParagraphStepWrapper(),
                defAssembleStepWrapper(),
                defPosterStepWrapper()
        );
    }

}
