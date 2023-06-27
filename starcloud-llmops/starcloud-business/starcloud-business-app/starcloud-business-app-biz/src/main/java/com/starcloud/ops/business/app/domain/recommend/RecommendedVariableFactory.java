package com.starcloud.ops.business.app.domain.recommend;

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
public class RecommendedVariableFactory {

    /**
     * Open AI Chat Completion 默认变量
     *
     * @param defaultPrompt 默认提示
     * @return VariableRespVO
     */
    public static VariableRespVO defOpenAiVariable(String defaultPrompt, Boolean isShow) {
        VariableRespVO variable = new VariableRespVO();
        variable.setVariables(Arrays.asList(
                RecommendedVariableItemFactory.defMaxTokenVariable(),
                RecommendedVariableItemFactory.defTemperatureVariable(),
                RecommendedVariableItemFactory.defNumVariable(),
                RecommendedVariableItemFactory.defPromptVariable(defaultPrompt, isShow)
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
                RecommendedVariableItemFactory.defArticleTopicVariable(),
                RecommendedVariableItemFactory.defLanguageVariable(),
                RecommendedVariableItemFactory.defWritingStyleVariable(),
                RecommendedVariableItemFactory.defWritingToneVariable(),
                RecommendedVariableItemFactory.defTemperatureVariable(),
                RecommendedVariableItemFactory.defMaxTokenVariable()
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
                RecommendedVariableItemFactory.defArticleSectionsVariable()
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
                RecommendedVariableItemFactory.defArticleParagraphsVariable()
        ));
        return variable;
    }
}
