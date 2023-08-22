package com.starcloud.ops.business.app.convert.app;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.ChatConfigReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.ImageConfigReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.WorkflowConfigReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.skill.HandlerSkillVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ChatConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ImageConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.ImageAppEntity;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.skill.HandlerSkill;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.util.AppUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 我的应用转换类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Mapper
@SuppressWarnings("all")
public interface AppConvert {

    /**
     * AppConvert
     */
    AppConvert INSTANCE = Mappers.getMapper(AppConvert.class);

    /**
     * AppReqVO 转 AppEntity
     *
     * @param appRequest 我的应用请求对象
     * @return AppEntity
     */
    AppEntity convert(AppReqVO appRequest);

    /**
     * AppMarketEntity 转 AppEntity
     *
     * @param entity AppMarketEntity
     * @return AppEntity
     */
    AppEntity convert(AppMarketEntity entity);

    /**
     * AppEntity 转 AppDO
     *
     * @param appEntity 我的应用实体
     * @return AppDO
     */
    default AppDO convert(BaseAppEntity appEntity) {
        AppDO appDO = new AppDO();
        appDO.setUid(appEntity.getUid());
        appDO.setName(appEntity.getName());
        appDO.setModel(appEntity.getModel());
        appDO.setType(appEntity.getType());
        appDO.setSource(appEntity.getSource());
        appDO.setTags(AppUtils.join(appEntity.getTags()));
        appDO.setCategories(AppUtils.join(appEntity.getCategories()));
        appDO.setScenes(AppUtils.joinScenes(appEntity.getScenes()));
        appDO.setImages(AppUtils.join(appEntity.getImages()));
        appDO.setIcon(appEntity.getIcon());
        appDO.setDescription(appEntity.getDescription());
        appDO.setPublishUid(appEntity.getPublishUid());
        appDO.setInstallUid(appEntity.getInstallUid());
        appDO.setLastPublish(appEntity.getLastPublish());
        appDO.setDeleted(Boolean.FALSE);
        appDO.setCreator(appEntity.getCreator());
        // 处理配置
        if (AppModelEnum.COMPLETION.name().equals(appEntity.getModel())) {
            WorkflowConfigEntity config = appEntity.getWorkflowConfig();
            if (Objects.nonNull(config)) {
                appDO.setConfig(JSONUtil.toJsonStr(config));
            }
        } else if (AppModelEnum.CHAT.name().equals(appEntity.getModel())) {
            ChatConfigEntity config = appEntity.getChatConfig();
            if (Objects.nonNull(config)) {
                appDO.setConfig(JSONUtil.toJsonStr(config));
            }
        } else if (AppModelEnum.BASE_GENERATE_IMAGE.name().equals(appEntity.getModel())) {
            ImageConfigEntity config = appEntity.getImageConfig();
            if (Objects.nonNull(config)) {
                appDO.setConfig(JSONUtil.toJsonStr(config));
            }
        }
        return appDO;
    }

