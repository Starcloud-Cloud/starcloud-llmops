package com.starcloud.ops.business.app.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;

import java.util.Arrays;
import java.util.Collections;

/**
 * 推荐应用Config工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendConfigFactory {

    /**
     * 生成文本默认配置
     *
     * @return WorkflowConfigRespVO
     */
    public static WorkflowConfigRespVO defGenerateTextConfig() {
        WorkflowConfigRespVO config = new WorkflowConfigRespVO();
        config.setSteps(Collections.singletonList(RecommendStepWrapperFactory.defDefaultTextCompletionStepWrapper()));
        config.setVariable(null);
        return config;
    }

    /**
     * 生成文章默认配置
     *
     * @return WorkflowConfigRespVO
     */
    public static WorkflowConfigRespVO defGenerateArticleConfig() {
        WorkflowConfigRespVO config = new WorkflowConfigRespVO();
        config.setSteps(Arrays.asList(
                RecommendStepWrapperFactory.defArticleTitleStepWrapper(),
                RecommendStepWrapperFactory.defArticleSectionsStepWrapper(),
                RecommendStepWrapperFactory.defArticleContentStepWrapper(),
                RecommendStepWrapperFactory.defArticleExcerptStepWrapper()
        ));
        config.setVariable(null);
        return config;
    }
}
