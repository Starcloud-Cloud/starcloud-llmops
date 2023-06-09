package com.starcloud.ops.business.app.domain.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.domain.recommend.enums.WritingStyleEnum;
import com.starcloud.ops.business.app.domain.recommend.enums.WritingToneEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.enums.app.LanguageEnum;
import com.starcloud.ops.business.app.util.MessageUtil;

/**
 * 推荐应用Variable Item 工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendedVariableItemFactory {

    // Open AI Chat Completion Variable Item ---------------------------------------------------------------------------

    /**
     * Open AI Chat Completion 最大token变量
     * Open AI Chat Completion Max Token Variable
     *
     * @return VariableItemRespVO
     */
    public static VariableItemRespVO defMaxTokenVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField("max_tokens");
        variableItem.setLabel(MessageUtil.getMessage("OPEN_AI_MAX_TOKENS_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("OPEN_AI_MAX_TOKENS_DESCRIPTION"));
        variableItem.setDefaultValue(1000);
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.INPUT.name());
        variableItem.setGroup(AppVariableGroupEnum.MODEL.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        return variableItem;
    }

    /**
     * Open AI Chat Completion 采样温度变量
     * Open AI Chat Completion Temperature Variable
     *
     * @return VariableItemRespVO
     */
    public static VariableItemRespVO defTemperatureVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField("temperature");
        variableItem.setLabel(MessageUtil.getMessage("OPEN_AI_TEMPERATURE_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("OPEN_AI_TEMPERATURE_DESCRIPTION"));
        variableItem.setDefaultValue(0.7);
        variableItem.setOrder(2);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.INPUT.name());
        variableItem.setGroup(AppVariableGroupEnum.MODEL.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        return variableItem;
    }

    /**
     * Open AI Chat Completion n变量
     * Open AI Chat Completion n Variable
     *
     * @return VariableItemRespVO
     */
    public static VariableItemRespVO defNumVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField("n");
        variableItem.setLabel(MessageUtil.getMessage("OPEN_AI_N_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("OPEN_AI_N_DESCRIPTION"));
        variableItem.setDefaultValue(1);
        variableItem.setOrder(3);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.MODEL.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        variableItem.addOption("1", 1);
        variableItem.addOption("2", 2);
        variableItem.addOption("3", 3);
        variableItem.addOption("4", 4);
        variableItem.addOption("5", 5);
        variableItem.addOption("6", 6);
        variableItem.addOption("7", 7);
        variableItem.addOption("8", 8);
        variableItem.addOption("9", 9);
        variableItem.addOption("10", 10);
        return variableItem;
    }

    /**
     * Open AI Chat Completion prompt变量
     * Open AI Chat Completion prompt Variable
     *
     * @return VariableItemRespVO
     */
    public static VariableItemRespVO defPromptVariable(String defaultPrompt) {
        return defPromptVariable(defaultPrompt, Boolean.FALSE);
    }

    /**
     * Open AI Chat Completion prompt变量
     * Open AI Chat Completion prompt Variable
     *
     * @return VariableItemRespVO
     */
    public static VariableItemRespVO defPromptVariable(String defaultPrompt, Boolean isShow) {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField("prompt");
        variableItem.setLabel(MessageUtil.getMessage("OPEN_AI_PROMPT_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("OPEN_AI_PROMPT_DESCRIPTION"));
        variableItem.setDefaultValue(defaultPrompt);
        variableItem.setOrder(4);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TEXTAREA.name());
        variableItem.setGroup(AppVariableGroupEnum.MODEL.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(isShow);
        return variableItem;
    }

    // Open AI Chat Completion Variable Item ---------------------------------------------------------------------------

    // Article Generate Variable Item ----------------------------------------------------------------------------------\

    /**
     * 生成文章标题 主题 变量
     *
     * @return VariableItemRespVO
     */
    public static VariableItemRespVO defArticleTopicVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField("topic");
        variableItem.setLabel(MessageUtil.getMessage("ARTICLE_TOPIC_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("ARTICLE_TOPIC_DESCRIPTION"));
        variableItem.setDefaultValue(MessageUtil.getMessage("ARTICLE_TOPIC_DEFAULT_VALUE"));
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TEXTAREA.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 语言变量
     *
     * @return VariableItemRespVO
     */
    public static VariableItemRespVO defLanguageVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField("language");
        variableItem.setLabel(MessageUtil.getMessage("COMMON_LANGUAGE_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("COMMON_LANGUAGE_DESCRIPTION"));
        variableItem.setDefaultValue("English");
        variableItem.setValue("English");
        variableItem.setOrder(2);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        variableItem.setOptions(LanguageEnum.languageList(true));
        return variableItem;
    }

    /**
     * 写作风格变量
     *
     * @return VariableItemRespVO
     */
    public static VariableItemRespVO defWritingStyleVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField("writing_style");
        variableItem.setLabel(MessageUtil.getMessage("ARTICLE_WRITING_STYLE_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("ARTICLE_WRITING_STYLE_DESCRIPTION"));
        variableItem.setDefaultValue("Creative");
        variableItem.setValue("Creative");
        variableItem.setOrder(3);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        variableItem.setOptions(WritingStyleEnum.ofOptions());
        return variableItem;
    }

    /**
     * 写作基调变量
     *
     * @return VariableItemRespVO
     */
    public static VariableItemRespVO defWritingToneVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField("writing_tone");
        variableItem.setLabel(MessageUtil.getMessage("ARTICLE_WRITING_TONE_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("ARTICLE_WRITING_TONE_DESCRIPTION"));
        variableItem.setDefaultValue("Cheerful");
        variableItem.setValue("Cheerful");
        variableItem.setOrder(4);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        variableItem.setOptions(WritingToneEnum.ofOptions());
        return variableItem;
    }

    /**
     * 文章段落数量变量
     *
     * @return VariableItemRespVO
     */
    public static VariableItemRespVO defArticleSectionsVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField("sections");
        variableItem.setLabel(MessageUtil.getMessage("ARTICLE_SECTIONS_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("ARTICLE_SECTIONS_DESCRIPTION"));
        variableItem.setDefaultValue(2);
        variableItem.setValue(2);
        variableItem.setOrder(5);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        variableItem.addOption("2", 2);
        variableItem.addOption("5", 5);
        variableItem.addOption("10", 10);
        return variableItem;
    }

    /**
     * 文章内容段落数量变量
     *
     * @return VariableItemRespVO
     */
    public static VariableItemRespVO defArticleParagraphsVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField("paragraphs");
        variableItem.setLabel(MessageUtil.getMessage("ARTICLE_PARAGRAPHS_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("ARTICLE_PARAGRAPHS_DESCRIPTION"));
        variableItem.setDefaultValue(3);
        variableItem.setValue(3);
        variableItem.setOrder(6);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        variableItem.addOption("2", 2);
        variableItem.addOption("3", 3);
        variableItem.addOption("5", 5);
        variableItem.addOption("10", 10);
        return variableItem;
    }
    // Article Generate Variable Item ----------------------------------------------------------------------------------\


}