    /**
     * AppDO 转 AppEntity
     *
     * @param app AppDO
     * @return AppEntity
     */
    default BaseAppEntity convert(AppDO app, Boolean share) {
        BaseAppEntity appEntity = null;

        if (AppModelEnum.COMPLETION.name().equals(app.getModel())) {

            if (share) {
                //appEntity = new ShareAppEntity();
            } else {
                appEntity = new AppEntity();
            }

        } else if (AppModelEnum.CHAT.name().equals(app.getModel())) {

            if (share) {
                appEntity = new ChatAppEntity();
            } else {
                appEntity = new ChatAppEntity();
            }

        } else if (AppModelEnum.BASE_GENERATE_IMAGE.name().equals(app.getModel())) {

            appEntity = new ImageAppEntity();
        }

        appEntity.setUid(app.getUid());
        appEntity.setName(app.getName());
        appEntity.setModel(app.getModel());
        appEntity.setType(app.getType());
        appEntity.setSource(app.getSource());
        appEntity.setTags(AppUtils.split(app.getTags()));
        appEntity.setCategories(AppUtils.split(app.getCategories()));
        appEntity.setScenes(AppUtils.splitScenes(app.getScenes()));
        appEntity.setImages(AppUtils.split(app.getImages()));
        appEntity.setIcon(app.getIcon());
        appEntity.setDescription(app.getDescription());
        appEntity.setPublishUid(app.getPublishUid());
        appEntity.setInstallUid(app.getInstallUid());
        appEntity.setLastPublish(app.getLastPublish());

        appEntity.setCreator(app.getCreator());
        appEntity.setUpdater(app.getUpdater());

        appEntity.setCreateTime(app.getCreateTime());
        appEntity.setUpdateTime(app.getUpdateTime());

        appEntity.setTenantId(app.getTenantId());

        // 处理配置
        if (StringUtils.isNotBlank(app.getConfig())) {
            if (AppModelEnum.COMPLETION.name().equals(app.getModel())) {
                appEntity.setWorkflowConfig(JSONUtil.toBean(app.getConfig(), WorkflowConfigEntity.class));
            } else if (AppModelEnum.CHAT.name().equals(app.getModel())) {

                appEntity.setChatConfig(JSONUtil.toBean(app.getConfig(), ChatConfigEntity.class));
                appEntity.getChatConfig().init();

            } else if (AppModelEnum.BASE_GENERATE_IMAGE.name().equals(app.getModel())) {
                appEntity.setImageConfig(JSONUtil.toBean(app.getConfig(), ImageConfigEntity.class));
            }
        }
        return appEntity;
    }

    /**
     * AppMarketDO 转 AppEntity, 安装应用，更新应用时候使用
     *
     * @param appMarket AppMarketDO
     * @return AppEntity
     */
    default AppEntity convert(AppMarketDO appMarket) {
        AppEntity appEntity = new AppEntity();
        appEntity.setName(appMarket.getName());
        appEntity.setModel(appMarket.getModel());
        appEntity.setType(AppTypeEnum.INSTALLED.name());
        appEntity.setSource(AppSourceEnum.WEB.name());
        appEntity.setTags(AppUtils.split(appMarket.getTags()));
        appEntity.setCategories(AppUtils.split(appMarket.getCategories()));
        appEntity.setScenes(AppUtils.splitScenes(appMarket.getScenes()));
        appEntity.setImages(AppUtils.split(appMarket.getImages()));
        appEntity.setIcon(appMarket.getIcon());
        appEntity.setDescription(appMarket.getDescription());
        appEntity.setPublishUid(null);
        appEntity.setInstallUid(AppUtils.generateUid(appMarket.getUid(), appMarket.getVersion()));
        appEntity.setLastPublish(null);
        // 处理配置
        if (StringUtils.isNotBlank(appMarket.getConfig())) {
            if (AppModelEnum.COMPLETION.name().equals(appMarket.getModel())) {
                appEntity.setWorkflowConfig(JSONUtil.toBean(appMarket.getConfig(), WorkflowConfigEntity.class));
            } else if (AppModelEnum.CHAT.name().equals(appMarket.getModel())) {
                appEntity.setChatConfig(JSONUtil.toBean(appMarket.getConfig(), ChatConfigEntity.class));
            } else if (AppModelEnum.BASE_GENERATE_IMAGE.name().equals(appMarket.getModel())) {
                appEntity.setImageConfig(JSONUtil.toBean(appMarket.getConfig(), ImageConfigEntity.class));
            }
        }
        return appEntity;
    }

