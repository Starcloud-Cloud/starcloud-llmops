package com.starcloud.ops.business.app.domain.factory;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
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
    private static AppRepository appRepository;

    /**
     * 应用市场 Repository 服务
     */
    private static AppMarketRepository appMarketRepository;

    /**
     * 应用发布 Repository 服务
     */
    private static AppPublishRepository appPublishRepository;

    /**
     * 获取应用 Repository 服务
     *
     * @return AppRepository
     */
    public static AppRepository getAppRepository() {
        if (appRepository == null) {
            appRepository = SpringUtil.getBean(AppRepository.class);
        }
        return appRepository;
    }

    /**
     * 获取应用市场 Repository 服务
     *
     * @return AppMarketRepository
     */
    public static AppMarketRepository getAppMarketRepository() {
        if (appMarketRepository == null) {
            appMarketRepository = SpringUtil.getBean(AppMarketRepository.class);
        }
        return appMarketRepository;
    }

    /**
     * 获取应用发布 Repository 服务
     *
     * @return AppPublishRepository
     */
    public static AppPublishRepository getAppPublishRepository() {
        if (appPublishRepository == null) {
            appPublishRepository = SpringUtil.getBean(AppPublishRepository.class);
        }
        return appPublishRepository;
    }

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
            if (AppSceneEnum.WEB_MARKET.name().equals(request.getScene())) {
                return Objects.isNull(request.getAppReqVO()) ? AppFactory.factoryMarket(appId) : AppFactory.factoryMarket(appId, request.getAppReqVO());
                // 应用创作中心
            } else if (AppSceneEnum.WEB_ADMIN.name().equals(request.getScene())) {
                return Objects.isNull(request.getAppReqVO()) ? AppFactory.factoryApp(appId) : AppFactory.factoryApp(appId, request.getAppReqVO());
            }
        }

        // mediumUid 不为空的情况
        if (StringUtils.isNotBlank(request.getMediumUid())) {
            String mediumId = request.getMediumUid();
            // 应用市场场景
            if (AppSceneEnum.SHARE_WEB.name().equals(request.getScene())) {

                // 应用创作中心
            } else if (AppSceneEnum.SHARE_IFRAME.name().equals(request.getScene())) {

            }
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

        String appId = chatRequest.getAppUid();

        ChatAppEntity appEntity = factoryChatApp(chatRequest.getAppUid());

        return appEntity;
    }

    public static ChatAppEntity factory(String mediumUid) {
        return getAppPublishRepository().getChatEntityByMediumUid(mediumUid);
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
            imageAppEntity.setScenes(Collections.singletonList(AppSceneEnum.IMAGE.name()));
            imageAppEntity.setType(AppTypeEnum.MYSELF.name());
            imageAppEntity.setSource(AppSourceEnum.WEB.name());
            imageAppEntity.setImageConfig(ImageConvert.INSTANCE.convert(request.getImageRequest()));
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
        return (AppEntity) getAppRepository().get(appId);
    }

    /**
     * 获取 AppEntity, 不通过数据库查询，直接通过请求参数构建。以 appRequest 为准
     *
     * @param appId      appId
     * @param appRequest appRequest
     * @return AppEntity
     */
    public static AppEntity factoryApp(String appId, AppReqVO appRequest) {
        BaseAppEntity app = getAppRepository().get(appId);
        // 应用不存在, 还没有存入到数据库
        String creator, updator;
        if (Objects.isNull(app)) {
            Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
            if (Objects.isNull(loginUserId)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
            }
            creator = String.valueOf(loginUserId);
            updator = String.valueOf(loginUserId);
        } else {
            creator = app.getCreator();
            updator = app.getUpdater();
        }

        AppEntity appEntity = AppConvert.INSTANCE.convert(appRequest);
        appEntity.setUid(appId);
        appEntity.setCreator(creator);
        appEntity.setUpdater(creator);
        return appEntity;
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
        // 需要校验 模版市场 中是否存在该模版，不存在抛出异常
        AppMarketEntity market = getAppMarketRepository().get(appId);
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
        return (ChatAppEntity) getAppRepository().get(appId);
    }

    /**
     * @param appId
     * @return
     * @todo 通过 发布表 获取 具体的激活中的 appUid
     */
    public static AppEntity factoryShareApp(String appId) {

        //通过 发布表 获取 具体的激活中的 appUid

        appId = "2196b6cce43f41679e15487d79bde823";


        return factoryApp(appId);
    }

    /**
     * 获取 ImageAppEntity 通过 appId
     *
     * @param appId appId
     * @return ImageAppEntity
     */
    public static ImageAppEntity factoryImageApp(String appId) {
        return (ImageAppEntity) getAppRepository().get(appId);
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
        BaseAppEntity entity = AppConvert.INSTANCE.convert(appDO, false);
        return (ChatAppEntity) entity;
    }


}
