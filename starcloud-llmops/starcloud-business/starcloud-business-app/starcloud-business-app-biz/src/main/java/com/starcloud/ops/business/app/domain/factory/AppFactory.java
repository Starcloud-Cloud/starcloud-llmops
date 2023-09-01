package com.starcloud.ops.business.app.domain.factory;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.image.ImageConvert;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.ImageAppEntity;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.domain.repository.market.AppMarketRepository;
import com.starcloud.ops.business.app.domain.repository.publish.AppPublishRepository;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.recommend.RecommendAppConsts;
import com.starcloud.ops.business.app.service.limit.AppLimitRequest;
import com.starcloud.ops.business.app.service.limit.AppLimitService;
import com.starcloud.ops.business.app.validate.AppValidate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Objects;

/**
 * 获取应用工厂
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Validated
@SuppressWarnings("all")
public class AppFactory {

    /**
     * 应用 Repository 服务
     */
    private static AppRepository appRepository = SpringUtil.getBean(AppRepository.class);

    /**
     * 应用市场 Repository 服务
     */
    private static AppMarketRepository appMarketRepository = SpringUtil.getBean(AppMarketRepository.class);

    /**
     * 应用发布 Repository 服务
     */
    private static AppPublishRepository appPublishRepository = SpringUtil.getBean(AppPublishRepository.class);

    /**
     * 应用限流服务
     */
    private static AppLimitService appLimitService = SpringUtil.getBean(AppLimitService.class);

    /**
     * 获取 执行实体
     *
     * @param request 请求参数
     * @return BaseAppEntity
     */
    public static BaseAppEntity factory(@Valid AppExecuteReqVO request) {

        // 校验参数, appUid 和 mediumUid 不能同时为空
        if (StringUtils.isBlank(request.getAppUid()) && StringUtils.isBlank(request.getMediumUid())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(50000001, "appUid 和 mediumUid 不能同时为空"));
        }

        // AppUid 不为空的情况
        if (StringUtils.isNotBlank(request.getAppUid())) {
            String appId = request.getAppUid();
            // 应用市场场景
            if (AppSceneEnum.WEB_MARKET.name().equals(request.getScene()) || AppSceneEnum.OPTIMIZE_PROMPT.name().equals(request.getScene())) {
                AppMarketEntity market = Objects.isNull(request.getAppReqVO()) ? AppFactory.factoryMarket(appId) : AppFactory.factoryMarket(appId, request.getAppReqVO());
                appLimitService.marketLimit(AppLimitRequest.of(market.getUid(), request.getScene()), request.getSseEmitter());
                return market;
                // 应用创作中心
            } else if (AppSceneEnum.WEB_ADMIN.name().equals(request.getScene())) {
                AppEntity appEntity = Objects.isNull(request.getAppReqVO()) ? AppFactory.factoryApp(appId) : AppFactory.factoryApp(appId, request.getAppReqVO());
                appLimitService.appLimit(AppLimitRequest.of(appEntity.getUid(), request.getScene()), request.getSseEmitter());
                return appEntity;
            }
        }

        // mediumUid 不为空的情况
        if (StringUtils.isNotBlank(request.getMediumUid())) {
            String mediumId = request.getMediumUid();
            AppEntity appEntity = Objects.isNull(request.getAppReqVO()) ? AppFactory.factoryShareApp(mediumId) : AppFactory.factoryShareApp(mediumId, request.getAppReqVO());
            appLimitService.channelLimit(AppLimitRequest.of(mediumId, request.getScene(), request.getEndUser()), request.getSseEmitter());
            return appEntity;
        }

        throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_NO_EXISTS_UID);
    }

    /**
     * 获取 ChatAppEntity
     *
     * @param chatRequest 请求参数
     * @return ChatAppEntity
     */
    public static ChatAppEntity factory(@Valid ChatRequestVO chatRequest) {

        if (AppSceneEnum.CHAT_MARKET.name().equalsIgnoreCase(chatRequest.getScene())) {
            appLimitService.marketLimit(AppLimitRequest.of(chatRequest.getAppUid(), chatRequest.getScene()), chatRequest.getSseEmitter());
            AppMarketEntity appMarketEntity = AppFactory.factoryMarket(chatRequest.getAppUid());
            return AppMarketConvert.INSTANCE.convert2(appMarketEntity);
        }

        if (AppSceneEnum.CHAT_TEST.name().equalsIgnoreCase(chatRequest.getScene())) {
            String appId = chatRequest.getAppUid();
            appLimitService.appLimit(AppLimitRequest.of(appId, chatRequest.getScene()), chatRequest.getSseEmitter());
            ChatAppEntity appEntity = factoryChatApp(chatRequest.getAppUid());
            return appEntity;
        }

        if (StringUtils.isNotBlank(chatRequest.getMediumUid())) {
            String mediumId = chatRequest.getMediumUid();
            appLimitService.channelLimit(AppLimitRequest.of(mediumId, chatRequest.getScene(), chatRequest.getEndUser()), chatRequest.getSseEmitter());
            return factory(chatRequest.getMediumUid());
        }

        throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_NO_EXISTS_UID);
    }

    public static ChatAppEntity factory(String mediumUid) {
        return appPublishRepository.getChatEntityByMediumUid(mediumUid);
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
            imageAppEntity.setName("AI图片生成");
            imageAppEntity.setModel(AppModelEnum.BASE_GENERATE_IMAGE.name());
            imageAppEntity.setScenes(Collections.singletonList(AppSceneEnum.WEB_IMAGE.name()));
            imageAppEntity.setType(AppTypeEnum.MYSELF.name());
            imageAppEntity.setSource(AppSourceEnum.WEB.name());
            imageAppEntity.setImageConfig(ImageConvert.INSTANCE.convert(request.getImageRequest()));
            // 限流逻辑
            appLimitService.appLimit(AppLimitRequest.of(imageAppEntity.getUid(), request.getScene()));
            return imageAppEntity;
        }
        ImageAppEntity imageAppEntity = factoryImageApp(appUid);
        imageAppEntity.setImageConfig(ImageConvert.INSTANCE.convert(request.getImageRequest()));
        return imageAppEntity;
    }

    /**
     * 获取 AppEntity 通过 appId
     *
     * @param appId appId
     * @return AppEntity
     */
    public static AppEntity factoryApp(String appId) {
        return (AppEntity) appRepository.get(appId);
    }

    /**
     * 获取 AppEntity, 不通过数据库查询，直接通过请求参数构建。以 appRequest 为准
     *
     * @param appUid     appId
     * @param appRequest appRequest
     * @return AppEntity
     */
    public static AppEntity factoryApp(String appUid, AppReqVO appRequest) {
        BaseAppEntity app = appRepository.get(appUid);
        String create, update;
        if (app == null) {
            Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
            if (loginUserId == null) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
            }
            create = String.valueOf(loginUserId);
            update = String.valueOf(loginUserId);
        } else {
            create = app.getCreator();
            update = app.getUpdater();
        }

        AppEntity appEntity = AppConvert.INSTANCE.convert(appRequest);
        appEntity.setUid(appUid);
        appEntity.setCreator(create);
        appEntity.setUpdater(update);
        return appEntity;
    }

    /**
     * 通过模版市场 uid 获取 AppEntity
     *
     * @param appId appId
     * @return AppEntity
     */
    public static AppMarketEntity factoryMarket(String appId) {
        return appMarketRepository.get(appId);
    }

    /**
     * 通过模版市场 uid 获取 AppEntity
     *
     * @param appId appId
     * @return AppEntity
     */
    public static AppMarketEntity factoryMarket(String appId, AppReqVO appRequest) {
        // 需要校验 模版市场 中是否存在该模版，不存在抛出异常
        AppMarketEntity market = appMarketRepository.get(appId);
        AppMarketEntity appMarketEntity = AppMarketConvert.INSTANCE.convert(appRequest);
        appMarketEntity.setUid(appId);
        appMarketEntity.setCreator(market.getCreator());
        appMarketEntity.setUpdater(market.getUpdater());
        return appMarketEntity;
    }

    /**
     * 获取 ChatAppEntity 通过 appId
     *
     * @param appId appId
     * @return ChatAppEntity
     */
    public static ChatAppEntity factoryChatApp(String appId) {
        return (ChatAppEntity) appRepository.get(appId);
    }

    /**
     * 获取 AppEntity 通过 mediumUid
     *
     * @param mediumUid
     * @return AppEntity
     */
    public static AppEntity factoryShareApp(String mediumUid) {
        return appPublishRepository.getAppEntityByMediumUid(mediumUid);
    }

    /**
     * 获取 AppEntity 通过 mediumUid
     *
     * @param mediumUid  mediumUid
     * @param appRequest appRequest
     * @return AppEntity
     */
    public static AppEntity factoryShareApp(String mediumUid, AppReqVO appRequest) {
        // 需要校验 模版市场 中是否存在该模版，不存在抛出异常
        AppEntity app = factoryShareApp(mediumUid);
        AppEntity appEntity = AppConvert.INSTANCE.convert(appRequest);
        appEntity.setUid(app.getUid());
        appEntity.setCreator(app.getCreator());
        appEntity.setUpdater(app.getUpdater());
        appEntity.setTenantId(app.getTenantId());
        TenantContextHolder.setTenantId(app.getTenantId());
        return appEntity;
    }

    /**
     * 获取 ImageAppEntity 通过 appId
     *
     * @param appId appId
     * @return ImageAppEntity
     */
    public static ImageAppEntity factoryImageApp(String appId) {
        return (ImageAppEntity) appRepository.get(appId);
    }

    /**
     * 通过 publishUid 查询 ChatAppEntity
     *
     * @param publishUid
     * @return
     */
    public static ChatAppEntity factoryChatAppByPublishUid(String publishUid) {
        AppPublishDO appPublishDO = appPublishRepository.getByPublishUid(publishUid);
        String appInfo = appPublishDO.getAppInfo();
        AppDO appDO = JSONUtil.toBean(appInfo, AppDO.class);
        BaseAppEntity entity = AppConvert.INSTANCE.convert(appDO, false);
        return (ChatAppEntity) entity;
    }


}