    default AppEntity convertApp(AppRespVO respVO) {
        AppEntity appEntity = new AppEntity();

        appEntity.setUid(respVO.getUid());
        appEntity.setName(respVO.getName());
        appEntity.setModel(respVO.getModel());
        appEntity.setType(respVO.getType());
        appEntity.setSource(respVO.getSource());
        appEntity.setTags(respVO.getTags());
        appEntity.setCategories(respVO.getCategories());
        appEntity.setScenes(respVO.getScenes());
        appEntity.setImages(respVO.getImages());
        appEntity.setIcon(respVO.getIcon());
        appEntity.setDescription(respVO.getDescription());
        appEntity.setPublishUid(respVO.getPublishUid());
        appEntity.setInstallUid(respVO.getInstallUid());
        appEntity.setLastPublish(respVO.getLastPublish());
        appEntity.setCreator(respVO.getCreator());

        // 处理配置
        if (respVO.getWorkflowConfig() != null) {
            appEntity.setWorkflowConfig(JSON.parseObject(JSON.toJSONString(respVO.getWorkflowConfig()), WorkflowConfigEntity.class));
        }
        if (respVO.getChatConfig() != null) {
            appEntity.setChatConfig(JSON.parseObject(JSON.toJSONString(respVO.getChatConfig()), ChatConfigEntity.class));
        }
        if (respVO.getImageConfig() != null) {
            appEntity.setImageConfig(JSON.parseObject(JSON.toJSONString(respVO.getImageConfig()), ImageConfigEntity.class));
        }

        return appEntity;
    }

    /**
     * AppDO 转 AppRespVO
     *
     * @param app AppDO
     * @return AppRespVO
     */
    default AppRespVO convertResponse(AppDO app) {
        AppRespVO appRespVO = new AppRespVO();
        appRespVO.setUid(app.getUid());
        appRespVO.setName(app.getName());
        appRespVO.setModel(app.getModel());
        appRespVO.setType(app.getType());
        appRespVO.setSource(app.getSource());
        appRespVO.setTags(AppUtils.split(app.getTags()));
        appRespVO.setCategories(AppUtils.split(app.getCategories()));
        appRespVO.setScenes(AppUtils.splitScenes(app.getScenes()));
        appRespVO.setImages(AppUtils.split(app.getImages()));
        appRespVO.setIcon(app.getIcon());
        appRespVO.setDescription(app.getDescription());
        appRespVO.setPublishUid(app.getPublishUid());
        appRespVO.setInstallUid(app.getInstallUid());
        appRespVO.setCreateTime(app.getCreateTime());
        appRespVO.setUpdateTime(app.getUpdateTime());
        appRespVO.setLastPublish(app.getLastPublish());
        // 处理配置
        if (StringUtils.isNotBlank(app.getConfig())) {
            if (AppModelEnum.COMPLETION.name().equals(app.getModel())) {
                appRespVO.setWorkflowConfig(JSONUtil.toBean(app.getConfig(), WorkflowConfigRespVO.class));
                appRespVO.setActionIcons(buildActionIcons(appRespVO.getWorkflowConfig()));
            } else if (AppModelEnum.CHAT.name().equals(app.getModel())) {
                appRespVO.setChatConfig(JSONUtil.toBean(app.getConfig(), ChatConfigRespVO.class));
            } else if (AppModelEnum.BASE_GENERATE_IMAGE.name().equals(app.getModel())) {
                appRespVO.setImageConfig(JSONUtil.toBean(app.getConfig(), ImageConfigRespVO.class));
            }
        }

        return appRespVO;
    }

