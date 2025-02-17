package com.starcloud.ops.business.app.recommend;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.excel.util.StringUtils;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.MessageUtil;
import com.starcloud.ops.framework.common.api.util.StringUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推荐应用Action 包装类工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendStepWrapperFactory {

    private static AppDictionaryService appDictionaryService = SpringUtil.getBean(AppDictionaryService.class);

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
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Collections.emptyList());
        stepWrapper.setVariable(variable);
        return stepWrapper;
    }

    /**
     * xhs + ocr
     *
     * @return
     */
    public static WorkflowStepWrapperRespVO defXhsOcrStepWrapper() {
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField("小红书ocr");
        stepWrapper.setName("小红书ocr");
        stepWrapper.setDescription("小红书ocr");
        stepWrapper.setButtonLabel("小红书ocr");
        stepWrapper.setFlowStep(RecommendActionFactory.defXhsOcrStep());
        stepWrapper.setVariable(RecommendVariableFactory.defXhsOcrVariable());
        return stepWrapper;
    }

    /**
     * 图片ocr
     *
     * @return
     */
    public static WorkflowStepWrapperRespVO defImageOcrStepWrapper() {
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField("图片ocr");
        stepWrapper.setName("图片ocr");
        stepWrapper.setDescription("图片ocr");
        stepWrapper.setButtonLabel("图片ocr");
        stepWrapper.setFlowStep(RecommendActionFactory.defImageOcrStep());
        stepWrapper.setVariable(RecommendVariableFactory.defImageOcrVariable());
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
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Collections.emptyList());
        stepWrapper.setVariable(variable);
        return stepWrapper;
    }

    /**
     * 变量步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defGlobalVariableStepWrapper() {
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        String name = MessageUtil.getMessage("WORKFLOW_STEP_GLOBAL_VARIABLE_NAME");
        String field = AppUtils.obtainField(name);
        stepWrapper.setField(name);
        stepWrapper.setName(field);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_GLOBAL_VARIABLE_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_GLOBAL_VARIABLE_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defGlobalVariableActionStep());
        stepWrapper.setVariable(RecommendVariableFactory.defGlobalVariableVariable());
        return stepWrapper;
    }

    /**
     * 资料库生成步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defMaterialStepWrapper() {
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        String name = "素材库字段设置";
        String field = AppUtils.obtainField(name);
        stepWrapper.setField(name);
        stepWrapper.setName(field);
        stepWrapper.setDescription("素材库字段设置，应用中可以存在一个此步骤。");
        stepWrapper.setButtonLabel("素材库字段设置");
        stepWrapper.setFlowStep(RecommendActionFactory.defMaterialActionStep());
        stepWrapper.setVariable(RecommendVariableFactory.defMaterialVariable());
        return stepWrapper;
    }

    /**
     * 标题生成步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defTitleStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_TITLE_NAME");
        String field = AppUtils.obtainField(name);
        String defaultPrompt = "";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_TITLE_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_TITLE_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defTitleActionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defTitleVariable());
        return stepWrapper;
    }

    /**
     * 自定义生成步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defCustomStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_CUSTOM_NAME");
        String field = AppUtils.obtainField(name);
        String prompt = "";//"{{" + CreativeConstants.DEFAULT_CONTENT_STEP_PROMPT + "}}";
        String defaultPrompt = StringUtil.isBlank(prompt) ? "" : prompt;
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription(StringUtils.EMPTY);
        //stepWrapper.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_CUSTOM_DESCRIPTION"));
        stepWrapper.setButtonLabel(MessageUtil.getMessage("WORKFLOW_STEP_CUSTOM_BUTTON_LABEL"));
        stepWrapper.setFlowStep(RecommendActionFactory.defCustomActionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defCustomVariable());
        return stepWrapper;
    }

    /**
     * 仿写步骤
     *
     * @return
     */
    public static WorkflowStepWrapperRespVO defImitateStepWrapper() {
        String name = "笔记仿写";
        String field = AppUtils.obtainField(name);
        String defaultPrompt = "";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription("仿写笔记标题和内容");
        stepWrapper.setButtonLabel("笔记仿写");
        stepWrapper.setFlowStep(RecommendActionFactory.defImitateActionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defImitateVariable());
        return stepWrapper;
    }

    /**
     * 段落生成步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defParagraphStepWrapper() {
        String name = MessageUtil.getMessage("WORKFLOW_STEP_PARAGRAPH_NAME");
        String field = AppUtils.obtainField(name);
        String defaultPrompt = "";
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
     * 内容拼接步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defAssembleStepWrapper() {
        String name = "笔记描述配置";
        String field = AppUtils.obtainField(name);
        String defaultPrompt = "";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription("笔记描述配置，也可灵活引用上游生成的内容进行组合生成。");
        stepWrapper.setButtonLabel("笔记描述配置");
        stepWrapper.setFlowStep(RecommendActionFactory.defAssembleActionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defAssembleVariable());
        return stepWrapper;
    }

    /**
     * 海报生成步骤
     *
     * @return WorkflowStepRespVO
     */
    public static WorkflowStepWrapperRespVO defPosterStepWrapper() {
        String name = "模版配置";
        String field = AppUtils.obtainField(name);
        String defaultPrompt = "为图片配上一个符合图片场景和意境的标题和副标题，标题在30到50个字内。\n {STEP." + field + ".REQUIREMENT}\n\n 输出格式：\n```\n标题:\n副标题:\n```";
        WorkflowStepWrapperRespVO stepWrapper = new WorkflowStepWrapperRespVO();
        stepWrapper.setField(field);
        stepWrapper.setName(name);
        stepWrapper.setDescription("模版配置。应用中可以存在一个此步骤。");
        stepWrapper.setButtonLabel("模版配置");
        stepWrapper.setFlowStep(RecommendActionFactory.defPosterActionStep(defaultPrompt));
        stepWrapper.setVariable(RecommendVariableFactory.defPosterVariable());
        return stepWrapper;
    }

    /**
     * 通用步骤
     *
     * @return 通用步骤
     */
    public static List<WorkflowStepWrapperRespVO> defCommonStepWrapperList() {
        return Arrays.asList(
                defDefaultTextCompletionStepWrapper(),
                defXhsOcrStepWrapper(),
                defImageOcrStepWrapper()
        );
    }

    /**
     * 媒体矩阵步骤
     *
     * @return 媒体矩阵步骤
     */
    public static List<WorkflowStepWrapperRespVO> defMediaMatrixStepWrapperList() {
        return Arrays.asList(
                defGlobalVariableStepWrapper(),
                defMaterialStepWrapper(),
                defCustomStepWrapper(),
                defAssembleStepWrapper(),
                defPosterStepWrapper()
        );
    }

    /**
     * 步骤默认变量
     *
     * @return
     */
    public static Map<String, WorkflowStepWrapperRespVO> getStepVariable() {
        Map<String, WorkflowStepWrapperRespVO> result = new HashMap<>();
        for (WorkflowStepWrapperRespVO workflowStepWrapperRespVO : defMediaMatrixStepWrapperList()) {
            result.put(workflowStepWrapperRespVO.getHandler(), workflowStepWrapperRespVO);
        }
        return result;
    }

    /**
     * 从字典中获取默认值
     *
     * @param key key
     * @return 默认值
     */
    private static String getDefaultFromDict(String key) {
        return MapUtil.emptyIfNull(appDictionaryService.defaultAppConfiguration()).getOrDefault(key, StrUtil.EMPTY);
    }

}
