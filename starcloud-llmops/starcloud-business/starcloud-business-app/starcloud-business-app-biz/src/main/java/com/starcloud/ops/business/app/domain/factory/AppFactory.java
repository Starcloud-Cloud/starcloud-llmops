package com.starcloud.ops.business.app.domain.factory;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.image.ImageConvert;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.domain.entity.*;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.chat.ModelConfigEntity;
import com.starcloud.ops.business.app.domain.entity.chat.WebSearchConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.OpenaiCompletionParams;
import com.starcloud.ops.business.app.domain.repository.publish.AppPublishRepository;
import com.starcloud.ops.business.app.recommend.RecommendAppConsts;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.domain.repository.market.AppMarketRepository;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.validate.AppValidate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * 获取步骤处理器工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Validated
public class AppFactory {

    /**
     * AppRepository
     */
    private static AppRepository appRepository;

    private static AppMarketRepository appMarketRepository;

    private static AppPublishRepository appPublishRepository;

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

    public static AppPublishRepository getAppPublishRepository() {
        if (appPublishRepository == null) {
            appPublishRepository = SpringUtil.getBean(AppPublishRepository.class);
        }
        return appPublishRepository;
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


    /**
     * @param appId
     * @return
     * @todo 通过 发布表 获取 具体的激活中的 appUid
     */
    public static AppEntity factoryShareApp(String appId) {

        //通过 发布表 获取 具体的激活中的 appUid

        appId = "2196b6cce43f41679e15487d79bde823";


        return factory(appId);
    }


    /**
     * 获取 ChatAppEntity 通过 appId
     *
     * @param appId appId
     * @return ChatAppEntity
     */
    public static ChatAppEntity factoryChatApp(String appId) {
        return (ChatAppEntity) getAppRepository().getByUid(appId);
    }


    /**
     * 通过 publishUid 查询 ChatAppEntity
     *
     * @param publishUid
     * @return
     */
    public static ChatAppEntity factoryChatAppByPublishUid(String publishUid) {
        AppPublishDO appPublishDO = getAppPublishRepository().getByPublishUid(publishUid);
        String appInfo = appPublishDO.getAppInfo();
        AppDO appDO = JSONUtil.toBean(appInfo, AppDO.class);
        BaseAppEntity entity = AppConvert.INSTANCE.convert(appDO,false);
        return (ChatAppEntity) entity;
    }

    /**
     * 获取 ImageAppEntity 通过 appId
     *
     * @param appId appId
     * @return ImageAppEntity
     */
    public static ImageAppEntity factoryImageApp(String appId) {
        return (ImageAppEntity) getAppRepository().getByUid(appId);
    }

    /**
     * 通过模版市场 uid 获取 AppEntity
     *
     * @param appId appId
     * @return AppEntity
     */
    public static AppMarketEntity factoryMarket(String appId) {
        return getAppMarketRepository().get(appId);
    }

    /**
     * 通过模版市场 uid 获取 AppEntity
     *
     * @param appId appId
     * @return AppEntity
     */
    public static AppMarketEntity factoryMarket(String appId, AppReqVO appRequest) {
        getAppMarketRepository().get(appId);
        AppMarketEntity appMarketEntity = AppMarketConvert.INSTANCE.convert(appRequest);
        appMarketEntity.setUid(appId);
        return appMarketEntity;
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

    public static BaseAppEntity factory(@Valid AppExecuteReqVO executeReqVO) {

        // 获取 AppEntity
        BaseAppEntity app = null;
        String appId = executeReqVO.getAppUid();
        if (AppSceneEnum.WEB_MARKET.name().equals(executeReqVO.getScene())) {
            if (executeReqVO.getAppReqVO() == null) {
                app = AppFactory.factoryMarket(appId);
            } else {
                app = AppFactory.factoryMarket(appId, executeReqVO.getAppReqVO());
            }
        } else {
            if (executeReqVO.getAppReqVO() == null) {
                app = AppFactory.factory(appId);
            } else {
                app = AppFactory.factory(appId, executeReqVO.getAppReqVO());
            }
        }

//        if (executeReqVO.getAppReqVO() == null) {
//            if (AppSceneEnum.WEB_MARKET.name().equals(executeReqVO.getScene())) {
//                app = AppFactory.factoryMarket(appId);
//            } else {
//                app = AppFactory.factory(appId);
//            }
//        } else {
//            app = AppFactory.factory(appId, executeReqVO.getAppReqVO());
//        }

        Assert.notNull(app, "app fire is fail, app[{0}] not found", appId);
        return app;
    }

    public static ChatAppEntity factory(@Valid ChatRequestVO chatRequest) {

        String appId = chatRequest.getAppUid();

        ChatAppEntity appEntity = factoryChatApp(chatRequest.getAppUid());

        return appEntity;
    }

    /**
     * 构建 ImageAppEntity
     *
     * @param request 请求参数
     * @return ImageAppEntity
     */
    public static ImageAppEntity factory(ImageReqVO request) {
        String appUid = request.getAppUid();
        AppValidate.notBlank(appUid, ErrorCodeConstants.APP_UID_IS_REQUIRED);
        if (RecommendAppConsts.BASE_GENERATE_IMAGE.equals(appUid)) {
            ImageAppEntity imageAppEntity = new ImageAppEntity();
            imageAppEntity.setUid(appUid);
            imageAppEntity.setName(RecommendAppConsts.BASE_GENERATE_IMAGE);
            imageAppEntity.setModel(AppModelEnum.BASE_GENERATE_IMAGE.name());
            imageAppEntity.setScenes(Collections.singletonList(StringUtils.isBlank(request.getScene()) ? AppSceneEnum.WEB_ADMIN.name() : request.getScene()));
            imageAppEntity.setType(AppTypeEnum.MYSELF.name());
            imageAppEntity.setSource(AppSourceEnum.WEB.name());
            imageAppEntity.setImageConfig(ImageConvert.INSTANCE.convert(request.getImageRequest()));
            return imageAppEntity;
        }
        ImageAppEntity imageAppEntity = factoryImageApp(appUid);
        imageAppEntity.setImageConfig(ImageConvert.INSTANCE.convert(request.getImageRequest()));
        return imageAppEntity;
    }

}
