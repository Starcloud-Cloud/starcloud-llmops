package com.starcloud.ops.business.app.domain.recommend;

import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.util.MessageUtil;
import com.starcloud.ops.business.app.util.app.AppUtils;

import java.util.Collections;

/**
 * 推荐应用工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
public class RecommendedAppFactory {

    /**
     * 生成文本应用
     *
     * @return AppRespVO
     */
    public static AppRespVO defGenerateTextApp() {
        AppRespVO app = new AppRespVO();
        app.setUid(AppRecommendedConsts.GENERATE_TEXT);
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
        app.setWorkflowConfig(RecommendedConfigFactory.defGenerateTextConfig());
        return app;
    }

    /**
     * 生成文章应用
     *
     * @return AppRespVO
     */
    public static AppRespVO defGenerateArticleApp() {
        AppRespVO app = new AppRespVO();
        app.setUid(AppRecommendedConsts.GENERATE_ARTICLE);
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
        app.setWorkflowConfig(RecommendedConfigFactory.defGenerateArticleConfig());
        return app;
    }

    /**
     * 生成文章应用
     *
     * @return AppRespVO
     */
    public static AppRespVO defChatRobotApp() {
        AppRespVO app = new AppRespVO();
        app.setUid(AppRecommendedConsts.CHAT_ROBOT);
        app.setName(MessageUtil.getMessage("CHAT_ROBOT_NAME"));
        app.setDescription(MessageUtil.getMessage("CHAT_ROBOT_DESCRIPTION"));
        app.setModel(AppModelEnum.CHAT.name());
        app.setType(AppTypeEnum.MYSELF.name());
        app.setSource(AppSourceEnum.WEB.name());
        app.setTags(Collections.singletonList("Chat"));
        app.setCategories(Collections.singletonList("SEO_WRITING"));
        app.setScenes(AppUtils.DEFAULT_SCENES);
        app.setImages(Collections.singletonList(AppConstants.APP_MARKET_DEFAULT_IMAGE));
        app.setIcon("seo");
        return app;
    }

}
