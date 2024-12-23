package com.starcloud.ops.business.app.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.domain.entity.chat.ModelProviderEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeContentGenerateModelEnum;

import java.util.Arrays;
import java.util.Collections;

/**
 * 推荐应用Variable工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendVariableFactory {

    /**
     * 空变量
     *
     * @return VariableRespVO
     */
    public static VariableRespVO defGlobalVariableVariable() {
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Collections.emptyList());
        return variable;
    }

    /**
     * Open AI Chat Completion 默认变量
     *
     * @param defaultPrompt 默认提示
     * @return VariableRespVO
     */
    public static VariableRespVO defOpenAiVariable(String defaultPrompt, Boolean isShow) {
        VariableRespVO variable = new VariableRespVO();

        VariableItemRespVO modelVariable = RecommendVariableItemFactory.defModelVariable();
        modelVariable.setOrder(1);
        modelVariable.setIsShow(Boolean.FALSE);

        VariableItemRespVO maxTokenVariable = RecommendVariableItemFactory.defMaxTokenVariable();
        maxTokenVariable.setOrder(2);
        maxTokenVariable.setIsShow(Boolean.FALSE);

        VariableItemRespVO temperatureVariable = RecommendVariableItemFactory.defTemperatureVariable();
        temperatureVariable.setOrder(3);
        temperatureVariable.setIsShow(Boolean.FALSE);

        VariableItemRespVO promptVariable = RecommendVariableItemFactory.defPromptVariable(defaultPrompt, isShow);
        promptVariable.setOrder(4);


        variable.setVariables(Arrays.asList(
                modelVariable,
                maxTokenVariable,
                temperatureVariable,
                promptVariable
        ));

        return variable;
    }

    /**
     * Open AI Chat Completion 默认变量
     *
     * @param defaultPrompt 默认提示
     * @return VariableRespVO
     */
    public static VariableRespVO defCustomVariable(String defaultPrompt, Boolean isShow) {
        VariableRespVO variable = new VariableRespVO();

        VariableItemRespVO modelVariable = RecommendVariableItemFactory.defModelVariable();
        modelVariable.setOrder(1);
        modelVariable.setIsShow(Boolean.FALSE);
        modelVariable.setDefaultValue(ModelProviderEnum.QWEN.name());
        modelVariable.setValue(ModelProviderEnum.QWEN.name());

        VariableItemRespVO maxTokenVariable = RecommendVariableItemFactory.defMaxTokenVariable();
        maxTokenVariable.setOrder(2);
        maxTokenVariable.setIsShow(Boolean.FALSE);
        maxTokenVariable.setValue(4000);
        maxTokenVariable.setDefaultValue(4000);

        VariableItemRespVO temperatureVariable = RecommendVariableItemFactory.defTemperatureVariable();
        temperatureVariable.setOrder(3);
        temperatureVariable.setIsShow(Boolean.FALSE);

        VariableItemRespVO promptVariable = RecommendVariableItemFactory.defPromptVariable(defaultPrompt, isShow);
        promptVariable.setOrder(4);


        variable.setVariables(Arrays.asList(
                modelVariable,
                maxTokenVariable,
                temperatureVariable,
                promptVariable
        ));

        return variable;
    }

    /**
     * 小红书ocr变量
     *
     * @return
     */
    public static VariableRespVO defXhsOcrVariable() {
        VariableItemRespVO xhsUrlVariable = RecommendVariableItemFactory.defXhsUrlVariable();
        xhsUrlVariable.setOrder(1);
        xhsUrlVariable.setIsShow(Boolean.TRUE);

        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Collections.singletonList(
                xhsUrlVariable
        ));
        return variable;
    }

    /**
     * 图片ocr
     *
     * @return
     */
    public static VariableRespVO defImageOcrVariable() {
        VariableItemRespVO imageUrlVariable = RecommendVariableItemFactory.defImageUrlVariable();
        imageUrlVariable.setOrder(1);
        imageUrlVariable.setIsShow(Boolean.TRUE);

        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Arrays.asList(
                imageUrlVariable
        ));
        return variable;
    }

    /**
     * 生成文章全局变量
     *
     * @return VariableRespVO
     */
    public static VariableRespVO defArticleGlobalVariable() {
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Arrays.asList(
                RecommendVariableItemFactory.defArticleTopicVariable(),
                RecommendVariableItemFactory.defLanguageVariable(),
                RecommendVariableItemFactory.defWritingStyleVariable(),
                RecommendVariableItemFactory.defWritingToneVariable()
        ));
        return variable;
    }

    /**
     * 文章段落数量
     *
     * @return VariableRespVO
     */
    public static VariableRespVO defArticleSectionsVariable() {
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Collections.singletonList(
                RecommendVariableItemFactory.defArticleSectionsVariable()
        ));
        return variable;
    }

    /**
     * 生成文章内容段落变量
     *
     * @return VariableRespVO
     */
    public static VariableRespVO defArticleParagraphs() {
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Collections.singletonList(
                RecommendVariableItemFactory.defArticleParagraphsVariable()
        ));
        return variable;
    }

    /**
     * 媒体矩阵标题变量
     *
     * @return 变量
     */
    public static VariableRespVO defTitleVariable() {
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Arrays.asList(
                RecommendVariableItemFactory.defMediaMatrixGenerateVariable(),
                RecommendVariableItemFactory.defMediaMatrixRefersCount(),
                RecommendVariableItemFactory.defMediaMatrixRefers(),
                RecommendVariableItemFactory.defMediaMatrixRequirement()
        ));
        return variable;
    }

    /**
     * 媒体矩阵自定义内容变量
     *
     * @return 变量
     */
    public static VariableRespVO defCustomVariable() {
        VariableRespVO variable = new VariableRespVO();
        // 生成模式
        VariableItemRespVO generateVariable = RecommendVariableItemFactory.defMediaMatrixGenerateVariable();
        generateVariable.setOrder(1);
        // 目前默认写死为AI自定义生成
        generateVariable.setValue(CreativeContentGenerateModelEnum.AI_CUSTOM.name());
        generateVariable.setDefaultValue(CreativeContentGenerateModelEnum.AI_CUSTOM.name());
        generateVariable.setIsShow(Boolean.TRUE);

        // 素材类型
        VariableItemRespVO materialType = RecommendVariableItemFactory.defMediaMatrixMaterialType();
        materialType.setOrder(2);
        materialType.setIsShow(Boolean.TRUE);

        // 素材参考数量
        VariableItemRespVO refersCount = RecommendVariableItemFactory.defMediaMatrixRefersCount();
        refersCount.setOrder(3);
        refersCount.setIsShow(Boolean.FALSE);

        // 参考素材
        VariableItemRespVO refers = RecommendVariableItemFactory.defMediaMatrixRefers();
        refers.setOrder(4);
        refers.setIsShow(Boolean.TRUE);

        // AI自定义生成要求
        VariableItemRespVO customRequirement = RecommendVariableItemFactory.defCustomRequirement();
        customRequirement.setOrder(5);
        customRequirement.setIsShow(Boolean.TRUE);

        // AI仿写生成要求
        VariableItemRespVO parodyRequirement = RecommendVariableItemFactory.defParodyRequirement();
        parodyRequirement.setOrder(6);
        parodyRequirement.setIsShow(Boolean.TRUE);

        // JSON Schema
        VariableItemRespVO jsonSchema = RecommendVariableItemFactory.defMediaMatrixMaterialJsonSchema();
        jsonSchema.setOrder(7);
        jsonSchema.setIsShow(Boolean.FALSE);

        // 响应JSON Schema
        VariableItemRespVO respJsonSchema = RecommendVariableItemFactory.defMediaMatrixStepRespJsonSchema();
        respJsonSchema.setOrder(8);
        respJsonSchema.setIsShow(Boolean.FALSE);

        // 系统提示
        VariableItemRespVO defaultContentStepPromp = RecommendVariableItemFactory.defDefaultContentStepPromptVariable();
        defaultContentStepPromp.setOrder(9);
        defaultContentStepPromp.setIsShow(Boolean.FALSE);

        VariableItemRespVO defaultResponseJsonParserPrompt = RecommendVariableItemFactory.defDefaultResponseJsonParserPromptVariable();
        defaultResponseJsonParserPrompt.setOrder(10);
        defaultResponseJsonParserPrompt.setIsShow(Boolean.FALSE);

        variable.setVariables(Arrays.asList(
                generateVariable,
                materialType,
                refersCount,
                refers,
                customRequirement,
                parodyRequirement,
                jsonSchema,
                respJsonSchema,
                defaultContentStepPromp,
                defaultResponseJsonParserPrompt
        ));
        return variable;
    }


    /**
     * 笔记仿写内容变量
     *
     * @return 变量
     */
    public static VariableRespVO defImitateVariable() {
        VariableRespVO variable = new VariableRespVO();
        // 生成模式
        VariableItemRespVO generateVariable = RecommendVariableItemFactory.defMediaImitateGenerateVariable();
        generateVariable.setOrder(1);
        generateVariable.setIsShow(Boolean.TRUE);

        // 素材类型
        VariableItemRespVO materialType = RecommendVariableItemFactory.defMediaMatrixImitateType();
        materialType.setOrder(2);
        materialType.setIsShow(Boolean.TRUE);

        // 素材参考数量
        VariableItemRespVO refersCount = RecommendVariableItemFactory.defMediaMatrixRefersCount();
        refersCount.setOrder(3);
        refersCount.setIsShow(Boolean.TRUE);

        // 参考素材
        VariableItemRespVO refers = RecommendVariableItemFactory.defMediaMatrixRefers();
        refers.setOrder(4);
        refers.setIsShow(Boolean.TRUE);
        // 模仿元素
        VariableItemRespVO refersImitate = RecommendVariableItemFactory.defMediaMatrixRefersImitate();
        refersImitate.setOrder(5);
        refersImitate.setIsShow(Boolean.FALSE);

        // 参考标签
        VariableItemRespVO refersTag = RecommendVariableItemFactory.defMediaMatrixRefersTag();
        refersImitate.setOrder(6);
        refersImitate.setIsShow(Boolean.TRUE);

        // 参考图片
        VariableItemRespVO refersImage = RecommendVariableItemFactory.defMediaMatrixRefersImage();
        refersImitate.setOrder(7);
        refersImitate.setIsShow(Boolean.TRUE);

        // 生成要求
        VariableItemRespVO requirement = RecommendVariableItemFactory.defMediaMatrixRequirement();
        requirement.setOrder(8);
        requirement.setIsShow(Boolean.TRUE);

        // JSON Schema
        VariableItemRespVO jsonSchema = RecommendVariableItemFactory.defMediaMatrixMaterialJsonSchema();
        jsonSchema.setOrder(9);
        jsonSchema.setIsShow(Boolean.FALSE);

        // 响应JSON Schema
        VariableItemRespVO respJsonSchema = RecommendVariableItemFactory.defMediaMatrixStepRespJsonSchema();
        respJsonSchema.setOrder(10);
        respJsonSchema.setIsShow(Boolean.FALSE);

        variable.setVariables(Arrays.asList(
                generateVariable,
                materialType,
                refersCount,
//                refers,
                refersImitate,
                refersTag,
                refersImage,
                requirement,
                jsonSchema,
                respJsonSchema
        ));
        return variable;
    }

    /**
     * 媒体矩阵开头变量
     *
     * @return 变量
     */
    public static VariableRespVO defParagraphVariable() {
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Arrays.asList(
                RecommendVariableItemFactory.defMediaMatrixGenerateVariable(),
                RecommendVariableItemFactory.defMediaMatrixRefersCount(),
                RecommendVariableItemFactory.defMediaMatrixRefers(),
                RecommendVariableItemFactory.defMediaMatrixParagraphCount(),
                RecommendVariableItemFactory.defMediaMatrixRequirement()
        ));
        return variable;
    }

    /**
     * 媒体矩阵内容变量
     *
     * @return 变量
     */
    public static VariableRespVO defAssembleVariable() {
        VariableRespVO variable = new VariableRespVO();

        VariableItemRespVO assembleTitle = RecommendVariableItemFactory.defAssembleTitle();
        assembleTitle.setOrder(1);
        assembleTitle.setIsShow(Boolean.TRUE);

        VariableItemRespVO assembleContent = RecommendVariableItemFactory.defAssembleContent();
        assembleContent.setOrder(2);
        assembleContent.setIsShow(Boolean.TRUE);

        VariableItemRespVO assembleTagList = RecommendVariableItemFactory.defAssembleTagList();
        assembleTagList.setOrder(3);
        assembleTagList.setIsShow(Boolean.TRUE);

        variable.setVariables(Arrays.asList(
                assembleTitle,
                assembleContent,
                assembleTagList
        ));
        return variable;
    }

    /**
     * 媒体矩阵开头变量
     *
     * @return 变量
     */
    public static VariableRespVO defPosterVariable() {
        VariableRespVO variable = new VariableRespVO();

        VariableItemRespVO posterStyleVariable = RecommendVariableItemFactory.defPosterStyleVariable();
        posterStyleVariable.setOrder(1);
        posterStyleVariable.setIsShow(Boolean.FALSE);

        VariableItemRespVO requirement = RecommendVariableItemFactory.defPosterRequirement();
        requirement.setOrder(2);
        requirement.setIsShow(Boolean.FALSE);

        VariableItemRespVO styleConfigVariable = RecommendVariableItemFactory.defPosterStyleConfigVariable();
        styleConfigVariable.setOrder(3);
        styleConfigVariable.setIsShow(Boolean.TRUE);

        VariableItemRespVO customStyleConfigVariable = RecommendVariableItemFactory.defCustomPosterStyleConfigVariable();
        customStyleConfigVariable.setOrder(4);
        customStyleConfigVariable.setIsShow(Boolean.TRUE);

        variable.setVariables(Arrays.asList(
                posterStyleVariable,
                requirement,
                styleConfigVariable,
                customStyleConfigVariable
        ));
        return variable;
    }

    /**
     * Open AI Chat Completion 默认变量
     *
     * @param defaultPrompt 默认提示
     * @return VariableRespVO
     */
    public static VariableRespVO defPosterStepVariable(String defaultPrompt, Boolean isShow) {
        VariableRespVO variable = new VariableRespVO();

        VariableItemRespVO modelVariable = RecommendVariableItemFactory.defModelVariable();
        modelVariable.setOrder(1);
        modelVariable.setIsShow(Boolean.FALSE);

        VariableItemRespVO maxTokenVariable = RecommendVariableItemFactory.defMaxTokenVariable();
        maxTokenVariable.setOrder(2);
        maxTokenVariable.setIsShow(Boolean.FALSE);

        VariableItemRespVO temperatureVariable = RecommendVariableItemFactory.defTemperatureVariable();
        temperatureVariable.setOrder(3);
        temperatureVariable.setIsShow(Boolean.FALSE);

        VariableItemRespVO promptVariable = RecommendVariableItemFactory.defPromptVariable(defaultPrompt, isShow);
        promptVariable.setOrder(4);

        VariableItemRespVO styleConfigVariable = RecommendVariableItemFactory.defSystemPosterStyleConfigVariable();
        styleConfigVariable.setOrder(5);
        styleConfigVariable.setIsShow(Boolean.FALSE);

        variable.setVariables(Arrays.asList(
                modelVariable,
                maxTokenVariable,
                temperatureVariable,
                promptVariable,
                styleConfigVariable
        ));
        return variable;
    }

    /**
     * 空变量
     *
     * @return VariableRespVO
     */
    public static VariableRespVO defMaterialVariable() {
        VariableRespVO variable = new VariableRespVO();

        VariableItemRespVO materialTypeVariable = RecommendVariableItemFactory.defMaterialTypeVariable();
        materialTypeVariable.setOrder(1);
        materialTypeVariable.setIsShow(Boolean.FALSE);

        VariableItemRespVO materialDefineVariable = RecommendVariableItemFactory.defMaterialDefineVariable();
        materialDefineVariable.setOrder(2);
        materialDefineVariable.setIsShow(Boolean.FALSE);

        VariableItemRespVO materialListVariable = RecommendVariableItemFactory.defMaterialListVariable();
        materialListVariable.setOrder(3);
        materialListVariable.setIsShow(Boolean.FALSE);

        VariableItemRespVO materialLibraryVariable = RecommendVariableItemFactory.defMaterialLibraryVariable();
        materialLibraryVariable.setOrder(4);
        materialLibraryVariable.setIsShow(true);

        VariableItemRespVO materialGenerateConfigVariable = RecommendVariableItemFactory.defMaterialGenerateConfigVariable();
        materialGenerateConfigVariable.setOrder(5);
        materialGenerateConfigVariable.setIsShow(Boolean.TRUE);

        VariableItemRespVO customMaterialGenerateConfigVariable = RecommendVariableItemFactory.defCustomMaterialGenerateConfigVariable();
        customMaterialGenerateConfigVariable.setOrder(6);
        customMaterialGenerateConfigVariable.setIsShow(Boolean.TRUE);

        VariableItemRespVO searchHabitsVariable = RecommendVariableItemFactory.defSearchHabitsVariable();
        searchHabitsVariable.setOrder(7);
        searchHabitsVariable.setIsShow(Boolean.FALSE);

        VariableItemRespVO materialUsageModel = RecommendVariableItemFactory.defMaterialUsageModelVariable();
        materialUsageModel.setOrder(8);
        materialUsageModel.setIsShow(Boolean.FALSE);

        variable.setVariables(Arrays.asList(
                materialTypeVariable,
                materialListVariable,
                materialUsageModel,
                materialLibraryVariable,
                // materialDefineVariable,
                // materialGenerateConfigVariable,
                // customMaterialGenerateConfigVariable,
                searchHabitsVariable
        ));
        return variable;
    }
}
