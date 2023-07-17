package com.starcloud.ops.business.app.domain.factory;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.chat.ModelConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.OpenaiCompletionParams;
import com.starcloud.ops.business.app.domain.entity.skill.ActionSkillEntity;
import com.starcloud.ops.business.app.domain.entity.skill.ApiSkillEntity;
import com.starcloud.ops.business.app.domain.entity.skill.AppWorkflowSkillEntity;
import com.starcloud.ops.business.app.domain.handler.datasearch.GoogleSearchActionHandler;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.domain.repository.market.AppMarketRepository;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 获取步骤处理器工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
public class AppFactory {

    /**
     * AppRepository
     */
    private static AppRepository appRepository;

    private static AppMarketRepository appMarketRepository;

    /**
     * 获取 AppRepository
     *
     * @return AppRepository
     */
    public static AppRepository getAppRepository() {
        if (appRepository == null) {
            appRepository = SpringUtil.getBean(AppRepository.class);
        }
        return appRepository;
    }

    public static AppMarketRepository getAppMarketRepository() {
        if (appMarketRepository == null) {
            appMarketRepository = SpringUtil.getBean(AppMarketRepository.class);
        }
        return appMarketRepository;
    }

    /**
     * 获取 AppEntity 通过 appId
     *
     * @param appId appId
     * @return AppEntity
     */
    public static AppEntity factory(String appId) {
        return (AppEntity) getAppRepository().getByUid(appId);
    }

    public static ChatAppEntity factoryChatApp(String appId) {


        return (ChatAppEntity) getAppRepository().getByUid(appId);
    }

    /**
     * 通过模版市场 uid 获取 AppEntity
     *
     * @param appId appId
     * @return AppEntity
     */
    public static AppEntity factoryMarket(String appId) {
        AppMarketEntity appMarketEntity = getAppMarketRepository().get(appId);
        return AppConvert.INSTANCE.convert(appMarketEntity);
    }

    /**
     * 获取 AppEntity, 不通过数据库查询，直接通过请求参数构建。以 appRequest 为准
     *
     * @param appId      appId
     * @param appRequest appRequest
     * @return AppEntity
     */
    public static AppEntity factory(String appId, AppReqVO appRequest) {
        AppEntity app = AppConvert.INSTANCE.convert(appRequest);
        app.setUid(appId);
        Assert.notNull(app, "app fire is fail, app[{0}] not found", appId);
        return app;
    }

    public static ChatAppEntity factory(ChatRequestVO chatRequest) {

        String appId = chatRequest.getAppUid();

        if ("play".equals(appId)) {

            ChatAppEntity chatAppEntity = new ChatAppEntity();

            chatAppEntity.setUid(appId);
            chatAppEntity.setName("chat-play");

            ChatConfigEntity chatConfig = new ChatConfigEntity();

            chatConfig.setPrePrompt("@123 can assist users in answering a wide range of professional knowledge-related queries. @123 is able to answer users' questions professionally and enthusiastically, and give professional and detailed insights. @123 can answer questions that is related to the field of .");
            chatConfig.setSkills(Arrays.asList(
                    SkillFactory.factory(ApiSkillEntity.class).setUrl("https://baidu.com").setName("search-news").setDesc("A search engine. Useful for when you need to answer questions about news. Input should be a search query."),
                    SkillFactory.factory(ApiSkillEntity.class).setUrl("https://baidu.com").setName("search-food").setDesc("A search engine. Useful for when you need to answer questions about food. Input should be a search query."),
                    SkillFactory.factoryAppWorkflow("appUid-test")
            ));


            ModelConfigEntity modelConfig = new ModelConfigEntity();

            OpenaiCompletionParams openaiCompletionParams = new OpenaiCompletionParams();

            openaiCompletionParams.setModel("gpt-3.5-turbo");
            openaiCompletionParams.setMaxTokens(500);
            openaiCompletionParams.setTemperature(0.7);
            openaiCompletionParams.setStream(false);

            modelConfig.setCompletionParams(openaiCompletionParams);

            chatConfig.setModelConfig(modelConfig);

            chatAppEntity.setChatConfig(chatConfig);

            chatAppEntity.setCreator("1");
            chatAppEntity.setCreateTime(LocalDateTime.now());


            return chatAppEntity;
        }

        ChatAppEntity appEntity = factoryChatApp(chatRequest.getAppUid());


        return appEntity;
    }


    public static AppEntity factory(String appId, AppReqVO appRequest, String requestId) {
        return null;
    }
}
