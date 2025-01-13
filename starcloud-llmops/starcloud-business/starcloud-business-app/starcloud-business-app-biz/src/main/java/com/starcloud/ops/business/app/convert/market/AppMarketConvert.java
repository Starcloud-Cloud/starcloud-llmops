package com.starcloud.ops.business.app.convert.market;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.AppUpdateReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.skill.HandlerSkillVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ChatConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ImageConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketReqVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.skill.HandlerSkill;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.publish.AppPublishAuditEnum;
import com.starcloud.ops.business.app.recommend.RecommendStepWrapperFactory;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.PinyinCache;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 模版市场转换器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@SuppressWarnings("all")
@Mapper
public interface AppMarketConvert {

    /**
     * AppMarketConvert 实例
     */
    AppMarketConvert INSTANCE = Mappers.getMapper(AppMarketConvert.class);

    /**
     * AppMarketReqVO 转 AppMarketEntity
     *
     * @param appMarketRequest AppMarketReqVO
     * @return AppMarketEntity
     */
    AppMarketEntity convert(AppMarketReqVO appMarketRequest);

    /**
     * AppMarketEntity 转 ChatAppEntity
     *
     * @param appMarketEntity AppMarketEntity
     * @return ChatAppEntity
     */
    ChatAppEntity convertChat(AppMarketEntity appMarketEntity);

    /**
     * AppMarketEntity 转 AppMarketDO
     *
     * @param appMarketEntity AppMarketEntity
     * @return AppMarketDO
     */
    default AppMarketDO convert(AppMarketEntity appMarketEntity) {
        AppMarketDO appMarket = new AppMarketDO();
        appMarket.setUid(appMarketEntity.getUid());
        appMarket.setName(appMarketEntity.getName());
        appMarket.setType(appMarketEntity.getType());
        appMarket.setModel(appMarketEntity.getModel());
        appMarket.setVersion(appMarketEntity.getVersion());
        appMarket.setLanguage(appMarketEntity.getLanguage());
        appMarket.setSort(appMarketEntity.getSort());
        appMarket.setTags(AppUtils.join(appMarketEntity.getTags()));
        appMarket.setCategory(appMarketEntity.getCategory());
        appMarket.setScenes(AppUtils.joinScenes(appMarketEntity.getScenes()));
        appMarket.setImages(AppUtils.join(appMarketEntity.getImages()));
        appMarket.setIcon(appMarketEntity.getIcon());
        appMarket.setFree(appMarketEntity.getFree());
        appMarket.setCost(appMarketEntity.getCost());
        appMarket.setUsageCount(appMarketEntity.getUsageCount());
        appMarket.setLikeCount(appMarketEntity.getLikeCount());
        appMarket.setViewCount(appMarketEntity.getViewCount());
        appMarket.setInstallCount(appMarketEntity.getInstallCount());
        appMarket.setDescription(appMarketEntity.getDescription());
        appMarket.setExample(appMarketEntity.getExample());
        appMarket.setDemo(appMarketEntity.getDemo());
        appMarket.setPluginList(JsonUtils.toJsonString(appMarketEntity.getPluginList()));
        appMarket.setOpenVideoMode(appMarketEntity.getOpenVideoMode());
        appMarket.setAudit(appMarketEntity.getAudit());
        appMarket.setDeleted(Boolean.FALSE);
        // 处理配置信息
        if (AppModelEnum.COMPLETION.name().equals(appMarket.getModel())) {
            WorkflowConfigEntity config = appMarketEntity.getWorkflowConfig();
            if (Objects.nonNull(config)) {
                appMarket.setConfig(JsonUtils.toJsonString(config));
            }
        } else if (AppModelEnum.CHAT.name().equals(appMarket.getModel())) {
            ChatConfigEntity config = appMarketEntity.getChatConfig();
            if (Objects.nonNull(config)) {
                appMarket.setConfig(JsonUtils.toJsonString(config));
            }
        } else if (AppModelEnum.IMAGE.name().equals(appMarket.getModel())) {
            ImageConfigEntity config = appMarketEntity.getImageConfig();
            if (Objects.nonNull(config)) {
                appMarket.setConfig(JsonUtils.toJsonString(config));
            }
        }
        return appMarket;
    }

    /**
     * AppPublishDO 转 AppMarketDO
     *
     * @param appPublish AppPublishDO
     * @return AppMarketDO
     */
    default AppMarketEntity convert(AppPublishDO appPublish) {
        AppMarketEntity appMarketEntity = new AppMarketEntity();
        String appInfo = appPublish.getAppInfo();
        if (StringUtils.isBlank(appInfo)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.PUBLISH_APP_INFO_NON_EXISTENT);
        }

