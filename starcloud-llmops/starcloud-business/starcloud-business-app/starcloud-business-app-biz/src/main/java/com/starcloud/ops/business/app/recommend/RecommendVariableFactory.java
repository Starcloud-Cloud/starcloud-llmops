package com.starcloud.ops.business.app.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;

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
     * Open AI Chat Completion 默认变量
     *
     * @param defaultPrompt 默认提示
     * @return VariableRespVO
     */
    public static VariableRespVO defOpenAiVariable(String defaultPrompt, Boolean isShow) {
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Arrays.asList(
                RecommendVariableItemFactory.defMaxTokenVariable(),
                RecommendVariableItemFactory.defTemperatureVariable(),
                RecommendVariableItemFactory.defPromptVariable(defaultPrompt, isShow)
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
    public static VariableRespVO defContentVariable() {
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Collections.singletonList(RecommendVariableItemFactory.defMediaMatrixRefers()));
        return variable;
    }

    /**
     * 媒体矩阵开头变量
     *
     * @return 变量
     */
    public static VariableRespVO defParagraphVariable() {
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Collections.singletonList(RecommendVariableItemFactory.defMediaMatrixRefers()));
        return variable;
    }

    /**
     * 媒体矩阵内容变量
     *
     * @return 变量
     */
    public static VariableRespVO defAssembleVariable() {
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Collections.emptyList());
        return variable;
    }

    /**
     * 媒体矩阵开头变量
     *
     * @return 变量
     */
    public static VariableRespVO defPosterVariable() {
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Collections.emptyList());
        return variable;
    }
}
