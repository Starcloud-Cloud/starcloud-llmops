package com.starcloud.ops.business.app.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.*;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.RecommendAppEnum;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.util.MessageUtil;
import com.starcloud.ops.business.app.util.AppUtils;

import java.util.Arrays;
import java.util.Collections;

/**
 * 推荐应用工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendAppFactory {

    /**
     * 生成文本应用
     *
     * @return AppRespVO
     */
    public static AppRespVO defGenerateTextApp() {
        AppRespVO app = new AppRespVO();
        app.setUid(RecommendAppEnum.GENERATE_TEXT.name());
        app.setName(MessageUtil.getMessage("WORKFLOW_STEP_GENERATE_TEXT_NAME"));
        app.setDescription(MessageUtil.getMessage("WORKFLOW_STEP_GENERATE_TEXT_DESCRIPTION"));
        app.setModel(AppModelEnum.COMPLETION.name());
        app.setType(AppTypeEnum.MYSELF.name());
        app.setSource(AppSourceEnum.WEB.name());
        app.setTags(Collections.singletonList("Generate Text"));
        app.setCategories(Collections.singletonList("SEO_WRITING"));
        app.setScenes(AppUtils.DEFAULT_SCENES);
        app.setImages(Collections.singletonList(AppConstants.APP_MARKET_DEFAULT_IMAGE));
        app.setIcon("seo");
        app.setWorkflowConfig(RecommendConfigFactory.defGenerateTextConfig());
        return app;
    }

    /**
     * 生成文章应用
     *
     * @return AppRespVO
     */
    public static AppRespVO defGenerateArticleApp() {
        AppRespVO app = new AppRespVO();
        app.setUid(RecommendAppEnum.GENERATE_ARTICLE.name());
        app.setName(MessageUtil.getMessage("GENERATE_ARTICLE_APP_NAME"));
        app.setDescription(MessageUtil.getMessage("GENERATE_ARTICLE_APP_DESCRIPTION"));
        app.setModel(AppModelEnum.COMPLETION.name());
        app.setType(AppTypeEnum.MYSELF.name());
        app.setSource(AppSourceEnum.WEB.name());
        app.setTags(Collections.singletonList("Generate Article"));
        app.setCategories(Collections.singletonList("SEO_WRITING"));
        app.setScenes(AppUtils.DEFAULT_SCENES);
        app.setImages(Collections.singletonList(AppConstants.APP_MARKET_DEFAULT_IMAGE));
        app.setIcon("seo");
        app.setWorkflowConfig(RecommendConfigFactory.defGenerateArticleConfig());
        return app;
    }

    /**
     * 生成聊天机器人
     *
     * @return AppRespVO
     */
    public static AppRespVO defChatRobotApp() {
        AppRespVO app = new AppRespVO();
        app.setUid(RecommendAppEnum.CHAT_ROBOT.name());
        app.setName("亚马逊AI助手");
        app.setDescription("作为一名亚马逊运营AI助手，我熟悉亚马逊运营中的各种基础技能，可以帮你做市场研究和分析、优化产品listing、优化广告文案、处理客户问题。");
        app.setModel(AppModelEnum.CHAT.name());
        app.setType(AppTypeEnum.MYSELF.name());
        app.setSource(AppSourceEnum.WEB.name());
        app.setTags(Collections.singletonList("Chat"));
        app.setCategories(Collections.singletonList("SEO_WRITING"));
        app.setScenes(AppUtils.DEFAULT_SCENES);
        app.setImages(Collections.singletonList("https://download.hotsalecloud.com/avatar/f88670eed0534ccd9bd80df21b92bf2c.svg"));
        app.setIcon("seo");

        ChatConfigRespVO chatConfigRespVO = new ChatConfigRespVO();
        ModelConfigRespVO openaiModel = ModelConfigRespVO.builder().provider("openai").completionParams(new OpenaiCompletionRespVo()).build();
        OpeningStatementRespVO openingStatementRespVO = new OpeningStatementRespVO();
        openingStatementRespVO.setEnabled(true);
        openingStatementRespVO.setStatement("你好！我是一个人工智能助手，我可以回答你的问题、提供信息、进行翻译、提供建议等等。请告诉我你需要什么帮助。");
        chatConfigRespVO.setOpeningStatement(openingStatementRespVO);
        CommonQuestionRespVO commonQuestionRespVO = new CommonQuestionRespVO();
        commonQuestionRespVO.setEnabled(true);
        commonQuestionRespVO.setContent("你能帮助我做什么？");
        DescriptionRespVo descriptionRespVo = new DescriptionRespVo();
        descriptionRespVo.setEnabled(true);
        chatConfigRespVO.setDescription(descriptionRespVo);
        chatConfigRespVO.setCommonQuestion(Arrays.asList(commonQuestionRespVO));
        chatConfigRespVO.setModelConfig(openaiModel);
        chatConfigRespVO.setPrePrompt("As an AI, you embody a seasoned Amazon operations expert. Tasked with delivering accurate, insightful responses to inquiries about Amazon's supply chain and logistics, leverage your comprehensive understanding of Amazon's systems and processes. Employ data analysis and problem-solving skills to provide thorough answers. Focus on Amazon operations, adeptly redirect unrelated queries, and utilize industry-specific terminology to offer clear, detailed responses. Reflect the questioner's language style to enhance communication. As an Amazon operations expert, avoid unrelated topics. Your ultimate goal: deliver precise, pertinent information, aiding users in their Amazon operations journey, thereby fostering smooth, efficient processes.");
        app.setChatConfig(chatConfigRespVO);
        return app;
    }

    public static AppRespVO emptyChatRobotApp() {
        AppRespVO app = new AppRespVO();
        app.setUid(RecommendAppEnum.CHAT_ROBOT.name());
//        app.setName("亚马逊AI助手");
//        app.setDescription("作为一名亚马逊运营AI助手，我熟悉亚马逊运营中的各种基础技能，可以帮你做市场研究和分析、优化产品listing、优化广告文案、处理客户问题。");
        app.setModel(AppModelEnum.CHAT.name());
        app.setType(AppTypeEnum.MYSELF.name());
        app.setSource(AppSourceEnum.WEB.name());
        app.setTags(Collections.singletonList("Chat"));
        app.setCategories(Collections.singletonList("SEO_WRITING"));
        app.setScenes(AppUtils.DEFAULT_SCENES);
        app.setImages(Collections.singletonList("https://download.hotsalecloud.com/avatar/f88670eed0534ccd9bd80df21b92bf2c.svg"));
        app.setIcon("seo");

        ChatConfigRespVO chatConfigRespVO = new ChatConfigRespVO();
        ModelConfigRespVO openaiModel = ModelConfigRespVO.builder().provider("openai").completionParams(new OpenaiCompletionRespVo()).build();
        OpeningStatementRespVO openingStatementRespVO = new OpeningStatementRespVO();
        openingStatementRespVO.setEnabled(true);
        openingStatementRespVO.setStatement("你好！我是一个人工智能助手，我可以回答你的问题、提供信息、进行翻译、提供建议等等。请告诉我你需要什么帮助。");
        chatConfigRespVO.setOpeningStatement(openingStatementRespVO);
        DescriptionRespVo descriptionRespVo = new DescriptionRespVo();
        descriptionRespVo.setEnabled(true);
        chatConfigRespVO.setDescription(descriptionRespVo);
        chatConfigRespVO.setModelConfig(openaiModel);
        app.setChatConfig(chatConfigRespVO);
        return app;
    }

}
