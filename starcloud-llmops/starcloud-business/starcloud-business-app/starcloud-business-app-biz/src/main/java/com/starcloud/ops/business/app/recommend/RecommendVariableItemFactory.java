package com.starcloud.ops.business.app.recommend;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.domain.entity.chat.ModelProviderEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeContentGenerateModelEnum;
import com.starcloud.ops.business.app.recommend.enums.WritingStyleEnum;
import com.starcloud.ops.business.app.recommend.enums.WritingToneEnum;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.MessageUtil;
import com.starcloud.ops.framework.common.api.enums.LanguageEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

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
        variableItem.setDefaultValue(ModelProviderEnum.GPT35.name());
        variableItem.setValue(ModelProviderEnum.GPT35.name());
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.MODEL.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        variableItem.setOptions(AppUtils.llmModelTypeList());
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
     * 业务类型
     *
     * @return 资料库类型
     */
    public static VariableItemRespVO defMaterialTypeVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.BUSINESS_TYPE);
        variableItem.setLabel("业务类型");
        variableItem.setDescription("业务类型");
        variableItem.setDefaultValue("default");
        variableItem.setValue("default");
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.INPUT.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        return variableItem;
    }

    /**
     * 图片搜索习惯
     *
     * @return 资料库类型
     */
    public static VariableItemRespVO defSearchHabitsVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.SEARCH_HABITS);
        variableItem.setLabel("搜索习惯");
        variableItem.setDescription("图片搜索习惯");
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.JSON.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        return variableItem;
    }

    /**
     * 素材自定义结构
     *
     * @return 资料库类型
     */
    public static VariableItemRespVO defMaterialDefineVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.MATERIAL_DEFINE);
        variableItem.setLabel("素材定义");
        variableItem.setDescription("素材定义");
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.JSON.name());
        variableItem.setGroup(AppVariableGroupEnum.ADVANCED.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 素材库
     *
     * @return
     */
    public static VariableItemRespVO defMaterialLibraryVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.SELECT_MATERIAL_QUERY);
        variableItem.setLabel("选择模式素材库查询条件");
        variableItem.setDescription("选择模式素材库查询条件");
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.JSON.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        variableItem.setValue(StrUtil.EMPTY_JSON);
        variableItem.setDefaultValue(StrUtil.EMPTY_JSON);
        return variableItem;
    }

    /**
     * 素材使用模式
     *
     * @return 素材使用模式
     */
    public static VariableItemRespVO defMaterialUsageModelVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.MATERIAL_USAGE_MODEL);
        variableItem.setLabel("素材使用模式");
        variableItem.setDescription("素材使用模式");
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.INPUT.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        return variableItem;
    }

    /**
     * xhs字段映射
     *
     * @return
     */
    public static VariableItemRespVO defFieldMapVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.FIELD_MAP);
        variableItem.setDefaultValue(StrUtil.EMPTY_JSON);
        variableItem.setLabel("字段映射");
        variableItem.setDescription("字段映射");
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.JSON.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 小红书笔记地址
     *
     * @return
     */
    public static VariableItemRespVO defXhsUrlVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.XHS_NOTE_URL);
        variableItem.setLabel("小红书笔记地址");
        variableItem.setDefaultValue(StrUtil.EMPTY);
        variableItem.setDescription("小红书笔记地址");
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.INPUT.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 图片oss地址
     *
     * @return
     */
    public static VariableItemRespVO defImageUrlVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.IMAGE_OCR_URL);
        variableItem.setLabel("图片oss地址");
        variableItem.setDefaultValue(StrUtil.EMPTY);
        variableItem.setDescription("图片oss地址");
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.JSON.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
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
        variableItem.setIsShow(Boolean.FALSE);
        return variableItem;
    }

    /**
     * 素材生成配置
     *
     * @return 素材生成配置
     */
    public static VariableItemRespVO defMaterialGenerateConfigVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.MATERIAL_GENERATE_CONFIG);
        variableItem.setLabel("素材生成配置");
        variableItem.setDescription("素材生成配置");
        variableItem.setDefaultValue(StrUtil.EMPTY_JSON);
        variableItem.setValue(StrUtil.EMPTY_JSON);
        variableItem.setOrder(300);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.JSON.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 素材生成配置
     *
     * @return 素材生成配置
     */
    public static VariableItemRespVO defCustomMaterialGenerateConfigVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.CUSTOM_MATERIAL_GENERATE_CONFIG);
        variableItem.setLabel("素材自定义生成配置");
        variableItem.setDescription("素材自定义生成配置");
        variableItem.setDefaultValue(StrUtil.EMPTY_JSON);
        variableItem.setValue(StrUtil.EMPTY_JSON);
        variableItem.setOrder(400);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.JSON.name());
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
        variableItem.setDefaultValue(CreativeContentGenerateModelEnum.AI_CUSTOM.name());
        variableItem.setValue(CreativeContentGenerateModelEnum.AI_CUSTOM.name());
        variableItem.setOrder(1000);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.RADIO.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        variableItem.addOption(CreativeContentGenerateModelEnum.AI_CUSTOM.getLabel(), CreativeContentGenerateModelEnum.AI_CUSTOM.name(), "直接让AI生成内容，要求越详细越好");
