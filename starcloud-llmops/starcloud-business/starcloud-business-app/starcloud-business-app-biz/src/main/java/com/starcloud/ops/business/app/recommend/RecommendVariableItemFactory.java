package com.starcloud.ops.business.app.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeGenerateModeEnum;
import com.starcloud.ops.business.app.recommend.enums.WritingStyleEnum;
import com.starcloud.ops.business.app.recommend.enums.WritingToneEnum;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.MessageUtil;
import com.starcloud.ops.framework.common.api.enums.LanguageEnum;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * 推荐应用Variable Item 工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
@SuppressWarnings("unused")
public class RecommendVariableItemFactory {

    // Open AI Chat Completion Variable Item ---------------------------------------------------------------------------

    /**
     * Open AI Chat Completion 最大token变量
     * Open AI Chat Completion Max Token Variable
     *
     * @return VariableItemRespVO
     */
    public static VariableItemRespVO defModelVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField("model");
        variableItem.setLabel(MessageUtil.getMessage("OPEN_AI_MODEL_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("OPEN_AI_MODEL_DESCRIPTION"));
        variableItem.setDefaultValue(ModelTypeEnum.GPT_3_5_TURBO_16K.getName());
        variableItem.setValue(ModelTypeEnum.GPT_3_5_TURBO_16K.getName());
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.MODEL.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        variableItem.setOptions(AppUtils.aiModelList());
        return variableItem;
    }

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
        variableItem.setValue(1000);
        variableItem.setOrder(2);
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
        variableItem.setValue(0.7);
        variableItem.setOrder(3);
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
        variableItem.setValue(1);
        variableItem.setOrder(4);
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
        variableItem.setValue(defaultPrompt);
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

    // SD Generate Image Variable Item ---------------------------------------------------------------------------------------\

    public static VariableItemRespVO defImageEngineVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField("engine");
        variableItem.setLabel(MessageUtil.getMessage("SD_IMAGE_ENGINE_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("SD_IMAGE_ENGINE_DESCRIPTION"));
        variableItem.setDefaultValue("pixabay");
        variableItem.setValue("pixabay");
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
//        variableItem.setOptions(ImageEngineEnum.ofOptions());
        return variableItem;
    }

    /**
     * 生成图片的数量
     *
     * @return VariableItemRespVO
     */
    public static VariableItemRespVO defImageSamplesVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField("samples");
        variableItem.setLabel(MessageUtil.getMessage("SD_IMAGE_SAMPLES_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("SD_IMAGE_SAMPLES_DESCRIPTION"));
        variableItem.setDefaultValue(1);
        variableItem.setValue(1);
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
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
     * 资料库类型
     *
     * @return 资料库类型
     */
    public static VariableItemRespVO defMaterialTypeVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.MATERIAL_TYPE);
        variableItem.setLabel("素材类型");
        variableItem.setDescription("素材类型");
        variableItem.setDefaultValue(MaterialTypeEnum.BOOK_LIST.getCode());
        variableItem.setValue(MaterialTypeEnum.BOOK_LIST.getCode());
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        variableItem.setOptions(MaterialTypeEnum.allOptions());
        return variableItem;
    }

    /**
     * 素材列表
     *
     * @return 素材列表
     */
    public static VariableItemRespVO defMaterialListVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.MATERIAL_LIST);
        variableItem.setLabel("素材列表");
        variableItem.setDescription("素材列表");
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.MATERIAL.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 生成模式
     *
     * @return 生成模式
     */
    public static VariableItemRespVO defMediaMatrixGenerateVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.GENERATE_MODE);
        variableItem.setLabel("生成模式");
        variableItem.setDescription("生成模式");
        variableItem.setDefaultValue(CreativeSchemeGenerateModeEnum.AI_PARODY.name());
        variableItem.setValue(CreativeSchemeGenerateModeEnum.AI_PARODY.name());
        variableItem.setOrder(1000);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.RADIO.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        variableItem.addOption(CreativeSchemeGenerateModeEnum.RANDOM.getLabel(), CreativeSchemeGenerateModeEnum.RANDOM.name(), "从参考内容中随机获取一条内容使用");
        variableItem.addOption(CreativeSchemeGenerateModeEnum.AI_PARODY.getLabel(), CreativeSchemeGenerateModeEnum.AI_PARODY.name(), "从参考内容中随机获取几条内容作为参考，并用AI进行仿写");
        variableItem.addOption(CreativeSchemeGenerateModeEnum.AI_CUSTOM.getLabel(), CreativeSchemeGenerateModeEnum.AI_CUSTOM.name(), "直接让AI生成内容，要求越详细越好");
        return variableItem;
    }

    /**
     * AI参考内容随机获取数量
     *
     * @return AI参考内容随机获取数量
     */
    public static VariableItemRespVO defMediaMatrixRefersCount() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.REFERS_COUNT);
        variableItem.setLabel("AI参考内容随机获取数量");
        variableItem.setDescription("AI参考内容随机获取数量");
        variableItem.setDefaultValue(3);
        variableItem.setValue(3);
        variableItem.setOrder(1040);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.INPUT.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        return variableItem;
    }

    /**
     * 参考内容变量
     *
     * @return 参考内容变量
     */
    public static VariableItemRespVO defMediaMatrixRefers() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.REFERS);
        variableItem.setLabel(MessageUtil.getMessage("MEDIA_MATRIX_REFERS_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("MEDIA_MATRIX_REFERS_DESCRIPTION"));
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(101);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.MATERIAL.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    public static VariableItemRespVO defMediaMatrixRefersImitate() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.REFERS_IMITATE);
        variableItem.setLabel(MessageUtil.getMessage("MEDIA_MATRIX_REFERS_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("MEDIA_MATRIX_REFERS_DESCRIPTION"));
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(102);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.MATERIAL.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    public static VariableItemRespVO defMediaMatrixMaterialType() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.MATERIAL_TYPE);
        variableItem.setLabel(MessageUtil.getMessage("MEDIA_MATRIX_MATERIAL_TYPE_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("MEDIA_MATRIX_MATERIAL_TYPE_DESCRIPTION"));
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(10000);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        variableItem.setOptions(MaterialTypeEnum.referOptions());
        return variableItem;
    }

    public static VariableItemRespVO defMediaMatrixMaterialJsonSchema() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.MATERIAL_JSONSCHEMA);
        variableItem.setLabel(MessageUtil.getMessage("MEDIA_MATRIX_MATERIAL_JSONSCHEMA_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("MEDIA_MATRIX_MATERIAL_JSONSCHEMA_DESCRIPTION"));
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(10001);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TEXTAREA.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        return variableItem;
    }


    public static VariableItemRespVO defMediaMatrixStepRespJsonSchema() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.STEP_RESP_JSONSCHEMA);
        variableItem.setLabel(MessageUtil.getMessage("MEDIA_MATRIX_STEP_RESP_JSONSCHEMA_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("MEDIA_MATRIX_STEP_RESP_JSONSCHEMA_DESCRIPTION"));
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(10002);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TEXTAREA.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        return variableItem;
    }


    /**
     * 生成段落数量
     *
     * @return 生成段落数量
     */
    public static VariableItemRespVO defMediaMatrixParagraphCount() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.PARAGRAPH_COUNT);
        variableItem.setLabel("生成段落数量");
        variableItem.setDescription("生成段落数量");
        variableItem.setDefaultValue(4);
        variableItem.setValue(4);
        variableItem.setOrder(102);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.INPUT.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 文案生成要求
     *
     * @return 文案生成要求
     */
    public static VariableItemRespVO defMediaMatrixRequirement() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.REQUIREMENT);
        variableItem.setLabel("文案生成要求");
        variableItem.setDescription("文案生成要求");
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(103);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TEXTAREA.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 拼接标题
     *
     * @return 拼接标题变量
     */
    public static VariableItemRespVO defAssembleTitle() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.TITLE);
        variableItem.setLabel("标题");
        variableItem.setDescription("标题");
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(105);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.INPUT.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 拼接内容
     *
     * @return 拼接内容容变量
     */
    public static VariableItemRespVO defAssembleContent() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.CONTENT);
        variableItem.setLabel("内容");
        variableItem.setDescription("内容");
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(104);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TEXTAREA.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 拼接内容
     *
     * @return 拼接内容容变量
     */
    public static VariableItemRespVO defAssembleTagList() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.TAG_LIST);
        variableItem.setLabel("标签");
        variableItem.setDescription("标签");
        variableItem.setDefaultValue(null);
        variableItem.setValue(null);
        variableItem.setOrder(105);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TAG_BOX.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 海报风格变量
     *
     * @return 海报风格变量
     */
    public static VariableItemRespVO defPosterStyleVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.POSTER_STYLE);
        variableItem.setLabel(MessageUtil.getMessage("POSTER_STYLE_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("POSTER_STYLE_DESCRIPTION"));
        variableItem.setDefaultValue("{}");
        variableItem.setValue("{}");
        variableItem.setOrder(2);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.JSON.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 海报风格配置
     *
     * @return 海报风格配置
     */
    public static VariableItemRespVO defPosterStyleConfigVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.POSTER_STYLE_CONFIG);
        variableItem.setLabel("风格配置");
        variableItem.setDescription("风格配置");
        variableItem.setDefaultValue("[]");
        variableItem.setValue("[]");
        variableItem.setOrder(3);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.JSON.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 系统风格配置
     *
     * @return 系统风格配置
     */
    public static VariableItemRespVO defSystemPosterStyleConfigVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.SYSTEM_POSTER_STYLE_CONFIG);
        variableItem.setLabel("风格配置");
        variableItem.setDescription("风格配置");
        variableItem.setDefaultValue("[]");
        variableItem.setValue("[]");
        variableItem.setOrder(3);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.JSON.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 海报生成标题变量
     *
     * @return 海报生成标题变量
     */
    public static VariableItemRespVO defPosterTitleVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.TITLE);
        variableItem.setLabel(MessageUtil.getMessage("POSTER_TITLE_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("POSTER_TITLE_DESCRIPTION"));
        variableItem.setDefaultValue("");
        variableItem.setValue("");
        variableItem.setOrder(3);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.INPUT.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 海报生成内容变量
     *
     * @return 海报生成内容变量
     */
    public static VariableItemRespVO defPosterContentVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.CONTENT);
        variableItem.setLabel(MessageUtil.getMessage("POSTER_CONTENT_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("POSTER_CONTENT_DESCRIPTION"));
        variableItem.setDefaultValue("");
        variableItem.setValue("");
        variableItem.setOrder(4);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TEXTAREA.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 海报生成要求
     *
     * @return 海报生成要求
     */
    public static VariableItemRespVO defPosterRequirement() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.REQUIREMENT);
        variableItem.setLabel("生成要求");
        variableItem.setDescription("生成要求");
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(1009);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TEXTAREA.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }
}
