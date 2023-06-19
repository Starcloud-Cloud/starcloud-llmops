package com.starcloud.ops.business.app.convert.app;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.app.dto.AppCategoryDTO;
import com.starcloud.ops.business.app.api.app.dto.AppChatConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppDTO;
import com.starcloud.ops.business.app.api.app.dto.CategoryRemark;
import com.starcloud.ops.business.app.api.app.request.AppPublishRequest;
import com.starcloud.ops.business.app.api.app.request.AppRequest;
import com.starcloud.ops.business.app.api.app.request.AppUpdateRequest;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * 我的应用转换类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@UtilityClass
public class AppConvert {

    /**
     * 将 DO 转换为 DTO
     *
     * @param appDO 模版 DO
     * @return 模版 DTO
     */
    public static AppDTO convert(AppDO appDO) {
        AppValidate.assertNotNull(appDO, "app Data");
        AppDTO appDTO = new AppDTO();
        appDTO.setUid(appDO.getUid());
        appDTO.setName(appDO.getName());
        appDTO.setModel(appDO.getModel());
        appDTO.setType(appDO.getType());
        appDTO.setSource(appDO.getSource());
        appDTO.setTags(AppUtils.split(appDO.getTags()));
        appDTO.setCategories(AppUtils.split(appDO.getCategories()));
        appDTO.setScenes(AppUtils.splitScenes(appDO.getScenes()));
        appDTO.setImages(null);
        appDTO.setIcon(appDO.getIcon());
        appDTO.setStepIcons(StringUtil.toList(appDO.getStepIcons()));
        appDTO.setDescription(appDO.getDescription());
        appDTO.setUploadUid(appDO.getUploadUid());
        appDTO.setDownloadUid(appDO.getDownloadUid());
        appDTO.setStatus(appDO.getStatus());
        appDTO.setDeleted(appDO.getDeleted());
        appDTO.setCreator(appDO.getCreator());
        appDTO.setCreateTime(appDO.getCreateTime());
        appDTO.setUpdater(appDO.getUpdater());
        appDTO.setUpdateTime(appDO.getUpdateTime());
        appDTO.setLastUpload(appDO.getLastUpload());
        appDTO.setTenantId(appDO.getTenantId());
        // 配置信息处理
        if (AppModelEnum.COMPLETION.name().equals(appDO.getModel())) {
            appDTO.setConfig(JSON.parseObject(appDO.getConfig(), AppConfigDTO.class));
        } else if (AppModelEnum.CHAT.name().equals(appDO.getModel())) {
            appDTO.setChatConfig(JSON.parseObject(appDO.getConfig(), AppChatConfigDTO.class));
        } else {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MODEL_IS_UNKNOWN, appDO.getModel());
        }
        return appDTO;
    }

    /**
     * 将请求转换为 DO
     *
     * @param request 请求
     * @return 模版 DO
     */
    public static AppDO convertCreate(AppRequest request) {
        AppValidate.validate(request);
        AppUtils.buildRequest(request);
        AppDO appDO = new AppDO();
        appDO.setName(request.getName());
        appDO.setType(request.getType());
        appDO.setSource(request.getSource());
        appDO.setTags(AppUtils.join(request.getTags()));
        appDO.setCategories(AppUtils.join(request.getCategories()));
        appDO.setScenes(AppUtils.joinScenes(request.getScenes()));
        appDO.setImages(null);
        appDO.setIcon(request.getIcon());
        appDO.setDescription(request.getDescription());
        appDO.setStatus(StateEnum.ENABLE.getCode());
        appDO.setDeleted(Boolean.FALSE);
        if (AppModelEnum.COMPLETION.name().equals(request.getModel())) {
            AppConfigDTO config = request.getConfig();
            appDO.setConfig(JSON.toJSONString(config));
            appDO.setStepIcons(AppUtils.buildStepIcons(config));
        } else if (AppModelEnum.CHAT.name().equals(request.getModel())) {
            AppChatConfigDTO chatConfig = request.getChatConfig();
            appDO.setConfig(JSON.toJSONString(chatConfig));
        } else {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MODEL_IS_UNKNOWN, request.getModel());
        }

        return appDO;

    }

    /**
     * 将更新请求转换为 DO
     *
     * @param request 更新请求
     * @return 模版 DO
     */
    public static AppDO convertModify(AppUpdateRequest request) {
        AppValidate.assertNotNull(request, "App Update Request Data");
        AppDO appDO = convertCreate(request);
        appDO.setStatus(null);
        appDO.setDeleted(null);
        return appDO;
    }

    /**
     * 将发布请求转换为 DO
     *
     * @param request 发布请求
     * @return 模版 DO
     */
    public static AppDO convertPublish(AppPublishRequest request) {
        AppValidate.assertNotNull(request, "App Publish Request Data");
        AppDO appDO = convertCreate(request);
        appDO.setStatus(null);
        appDO.setDeleted(null);
        return appDO;
    }

    /**
     * 将 DO 转换为 DTO
     *
     * @param market 应用市场
     * @return App 请求
     */
    public static AppDO convertInsert(AppMarketDO market) {
        AppRequest request = new AppRequest();
        request.setName(market.getName());
        request.setModel(market.getModel());
        request.setType(AppTypeEnum.DOWNLOAD.name());
        request.setSource(AppSourceEnum.WEB.name());
        request.setTags(AppUtils.split(market.getTags()));
        request.setCategories(AppUtils.split(market.getCategories()));
        request.setScenes(AppUtils.splitScenes(market.getScenes()));
        request.setImages(null);
        request.setIcon(market.getIcon());
        request.setDescription(market.getDescription());
        if (AppModelEnum.COMPLETION.name().equals(market.getModel())) {
            request.setConfig(JSON.parseObject(market.getConfig(), AppConfigDTO.class));
        } else if (AppModelEnum.CHAT.name().equals(market.getModel())) {
            request.setChatConfig(JSON.parseObject(market.getConfig(), AppChatConfigDTO.class));
        } else {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MODEL_IS_UNKNOWN, market.getModel());
        }
        return convertCreate(request);
    }

    /**
     * 将 DictDataDO 转换为 AppCategoryDTO
     *
     * @param dict 字典数据
     * @return AppCategoryDTO
     */
    public static AppCategoryDTO convertCategory(DictDataDO dict) {
        String remark = dict.getRemark();
        if (StringUtils.isBlank(remark)) {
            return null;
        }
        CategoryRemark categoryRemark = JSON.parseObject(remark, CategoryRemark.class);
        if (categoryRemark == null) {
            return null;
        }

        AppCategoryDTO category = new AppCategoryDTO();
        category.setCode(dict.getValue());
        category.setSort(dict.getSort());
        category.setIcon(categoryRemark.getIcon());
        category.setImage(categoryRemark.getImage());
        Locale locale = LocaleContextHolder.getLocale();
        if (locale.equals(Locale.SIMPLIFIED_CHINESE)) {
            category.setName(categoryRemark.getLabelZh());
            category.setDescription(categoryRemark.getDescriptionZh());
        } else {
            category.setName(categoryRemark.getLabelEn());
            category.setDescription(categoryRemark.getDescriptionEn());
        }
        return category;
    }

}
