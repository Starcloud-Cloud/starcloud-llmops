package com.starcloud.ops.business.app.convert.app;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ChatConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ImageConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.chat.ChatRequest;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.domain.entity.*;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.util.app.AppUtils;
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
        // 处理配置
        if (AppModelEnum.COMPLETION.name().equals(appEntity.getModel())) {
            WorkflowConfigEntity config = appEntity.getWorkflowConfig();
            if (Objects.nonNull(config)) {
                appDO.setConfig(JSON.toJSONString(config));
            }
        } else if (AppModelEnum.CHAT.name().equals(appEntity.getModel())) {
            ChatConfigEntity config = appEntity.getChatConfig();
            if (Objects.nonNull(config)) {
                appDO.setConfig(JSON.toJSONString(config));
            }
        } else if (AppModelEnum.IMAGE.name().equals(appEntity.getModel())) {
            ImageConfigEntity config = appEntity.getImageConfig();
            if (Objects.nonNull(config)) {
                appDO.setConfig(JSON.toJSONString(config));
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
    default BaseAppEntity convert(AppDO app) {
        BaseAppEntity appEntity = null;

        if (AppModelEnum.COMPLETION.name().equals(app.getModel())) {

            appEntity = new AppEntity();

        } else if (AppModelEnum.CHAT.name().equals(app.getModel())) {

            appEntity = new ChatAppEntity();

        } else if (AppModelEnum.IMAGE.name().equals(app.getModel())) {

            appEntity = new ImageAppEntity();
        }

        appEntity.setUid(app.getUid());
        appEntity.setName(app.getName());
        appEntity.setModel(app.getModel());
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
        // 处理配置
        if (StringUtils.isNotBlank(app.getConfig())) {
            if (AppModelEnum.COMPLETION.name().equals(app.getModel())) {
                appEntity.setWorkflowConfig(JSON.parseObject(app.getConfig(), WorkflowConfigEntity.class));
            } else if (AppModelEnum.CHAT.name().equals(app.getModel())) {
                appEntity.setChatConfig(JSON.parseObject(app.getConfig(), ChatConfigEntity.class));
            } else if (AppModelEnum.IMAGE.name().equals(app.getModel())) {
                appEntity.setImageConfig(JSON.parseObject(app.getConfig(), ImageConfigEntity.class));
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
                appEntity.setWorkflowConfig(JSON.parseObject(appMarket.getConfig(), WorkflowConfigEntity.class));
            } else if (AppModelEnum.CHAT.name().equals(appMarket.getModel())) {
                appEntity.setChatConfig(JSON.parseObject(appMarket.getConfig(), ChatConfigEntity.class));
            } else if (AppModelEnum.IMAGE.name().equals(appMarket.getModel())) {
                appEntity.setImageConfig(JSON.parseObject(appMarket.getConfig(), ImageConfigEntity.class));
            }
        }
        return appEntity;
    }

    /**
     * AppDO 转 AppRespVO
     *
     * @param app AppDO
     * @return AppRespVO
     */
    default AppRespVO convertResp(AppDO app) {
        AppRespVO appRespVO = new AppRespVO();
        appRespVO.setUid(app.getUid());
        appRespVO.setName(app.getName());
        appRespVO.setModel(app.getModel());
        appRespVO.setModel(app.getModel());
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
                appRespVO.setWorkflowConfig(JSON.parseObject(app.getConfig(), WorkflowConfigRespVO.class));
                appRespVO.setActionIcons(buildActionIcons(appRespVO.getWorkflowConfig()));
            } else if (AppModelEnum.CHAT.name().equals(app.getModel())) {
                appRespVO.setChatConfig(JSON.parseObject(app.getConfig(), ChatConfigRespVO.class));
            } else if (AppModelEnum.IMAGE.name().equals(app.getModel())) {
                appRespVO.setImageConfig(JSON.parseObject(app.getConfig(), ImageConfigRespVO.class));
            }
        }

        return appRespVO;
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

}
