package com.starcloud.ops.business.app.convert.app;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.skill.HandlerSkillVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ChatConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ImageConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
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
import com.starcloud.ops.business.app.recommend.RecommendStepWrapperFactory;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.PinyinCache;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
     * AppMarketRespVO 转 AppRespVO
     *
     * @param appMarket 我的应用市场响应对象
     * @return AppRespVO
     */
    AppRespVO convert(AppMarketRespVO appMarket);

    /**
     * AppEntity 转 AppDO
     *
     * @param appEntity 我的应用实体
     * @return AppDO
     */
    default AppDO convert(BaseAppEntity appEntity) {
        AppDO app = new AppDO();
        app.setUid(appEntity.getUid());
        app.setName(appEntity.getName());
        app.setModel(appEntity.getModel());
        app.setType(appEntity.getType());
        app.setSource(appEntity.getSource());
        app.setSort(appEntity.getSort());
        app.setCategory(appEntity.getCategory());
        app.setTags(AppUtils.join(appEntity.getTags()));
        app.setScenes(AppUtils.joinScenes(appEntity.getScenes()));
        app.setImages(AppUtils.join(appEntity.getImages()));
        app.setIcon(appEntity.getIcon());
        app.setDescription(appEntity.getDescription());
        app.setExample(appEntity.getExample());
        app.setDemo(appEntity.getDemo());
        app.setPluginList(JsonUtils.toJsonString(appEntity.getPluginList()));
        app.setPublishUid(appEntity.getPublishUid());
        app.setInstallUid(appEntity.getInstallUid());
        app.setLastPublish(appEntity.getLastPublish());
        app.setDeleted(Boolean.FALSE);
        app.setCreator(appEntity.getCreator());
        app.setUpdater(appEntity.getUpdater());
        // 处理配置
        if (AppModelEnum.COMPLETION.name().equals(appEntity.getModel())) {
            WorkflowConfigEntity config = appEntity.getWorkflowConfig();
            if (Objects.nonNull(config)) {
                app.setConfig(JsonUtils.toJsonString(config));
            }
        } else if (AppModelEnum.CHAT.name().equals(appEntity.getModel())) {
            ChatConfigEntity config = appEntity.getChatConfig();
            if (Objects.nonNull(config)) {
                app.setConfig(JsonUtils.toJsonString(config));
            }
        } else if (AppModelEnum.IMAGE.name().equals(appEntity.getModel())) {
            ImageConfigEntity config = appEntity.getImageConfig();
            if (Objects.nonNull(config)) {
                app.setConfig(JsonUtils.toJsonString(config));
            }
        }
        return app;
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

        } else if (AppModelEnum.IMAGE.name().equals(app.getModel())) {

            appEntity = new ImageAppEntity();
        }

        appEntity.setUid(app.getUid());
        appEntity.setName(app.getName());
        appEntity.setModel(app.getModel());
        appEntity.setType(app.getType());
        appEntity.setSource(app.getSource());
        appEntity.setSort(app.getSort());
        appEntity.setCategory(app.getCategory());
        appEntity.setTags(AppUtils.split(app.getTags()));
        appEntity.setScenes(AppUtils.splitScenes(app.getScenes()));
        appEntity.setImages(AppUtils.split(app.getImages()));
        appEntity.setIcon(app.getIcon());
        appEntity.setDescription(app.getDescription());
        appEntity.setExample(app.getExample());
        appEntity.setDemo(app.getDemo());
        appEntity.setPluginList(JsonUtils.parseArray(app.getPluginList(), String.class));
        appEntity.setPublishUid(app.getPublishUid());
        appEntity.setInstallUid(app.getInstallUid());
        appEntity.setLastPublish(app.getLastPublish());
        appEntity.setCreator(app.getCreator());
        appEntity.setUpdater(app.getUpdater());
        appEntity.setCreateTime(app.getCreateTime());
        appEntity.setUpdateTime(app.getUpdateTime());
        appEntity.setTenantId(app.getTenantId());
        appEntity.setMaterialList(app.getMaterialList());

        // 处理配置
        if (StringUtils.isNotBlank(app.getConfig())) {
            if (AppModelEnum.COMPLETION.name().equals(app.getModel())) {
                appEntity.setWorkflowConfig(JsonUtils.parseObject(app.getConfig(), WorkflowConfigEntity.class));
            } else if (AppModelEnum.CHAT.name().equals(app.getModel())) {

                appEntity.setChatConfig(JsonUtils.parseObject(app.getConfig(), ChatConfigEntity.class));
                appEntity.getChatConfig().init();

            } else if (AppModelEnum.IMAGE.name().equals(app.getModel())) {
                appEntity.setImageConfig(JsonUtils.parseObject(app.getConfig(), ImageConfigEntity.class));
            }
        }
        return appEntity;
    }

    default AppEntity convertAppEntity(AppRespVO respVO) {
        AppEntity appEntity = new AppEntity();

        appEntity.setUid(respVO.getUid());
        appEntity.setName(respVO.getName());
        appEntity.setModel(respVO.getModel());
        appEntity.setType(respVO.getType());
        appEntity.setSource(respVO.getSource());
        appEntity.setSort(respVO.getSort());
        appEntity.setCategory(respVO.getCategory());
        appEntity.setTags(respVO.getTags());
        appEntity.setScenes(respVO.getScenes());
        appEntity.setImages(respVO.getImages());
        appEntity.setIcon(respVO.getIcon());
        appEntity.setDescription(respVO.getDescription());
        appEntity.setExample(respVO.getExample());
        appEntity.setDemo(respVO.getDemo());
        appEntity.setPluginList(respVO.getPluginList());
        appEntity.setPublishUid(respVO.getPublishUid());
        appEntity.setInstallUid(respVO.getInstallUid());
        appEntity.setLastPublish(respVO.getLastPublish());
        appEntity.setCreator(respVO.getCreator());
        appEntity.setUpdater(respVO.getUpdater());

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
     * AppDO 转 AppRespVO, 需要查询用户昵称时候使用
     *
     * @param app AppDO
     * @return AppRespVO
     */
    default AppRespVO convertResponse(AppDO app) {
        return convertResponse(app, true);
    }

    /**
     * AppDO 转 AppRespVO
     *
     * @param app AppDO
     * @return AppRespVO
     */
    default AppRespVO convertResponse(AppDO app, boolean needUserName) {
        AppRespVO appResponse = new AppRespVO();
        appResponse.setUid(app.getUid());
        appResponse.setName(app.getName());
        appResponse.setSpell(PinyinCache.get(app.getName()));
        appResponse.setSpellSimple(PinyinCache.getSimple(app.getName()));
        appResponse.setModel(app.getModel());
        appResponse.setType(app.getType());
        appResponse.setSource(app.getSource());
        appResponse.setSort(app.getSort());
        appResponse.setCategory(app.getCategory());
        appResponse.setTags(AppUtils.split(app.getTags()));
        appResponse.setScenes(AppUtils.splitScenes(app.getScenes()));
        appResponse.setImages(AppUtils.split(app.getImages()));
        appResponse.setIcon(app.getIcon());
        appResponse.setDescription(app.getDescription());
        appResponse.setExample(app.getExample());
        appResponse.setDemo(app.getDemo());
        appResponse.setPluginList(JsonUtils.parseArray(app.getPluginList(), String.class));
        appResponse.setPublishUid(app.getPublishUid());
        appResponse.setInstallUid(app.getInstallUid());
        appResponse.setCreator(app.getCreator());
        appResponse.setUpdater(app.getUpdater());
        appResponse.setTenantId(app.getTenantId());
        if (needUserName) {
            appResponse.setCreatorName(UserUtils.getUsername(app.getCreator()));
            appResponse.setUpdaterName(UserUtils.getUsername(app.getUpdater()));
        }
        appResponse.setCreateTime(app.getCreateTime());
        appResponse.setUpdateTime(app.getUpdateTime());
        appResponse.setLastPublish(app.getLastPublish());
        // 处理配置
        if (StringUtils.isNotBlank(app.getConfig())) {
            if (AppModelEnum.COMPLETION.name().equals(app.getModel())) {
                appResponse.setWorkflowConfig(JsonUtils.parseObject(app.getConfig(), WorkflowConfigRespVO.class));
                appResponse.setActionIcons(buildActionIcons(appResponse.getWorkflowConfig()));
            } else if (AppModelEnum.CHAT.name().equals(app.getModel())) {
                appResponse.setChatConfig(JsonUtils.parseObject(app.getConfig(), ChatConfigRespVO.class));
            } else if (AppModelEnum.IMAGE.name().equals(app.getModel())) {
                appResponse.setImageConfig(JsonUtils.parseObject(app.getConfig(), ImageConfigRespVO.class));
            }
        }
        appResponse.supplementStepVariable(RecommendStepWrapperFactory.getStepVariable());

        return appResponse;
    }

    /**
     * Page 转 PageResp
     *
     * @param page 分页对象
     * @return PageResp
     */
    default PageResp<AppRespVO> convertPage(Page<AppDO> page) {
        if (page == null) {
            return PageResp.of(Collections.emptyList(), 0L, 1L, 10L);
        }
        List<AppDO> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return PageResp.of(Collections.emptyList(), page.getTotal(), page.getCurrent(), page.getSize());
        }
        // 用户创建者ID列表。
        List<Long> creatorList = records.stream().map(item -> Long.valueOf(item.getCreator())).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        // 获取用户创建者ID，昵称 Map。
        Map<Long, String> creatorMap = UserUtils.getUserNicknameMapByIds(creatorList);

        // 用户更新者ID列表。
        List<Long> updaterList = records.stream().map(item -> Long.valueOf(item.getUpdater())).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        // 获取用户更新者ID，昵称 Map。
        Map<Long, String> updaterMap = UserUtils.getUserNicknameMapByIds(updaterList);

        List<AppRespVO> collect = records.stream().map(item -> {
            AppRespVO appResponse = convertResponse(item, false);
            appResponse.setCreatorName(creatorMap.get(Long.valueOf(item.getCreator())));
            appResponse.setUpdaterName(updaterMap.get(Long.valueOf(item.getUpdater())));
            return appResponse;
        }).collect(Collectors.toList());

        return PageResp.of(collect, page.getTotal(), page.getCurrent(), page.getSize());
    }

    default AppRespVO convertResponse(BaseAppEntity appEntity) {
        return convertResponse(appEntity, false);
    }

    /**
     * AppDO 转 AppRespVO
     *
     * @param appEntity AppDO
     * @return AppRespVO
     */
    default AppRespVO convertResponse(BaseAppEntity appEntity, boolean needUserName) {
        AppRespVO appRespVO = new AppRespVO();
        appRespVO.setUid(appEntity.getUid());
        appRespVO.setName(appEntity.getName());
        appRespVO.setModel(appEntity.getModel());
        appRespVO.setType(appEntity.getType());
        appRespVO.setSource(appEntity.getSource());
        appRespVO.setSort(appEntity.getSort());
        appRespVO.setCategory(appEntity.getCategory());
        appRespVO.setTags(appEntity.getTags());
        appRespVO.setScenes(appEntity.getScenes());
        appRespVO.setImages(appEntity.getImages());
        appRespVO.setIcon(appEntity.getIcon());
        appRespVO.setDescription(appEntity.getDescription());
        appRespVO.setExample(appEntity.getExample());
        appRespVO.setDemo(appEntity.getDemo());
        appRespVO.setPluginList(appEntity.getPluginList());
        appRespVO.setPublishUid(appEntity.getPublishUid());
        appRespVO.setInstallUid(appEntity.getInstallUid());
        appRespVO.setCreator(appEntity.getCreator());
        appRespVO.setUpdater(appEntity.getUpdater());
        if (needUserName) {
            appRespVO.setCreatorName(UserUtils.getUsername(appEntity.getCreator()));
            appRespVO.setUpdaterName(UserUtils.getUsername(appEntity.getUpdater()));
        }
        appRespVO.setCreateTime(appEntity.getCreateTime());
        appRespVO.setUpdateTime(appEntity.getUpdateTime());
        appRespVO.setLastPublish(appEntity.getLastPublish());
        // 处理配置
        if (AppModelEnum.COMPLETION.name().equals(appEntity.getModel())) {
            WorkflowConfigEntity config = appEntity.getWorkflowConfig();
            if (Objects.nonNull(config)) {
                appRespVO.setWorkflowConfig(JsonUtils.parseObject(JsonUtils.toJsonString(appEntity.getWorkflowConfig()), WorkflowConfigRespVO.class));
            }
        } else if (AppModelEnum.CHAT.name().equals(appEntity.getModel())) {
            ChatConfigEntity config = appEntity.getChatConfig();
            if (Objects.nonNull(config)) {
                appRespVO.setChatConfig(JsonUtils.parseObject(JsonUtils.toJsonString(appEntity.getChatConfig()), ChatConfigRespVO.class));
            }
        } else if (AppModelEnum.IMAGE.name().equals(appEntity.getModel())) {
            ImageConfigEntity config = appEntity.getImageConfig();
            if (Objects.nonNull(config)) {
                appRespVO.setImageConfig(JsonUtils.parseObject(JsonUtils.toJsonString(appEntity.getImageConfig()), ImageConfigRespVO.class));
            }
        }

        return appRespVO;
    }

    AppReqVO convertRequest(AppMarketRespVO appResponse);

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
