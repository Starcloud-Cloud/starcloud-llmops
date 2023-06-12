package com.starcloud.ops.business.app.convert;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.app.dto.AppConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppDTO;
import com.starcloud.ops.business.app.api.app.request.AppRequest;
import com.starcloud.ops.business.app.api.app.request.AppUpdateRequest;
import com.starcloud.ops.business.app.dal.databoject.AppDO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.AppResultCode;
import com.starcloud.ops.business.app.exception.AppException;
import com.starcloud.ops.business.app.util.AppUtil;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import lombok.experimental.UtilityClass;

import java.util.Optional;

/**
 * 模版转换类
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
        Assert.notNull(appDO, () -> AppException.exception(AppResultCode.TEMPLATE_DATA_IS_NULL, "AppDO"));
        AppDTO appDTO = new AppDTO();
        // ID 为 null, 不透传给前端
        appDTO.setId(null);
        appDTO.setUid(appDO.getUid());
        appDTO.setName(appDO.getName());
        appDTO.setType(appDO.getType());
        appDTO.setLogotype(appDO.getLogotype());
        appDTO.setSourceType(appDO.getSourceType());
        appDTO.setUploadUid(appDO.getUploadUid());
        appDTO.setDownloadUid(appDO.getDownloadUid());
        appDTO.setVersion(Optional.ofNullable(appDO.getVersion()).orElse(AppConstants.DEFAULT_VERSION));
        appDTO.setTags(AppUtil.buildField(appDO.getTags()));
        appDTO.setCategories(AppUtil.buildField(appDO.getCategories()));
        appDTO.setScenes(AppUtil.buildScenes(appDO.getScenes()));
        appDTO.setConfig(JSON.parseObject(appDO.getConfig(), AppConfigDTO.class));
        appDTO.setImages(AppUtil.buildField(appDO.getImages()));
        appDTO.setIcon(appDO.getIcon());
        appDTO.setStepIcons(AppUtil.buildField(appDO.getStepIcons()));
        appDTO.setDescription(appDO.getDescription());
        appDTO.setStatus(appDO.getStatus());
        appDTO.setDeleted(appDO.getDeleted());
        appDTO.setCreator(appDO.getCreator());
        appDTO.setCreateTime(appDO.getCreateTime());
        appDTO.setUpdater(appDO.getUpdater());
        appDTO.setUpdateTime(appDO.getUpdateTime());
        appDTO.setLastUpload(appDO.getLastUpload());
        appDTO.setTenantId(appDO.getTenantId());
        return appDTO;
    }

    /**
     * 将请求转换为 DO
     *
     * @param request 请求
     * @return 模版 DO
     */
    public static AppDO convertCreate(AppRequest request) {
        // 基础校验和数据处理
        Assert.notNull(request, () -> AppException.exception(AppResultCode.TEMPLATE_DATA_IS_NULL, "TemplateRequest"));
        String name = AppValidate.validateName(request.getName());
        String type = AppValidate.validateType(request.getType());
        String logotype = AppValidate.validateLogotype(request.getLogotype());
        String sourceType = AppValidate.validateSourceType(request.getSourceType());
        AppConfigDTO config = AppValidate.validateConfig(request.getConfig());
        String tags = AppUtil.buildField(request.getTags());
        String categories = AppUtil.buildField(request.getCategories());
        String scenes = AppUtil.buildScenes(request.getScenes());

        AppDO template = new AppDO();
        template.setName(name);
        template.setType(type);
        template.setLogotype(logotype);
        template.setSourceType(sourceType);
        template.setVersion(Optional.ofNullable(request.getVersion()).orElse(AppConstants.DEFAULT_VERSION));
        template.setTags(tags);
        template.setCategories(categories);
        template.setScenes(scenes);
        template.setImages(AppUtil.buildField(request.getImages()));
        template.setIcon(request.getIcon());
        template.setStepIcons(AppUtil.buildStepIcons(config));
        template.setDescription(request.getDescription());
        template.setStatus(StateEnum.ENABLE.getCode());
        template.setDeleted(Boolean.FALSE);

        // 保证 config 中的一些数据和 template 中的一致
        config.setType(type);
        config.setLogotype(logotype);
        config.setSourceType(sourceType);
        config.setTags(AppUtil.buildField(tags));
        config.setCategories(AppUtil.buildField(categories));
        config.setScenes(AppUtil.buildScenes(scenes));
        template.setConfig(JSON.toJSONString(config));

        return template;

    }

    /**
     * 将更新请求转换为 DO
     *
     * @param request 更新请求
     * @return 模版 DO
     */
    public static AppDO convertModify(AppUpdateRequest request) {
        Assert.notNull(request, () -> AppException.exception(AppResultCode.TEMPLATE_DATA_IS_NULL, "TemplateUpdateRequest"));
        Assert.notBlank(request.getUid(), () -> AppException.exception(AppResultCode.TEMPLATE_UID_IS_REQUIRED));
        return convertCreate(request);
    }

}