    /**
     * AppDO 转 AppRespVO
     *
     * @param appEntity AppDO
     * @return AppRespVO
     */
    default AppRespVO convertResponse(BaseAppEntity appEntity) {
        AppRespVO appRespVO = new AppRespVO();
        appRespVO.setUid(appEntity.getUid());
        appRespVO.setName(appEntity.getName());
        appRespVO.setModel(appEntity.getModel());
        appRespVO.setType(appEntity.getType());
        appRespVO.setSource(appEntity.getSource());
        appRespVO.setTags(appEntity.getTags());
        appRespVO.setCategories(appEntity.getCategories());
        appRespVO.setScenes(appEntity.getScenes());
        appRespVO.setImages(appEntity.getImages());
        appRespVO.setIcon(appEntity.getIcon());
        appRespVO.setDescription(appEntity.getDescription());
        appRespVO.setPublishUid(appEntity.getPublishUid());
        appRespVO.setInstallUid(appEntity.getInstallUid());
        appRespVO.setCreateTime(appEntity.getCreateTime());
        appRespVO.setUpdateTime(appEntity.getUpdateTime());
        appRespVO.setLastPublish(appEntity.getLastPublish());
        // 处理配置
        if (AppModelEnum.COMPLETION.name().equals(appEntity.getModel())) {
            WorkflowConfigEntity config = appEntity.getWorkflowConfig();
            if (Objects.nonNull(config)) {
                appRespVO.setWorkflowConfig(JSONUtil.toBean(JSONUtil.toJsonStr(appEntity.getWorkflowConfig()), WorkflowConfigRespVO.class));
            }
        } else if (AppModelEnum.CHAT.name().equals(appEntity.getModel())) {
            ChatConfigEntity config = appEntity.getChatConfig();
            if (Objects.nonNull(config)) {
                appRespVO.setChatConfig(JSONUtil.toBean(JSONUtil.toJsonStr(appEntity.getChatConfig()), ChatConfigRespVO.class));
            }
        } else if (AppModelEnum.BASE_GENERATE_IMAGE.name().equals(appEntity.getModel())) {
            ImageConfigEntity config = appEntity.getImageConfig();
            if (Objects.nonNull(config)) {
                appRespVO.setImageConfig(JSONUtil.toBean(JSONUtil.toJsonStr(appEntity.getImageConfig()), ImageConfigRespVO.class));
            }
        }

        return appRespVO;
    }

    default AppReqVO convertRequest(AppMarketDO appMarketDO) {
        AppReqVO appReqVO = new AppReqVO();
        appReqVO.setName(appMarketDO.getName());
        appReqVO.setModel(appMarketDO.getModel());
        appReqVO.setTags(AppUtils.split(appMarketDO.getTags()));
        appReqVO.setCategories(AppUtils.split(appMarketDO.getCategories()));
        appReqVO.setScenes(AppUtils.splitScenes(appMarketDO.getScenes()));
        appReqVO.setImages(AppUtils.split(appMarketDO.getImages()));
        appReqVO.setIcon(appMarketDO.getIcon());
        appReqVO.setDescription(appMarketDO.getDescription());
        // 处理配置
        if (StringUtils.isNotBlank(appMarketDO.getConfig())) {
            if (AppModelEnum.COMPLETION.name().equals(appMarketDO.getModel())) {
                appReqVO.setWorkflowConfig(JSONUtil.toBean(appMarketDO.getConfig(), WorkflowConfigReqVO.class));
            } else if (AppModelEnum.CHAT.name().equals(appMarketDO.getModel())) {
                appReqVO.setChatConfig(JSONUtil.toBean(appMarketDO.getConfig(), ChatConfigReqVO.class));
            } else if (AppModelEnum.BASE_GENERATE_IMAGE.name().equals(appMarketDO.getModel())) {
                appReqVO.setImageConfig(JSONUtil.toBean(appMarketDO.getConfig(), ImageConfigReqVO.class));
            }
        }

        return appReqVO;
    }

    /**
     * 构建 actionIcons
     *
     * @param workflowConfig 工作流配置
     * @return actionIcons
     */
    default List<String> buildActionIcons(WorkflowConfigRespVO workflowConfig) {
        return CollectionUtil.emptyIfNull(workflowConfig.getSteps()).stream()
                .map(WorkflowStepWrapperRespVO::getFlowStep)
                .map(WorkflowStepRespVO::getIcon)
                .collect(Collectors.toList());
    }


    /**
     * handlerSkillVO 转 HandlerSkill
     *
     * @param handlerSkillVO HandlerSkillVO
     * @return AppMarketEntity
     */
    default HandlerSkill convert(HandlerSkillVO handlerSkillVO) {

        HandlerSkill handlerSkill = HandlerSkill.of(handlerSkillVO.getName());
        handlerSkill.setEnabled(handlerSkill.getEnabled());
        return handlerSkill;
    }

}