//        variableItem.addOption(CreativeContentGenerateModelEnum.AI_PARODY.getLabel(), CreativeContentGenerateModelEnum.AI_PARODY.name(), "从参考内容中随机获取几条内容作为参考，并用AI进行仿写");
//        variableItem.addOption(CreativeContentGenerateModelEnum.RANDOM.getLabel(), CreativeContentGenerateModelEnum.RANDOM.name(), "从参考内容中随机获取一条内容使用");
        return variableItem;
    }


    /**
     * 笔记仿写生成模式
     *
     * @return 生成模式
     */
    public static VariableItemRespVO defMediaImitateGenerateVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.GENERATE_MODE);
        variableItem.setLabel("生成模式");
        variableItem.setDescription("生成模式");
        variableItem.setDefaultValue(CreativeContentGenerateModelEnum.AI_PARODY.name());
        variableItem.setValue(CreativeContentGenerateModelEnum.AI_PARODY.name());
        variableItem.setOrder(1000);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.RADIO.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        variableItem.addOption(CreativeContentGenerateModelEnum.AI_PARODY.getLabel(), CreativeContentGenerateModelEnum.AI_PARODY.name(), "从参考内容中随机获取几条内容作为参考，并用AI进行仿写");
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

    public static VariableItemRespVO defMediaMatrixRefersTag() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.TAG_LIST);
        variableItem.setLabel("参考标签");
        variableItem.setDescription("参考标签");
        variableItem.setDefaultValue(null);
        variableItem.setValue(null);
        variableItem.setOrder(102);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TAG_BOX.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    public static VariableItemRespVO defMediaMatrixRefersImage() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.REFERS_IMAGE);
        variableItem.setLabel("参考图片");
        variableItem.setDescription("参考图片");
        variableItem.setDefaultValue(null);
        variableItem.setValue(null);
        variableItem.setOrder(102);
        variableItem.setType(AppVariableTypeEnum.IMAGE.name());
//        variableItem.setStyle(AppVariableStyleEnum.IMAGE_LIST.name());
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
        variableItem.setDefaultValue(MaterialTypeEnum.NOTE_TITLE.getCode());
        variableItem.setValue(MaterialTypeEnum.NOTE_TITLE.getCode());
        variableItem.setOrder(10000);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        variableItem.setOptions(MaterialTypeEnum.referOptions());
        return variableItem;
    }

    public static VariableItemRespVO defMediaMatrixImitateType() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.MATERIAL_TYPE);
        variableItem.setLabel(MessageUtil.getMessage("MEDIA_MATRIX_MATERIAL_TYPE_LABEL"));
        variableItem.setDescription(MessageUtil.getMessage("MEDIA_MATRIX_MATERIAL_TYPE_DESCRIPTION"));
        variableItem.setDefaultValue(MaterialTypeEnum.NOTE.getCode());
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(10000);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.SELECT.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        variableItem.setOptions(Collections.singletonList(MaterialTypeEnum.NOTE.option()));
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
     * AI仿写文案生成要求
     *
     * @return 文案生成要求
     */
    public static VariableItemRespVO defParodyRequirement() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.PARODY_REQUIREMENT);
        variableItem.setLabel("文案生成要求");
        variableItem.setDescription("文案生成要求");
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(1001);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TEXTAREA.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * AI自定义文案生成要求
     *
     * @return 文案生成要求
     */
    public static VariableItemRespVO defCustomRequirement() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.CUSTOM_REQUIREMENT);
        variableItem.setLabel("文案生成要求");
        variableItem.setDescription("文案生成要求");
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(1002);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TEXTAREA.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        return variableItem;
    }

    /**
     * 系统默认prompt
     *
     * @return 系统默认prompt
     */
    public static VariableItemRespVO defDefaultContentStepPromptVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.DEFAULT_CONTENT_STEP_PROMPT);
        variableItem.setLabel("内容生成默认prompt");
        variableItem.setDescription("内容生成默认prompt");
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(103);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TEXTAREA.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        return variableItem;
    }

    /**
     * 系统 json parser prompt
     *
     * @return 系统默认prompt
     */
    public static VariableItemRespVO defDefaultResponseJsonParserPromptVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.DEFAULT_RESPONSE_JSON_PARSER_PROMPT);
        variableItem.setLabel("生成数据JSON格式化默认Prompt");
        variableItem.setDescription("生成数据JSON格式化默认Prompt，返回结果-响应类型选择为 JSON 时才会生效");
        variableItem.setDefaultValue(StringUtils.EMPTY);
        variableItem.setValue(StringUtils.EMPTY);
        variableItem.setOrder(104);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TEXTAREA.name());
        variableItem.setGroup(AppVariableGroupEnum.SYSTEM.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
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
     * 系统风格配置
     *
     * @return 系统风格配置
     */
    public static VariableItemRespVO defCustomPosterStyleConfigVariable() {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(CreativeConstants.CUSTOM_POSTER_STYLE_CONFIG);
        variableItem.setLabel("自定义风格配置");
        variableItem.setDescription("自定义风格配置");
        variableItem.setDefaultValue("[]");
        variableItem.setValue("[]");
        variableItem.setOrder(4);
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
