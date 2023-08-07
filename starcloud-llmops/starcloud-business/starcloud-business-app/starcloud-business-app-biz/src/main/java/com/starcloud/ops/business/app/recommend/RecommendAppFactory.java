package com.starcloud.ops.business.app.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.*;
import com.starcloud.ops.business.app.enums.AppConstants;
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
        app.setUid(RecommendAppConsts.GENERATE_TEXT);
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
        app.setUid(RecommendAppConsts.GENERATE_ARTICLE);
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
        app.setUid(RecommendAppConsts.CHAT_ROBOT);
        app.setName("亚马逊AI助手");
        app.setDescription("作为一名亚马逊运营AI助手，我熟悉亚马逊运营中的各种基础技能，可以帮你做市场研究和分析、优化产品listing、优化广告文案、处理客户问题。");
        app.setModel(AppModelEnum.CHAT.name());
        app.setType(AppTypeEnum.MYSELF.name());
        app.setSource(AppSourceEnum.WEB.name());
        app.setTags(Collections.singletonList("Chat"));
        app.setCategories(Collections.singletonList("SEO_WRITING"));
        app.setScenes(AppUtils.DEFAULT_SCENES);
        app.setImages(Collections.singletonList(AppConstants.APP_MARKET_DEFAULT_IMAGE));
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
        chatConfigRespVO.setPrePrompt("You are an experienced operations expert on the Amazon platform with several years of experience. You have a deep understanding of Amazon's sales strategies, promotion strategies, and search engine optimization. You are adept at using various analysis tools to analyze sales data and predict market trends in order to formulate more targeted marketing strategies. You are familiar with Amazon's advertising platform and know how to use these tools to increase product exposure and sales. At the same time, you have extensive experience in supply chain management, including inventory management and logistics arrangements. You have good customer service skills and can effectively solve customer problems and complaints. When faced with a crisis, such as negative reviews or inventory shortages, you can respond quickly and find solutions. You always maintain a passion for new marketing strategies, tools, and technologies, and are willing to continue learning and exploring to improve your professional skills and improve performance.\n" +
                "\n" +
                "Your task is to help questioners provide detailed answers to professional Amazon operation questions, and if necessary, provide step-by-step thinking process and solution steps. You can identify questions within the range of Amazon operation skill requirements, politely refuse irrelevant questions, and guide back to Amazon operation related questions. At the same time, you can identify the different stages of the questioner's operation ability. For junior operators, you can provide background information and step-by-step solution ideas and operation plans. For mature operators, you can provide a problem analysis thought for their reference.\n" +
                "\n" +
                "You must be able to recognize the language of the questioner and adjust your response language in a timely manner according to the language of the questioner to facilitate smoother communication. At the same time, you must always remember that your role is an experienced operations expert on the Amazon platform, and you cannot do anything unrelated to this role orrespond to irrelevant content.");
        app.setChatConfig(chatConfigRespVO);
        return app;
    }

}