        AppDO app = JsonUtils.parseObject(appInfo, AppDO.class);
        if (StringUtils.isBlank(app.getCategory())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_CATEGORY_NON_EXISTENT);
        }
        appMarketEntity.setUid(appPublish.getMarketUid());
        appMarketEntity.setName(app.getName());
        appMarketEntity.setModel(app.getModel());
        appMarketEntity.setType(appPublish.getType());
        appMarketEntity.setVersion(appPublish.getVersion());
        appMarketEntity.setLanguage(appPublish.getLanguage());
        appMarketEntity.setSort(appPublish.getSort());
        appMarketEntity.setCategory(appPublish.getCategory());
        appMarketEntity.setTags(AppUtils.split(app.getTags()));
        appMarketEntity.setScenes(AppUtils.splitScenes(app.getScenes()));
        appMarketEntity.setImages(AppUtils.split(app.getImages()));
        appMarketEntity.setIcon(app.getIcon());
        appMarketEntity.setFree(Boolean.TRUE);
        appMarketEntity.setCost(BigDecimal.ZERO);
        appMarketEntity.setUsageCount(0);
        appMarketEntity.setLikeCount(0);
        appMarketEntity.setViewCount(0);
        appMarketEntity.setInstallCount(0);
        appMarketEntity.setAudit(AppPublishAuditEnum.APPROVED.getCode());
        appMarketEntity.setDescription(app.getDescription());
        appMarketEntity.setExample(app.getExample());
        appMarketEntity.setDemo(app.getDemo());
        appMarketEntity.setPluginList(JsonUtils.parseArray(app.getPluginList(), String.class));
        // 处理配置信息
        if (StringUtils.isNotBlank(app.getConfig())) {
            if (AppModelEnum.COMPLETION.name().equals(app.getModel())) {
                appMarketEntity.setWorkflowConfig(JsonUtils.parseObject(app.getConfig(), WorkflowConfigEntity.class));
            } else if (AppModelEnum.CHAT.name().equals(app.getModel())) {
                appMarketEntity.setChatConfig(JsonUtils.parseObject(app.getConfig(), ChatConfigEntity.class));
            } else if (AppModelEnum.IMAGE.name().equals(app.getModel())) {
                appMarketEntity.setImageConfig(JsonUtils.parseObject(app.getConfig(), ImageConfigEntity.class));
            }
        }
        return appMarketEntity;
    }

    /**
     * AppMarketDO 转 AppMarketEntity
     *
     * @param appMarket AppMarketDO
     * @return AppMarketEntity
     */
    default AppMarketEntity convert(AppMarketDO appMarket) {
        AppMarketEntity appMarketEntity = new AppMarketEntity();
        appMarketEntity.setUid(appMarket.getUid());
        appMarketEntity.setName(appMarket.getName());
        appMarketEntity.setType(appMarket.getType());
        appMarketEntity.setModel(appMarket.getModel());
        appMarketEntity.setVersion(appMarket.getVersion());
        appMarketEntity.setLanguage(appMarket.getLanguage());
        appMarketEntity.setSort(appMarket.getSort());
        appMarketEntity.setCategory(appMarket.getCategory());
        appMarketEntity.setTags(AppUtils.split(appMarket.getTags()));
        appMarketEntity.setScenes(AppUtils.splitScenes(appMarket.getScenes()));
        appMarketEntity.setImages(AppUtils.split(appMarket.getImages()));
        appMarketEntity.setIcon(appMarket.getIcon());
        appMarketEntity.setFree(appMarket.getFree());
        appMarketEntity.setCost(appMarket.getCost());
        appMarketEntity.setUsageCount(appMarket.getUsageCount());
        appMarketEntity.setLikeCount(appMarket.getLikeCount());
        appMarketEntity.setViewCount(appMarket.getViewCount());
        appMarketEntity.setInstallCount(appMarket.getInstallCount());
        appMarketEntity.setDescription(appMarket.getDescription());
        appMarketEntity.setCreator(appMarket.getCreator());
        appMarketEntity.setUpdater(appMarket.getUpdater());
        appMarketEntity.setCreateTime(appMarket.getCreateTime());
        appMarketEntity.setUpdateTime(appMarket.getUpdateTime());
        appMarketEntity.setAudit(appMarket.getAudit());
        appMarketEntity.setExample(appMarket.getExample());
        appMarketEntity.setDemo(appMarket.getDemo());
        appMarketEntity.setPluginList(JsonUtils.parseArray(appMarket.getPluginList(), String.class));
        appMarketEntity.setTenantId(appMarket.getTenantId());
        // 处理配置信息
        if (StringUtils.isNotBlank(appMarket.getConfig())) {
            if (AppModelEnum.COMPLETION.name().equals(appMarket.getModel())) {
                appMarketEntity.setWorkflowConfig(JsonUtils.parseObject(appMarket.getConfig(), WorkflowConfigEntity.class));
            } else if (AppModelEnum.CHAT.name().equals(appMarket.getModel())) {
                appMarketEntity.setChatConfig(JsonUtils.parseObject(appMarket.getConfig(), ChatConfigEntity.class));
            } else if (AppModelEnum.IMAGE.name().equals(appMarket.getModel())) {
                appMarketEntity.setImageConfig(JsonUtils.parseObject(appMarket.getConfig(), ImageConfigEntity.class));
            }
        }
        return appMarketEntity;
    }

    /**
     * 请求转为entity
     *
     * @param appRequest 请求
     * @return entity
     */
    AppMarketEntity convert(AppReqVO appRequest);


    AppMarketEntity convert(AppUpdateReqVO appRequest);

    /**
     * 响应转为entity
     *
     * @param appRequest 请求
     * @return entity
     */
    AppMarketEntity convertEntity(AppMarketRespVO appResponse);

    /**
     * 响应转为entity
     *
     * @param appRequest 请求
     * @return entity
     */
    AppMarketRespVO convertResponse(AppMarketEntity entity);

    /**
     * 将返回转换为 AppRespVO
     *
     * @param appResponse
     * @return AppRespVO
     */
    AppMarketRespVO convert(AppRespVO appResponse);

    /**
     * 将返回转换为 AppReqVO
     *
     * @param response
     * @return AppReqVO
     */
    AppReqVO convert(AppMarketRespVO response);

    /**
     * AppMarketDO 转 AppMarketRespVO
     *
     * @param appMarket AppMarketDO
     * @return AppMarketRespVO
     */
    default AppMarketRespVO convertResponse(AppMarketDO appMarket) {
        AppMarketRespVO appMarketResponse = new AppMarketRespVO();
        appMarketResponse.setUid(appMarket.getUid());
        appMarketResponse.setName(appMarket.getName());
        appMarketResponse.setSpell(PinyinCache.get(appMarket.getName()));
        appMarketResponse.setSpellSimple(PinyinCache.getSimple(appMarket.getName()));
        appMarketResponse.setType(appMarket.getType());
        appMarketResponse.setModel(appMarket.getModel());
        appMarketResponse.setVersion(appMarket.getVersion());
        appMarketResponse.setLanguage(appMarket.getLanguage());
        appMarketResponse.setSort(appMarket.getSort());
        appMarketResponse.setCategory(appMarket.getCategory());
        appMarketResponse.setTags(AppUtils.split(appMarket.getTags()));
        appMarketResponse.setScenes(AppUtils.splitScenes(appMarket.getScenes()));
        appMarketResponse.setImages(AppUtils.split(appMarket.getImages()));
        appMarketResponse.setIcon(appMarket.getIcon());
        appMarketResponse.setFree(appMarket.getFree());
        appMarketResponse.setCost(appMarket.getCost());
        appMarketResponse.setUsageCount(appMarket.getUsageCount());
        appMarketResponse.setLikeCount(appMarket.getLikeCount());
        appMarketResponse.setViewCount(appMarket.getViewCount());
        appMarketResponse.setInstallCount(appMarket.getInstallCount());
        appMarketResponse.setDescription(appMarket.getDescription());
        appMarketResponse.setExample(appMarket.getExample());
        appMarketResponse.setDemo(appMarket.getDemo());
        appMarketResponse.setPluginList(JsonUtils.parseArray(appMarket.getPluginList(), String.class));
        appMarketResponse.setOpenVideoMode(appMarket.getOpenVideoMode());
        appMarketResponse.setCreateTime(appMarket.getCreateTime());
        appMarketResponse.setUpdateTime(appMarket.getUpdateTime());
        // 处理配置信息
        if (AppModelEnum.COMPLETION.name().equals(appMarket.getModel())) {
            WorkflowConfigRespVO config = JsonUtils.parseObject(appMarket.getConfig(), WorkflowConfigRespVO.class);
            if (Objects.nonNull(config)) {
                appMarketResponse.setWorkflowConfig(config);
                appMarketResponse.setStepCount(Optional.of(config).map(WorkflowConfigRespVO::getSteps).map(List::size).orElse(0));
            }
        } else if (AppModelEnum.CHAT.name().equals(appMarket.getModel())) {
            ChatConfigRespVO config = JsonUtils.parseObject(appMarket.getConfig(), ChatConfigRespVO.class);
            if (Objects.nonNull(config)) {
                appMarketResponse.setChatConfig(config);
            }
        } else if (AppModelEnum.IMAGE.name().equals(appMarket.getModel())) {
            ImageConfigRespVO config = JsonUtils.parseObject(appMarket.getConfig(), ImageConfigRespVO.class);
            if (Objects.nonNull(config)) {
                appMarketResponse.setImageConfig(config);
            }
        }
        appMarketResponse.supplementStepVariable(RecommendStepWrapperFactory.getStepVariable());
        return appMarketResponse;
    }

    /**
     * AppMarketDO 转 AppMarketRespVO
     *
     * @param appMarket AppMarketDO
     * @return AppMarketRespVO
     */
    default AppMarketRespVO convertResponseWithId(AppMarketDO appMarket) {
        AppMarketRespVO appMarketResponse = new AppMarketRespVO();
        appMarketResponse.setId(appMarket.getId());
        appMarketResponse.setUid(appMarket.getUid());
        appMarketResponse.setName(appMarket.getName());
        appMarketResponse.setSpell(PinyinCache.get(appMarket.getName()));
        appMarketResponse.setSpellSimple(PinyinCache.getSimple(appMarket.getName()));
        appMarketResponse.setType(appMarket.getType());
        appMarketResponse.setModel(appMarket.getModel());
        appMarketResponse.setVersion(appMarket.getVersion());
        appMarketResponse.setLanguage(appMarket.getLanguage());
        appMarketResponse.setSort(appMarket.getSort());
        appMarketResponse.setCategory(appMarket.getCategory());
        appMarketResponse.setTags(AppUtils.split(appMarket.getTags()));
        appMarketResponse.setScenes(AppUtils.splitScenes(appMarket.getScenes()));
        appMarketResponse.setImages(AppUtils.split(appMarket.getImages()));
        appMarketResponse.setIcon(appMarket.getIcon());
        appMarketResponse.setFree(appMarket.getFree());
        appMarketResponse.setCost(appMarket.getCost());
        appMarketResponse.setUsageCount(appMarket.getUsageCount());
        appMarketResponse.setLikeCount(appMarket.getLikeCount());
        appMarketResponse.setViewCount(appMarket.getViewCount());
        appMarketResponse.setInstallCount(appMarket.getInstallCount());
        appMarketResponse.setDescription(appMarket.getDescription());
        appMarketResponse.setExample(appMarket.getExample());
        appMarketResponse.setDemo(appMarket.getDemo());
        appMarketResponse.setPluginList(JsonUtils.parseArray(appMarket.getPluginList(), String.class));
        appMarketResponse.setOpenVideoMode(appMarket.getOpenVideoMode());
        appMarketResponse.setCreateTime(appMarket.getCreateTime());
        appMarketResponse.setUpdateTime(appMarket.getUpdateTime());
        // 处理配置信息
        if (AppModelEnum.COMPLETION.name().equals(appMarket.getModel())) {
            WorkflowConfigRespVO config = JsonUtils.parseObject(appMarket.getConfig(), WorkflowConfigRespVO.class);
            if (Objects.nonNull(config)) {
                appMarketResponse.setWorkflowConfig(config);
                appMarketResponse.setStepCount(Optional.of(config).map(WorkflowConfigRespVO::getSteps).map(List::size).orElse(0));
            }
        } else if (AppModelEnum.CHAT.name().equals(appMarket.getModel())) {
            ChatConfigRespVO config = JsonUtils.parseObject(appMarket.getConfig(), ChatConfigRespVO.class);
            if (Objects.nonNull(config)) {
                appMarketResponse.setChatConfig(config);
            }
        } else if (AppModelEnum.IMAGE.name().equals(appMarket.getModel())) {
            ImageConfigRespVO config = JsonUtils.parseObject(appMarket.getConfig(), ImageConfigRespVO.class);
            if (Objects.nonNull(config)) {
                appMarketResponse.setImageConfig(config);
            }
        }
        appMarketResponse.supplementStepVariable(RecommendStepWrapperFactory.getStepVariable());
        return appMarketResponse;
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

    List<AppRespVO> convert(List<AppMarketDO> appMarketDOS);

    default List<String> strToListStr(String str) {
        if (StringUtils.isBlank(str)) {
            return Lists.newArrayList();
        }
        return StringUtil.toList(str);
    }

}
