package com.starcloud.ops.business.app.domain.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;

import java.util.Arrays;

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
}
