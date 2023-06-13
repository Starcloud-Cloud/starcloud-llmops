package com.starcloud.ops.business.app.convert.app;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.app.dto.AppChatConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppDTO;
import com.starcloud.ops.business.app.api.app.request.AppRequest;
import com.starcloud.ops.business.app.api.app.request.AppUpdateRequest;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
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
        Assert.notNull(appDO, () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_DATA_IS_NULL, "AppDO"));
        AppDTO appDTO = new AppDTO();
        // ID 为 null, 不透传给前端
        appDTO.setId(null);
        appDTO.setUid(appDO.getUid());
        appDTO.setName(appDO.getName());
        appDTO.setModel(appDO.getModel());
        appDTO.setType(appDO.getType());
        appDTO.setLogotype(appDO.getLogotype());
        appDTO.setSourceType(appDO.getSourceType());
        appDTO.setUploadUid(appDO.getUploadUid());
        appDTO.setDownloadUid(appDO.getDownloadUid());
        appDTO.setVersion(Optional.ofNullable(appDO.getVersion()).orElse(AppConstants.DEFAULT_VERSION));
        appDTO.setTags(AppUtil.buildField(appDO.getTags()));
        appDTO.setCategories(AppUtil.buildField(appDO.getCategories()));
        appDTO.setScenes(AppUtil.buildScenes(appDO.getScenes()));
        if (AppModelEnum.COMPLETION.name().equals(appDO.getModel())) {
            appDTO.setConfig(JSON.parseObject(appDO.getConfig(), AppConfigDTO.class));
        } else {
            appDTO.setChatConfig(JSON.parseObject(appDO.getConfig(), AppChatConfigDTO.class));
        }
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
        Assert.notNull(request, () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_DATA_IS_NULL, "AppRequest"));
        String name = AppValidate.validateName(request.getName());
        String model = AppValidate.validateModel(request.getModel());
        String type = AppValidate.validateType(request.getType());
        String logotype = AppValidate.validateLogotype(request.getLogotype());
        String sourceType = AppValidate.validateSourceType(request.getSourceType());
        String tags = AppUtil.buildField(request.getTags());
        String categories = AppUtil.buildField(request.getCategories());
        String scenes = AppUtil.buildScenes(request.getScenes());

        AppDO appDO = new AppDO();
        appDO.setName(name);
        appDO.setType(type);
        appDO.setLogotype(logotype);
        appDO.setSourceType(sourceType);
        appDO.setVersion(Optional.ofNullable(request.getVersion()).orElse(AppConstants.DEFAULT_VERSION));
        appDO.setTags(tags);
        appDO.setCategories(categories);
        appDO.setScenes(scenes);
        appDO.setImages(AppUtil.buildField(request.getImages()));
        appDO.setIcon(request.getIcon());
        appDO.setDescription(request.getDescription());
        appDO.setStatus(StateEnum.ENABLE.getCode());
        appDO.setDeleted(Boolean.FALSE);
        if (AppModelEnum.COMPLETION.name().equals(model)) {
            AppConfigDTO config = AppValidate.validateConfig(request.getConfig());
            // 生成模式应用
            config.setType(type);
            config.setLogotype(logotype);
            config.setSourceType(sourceType);
            config.setTags(AppUtil.buildField(tags));
            config.setCategories(AppUtil.buildField(categories));
            config.setScenes(AppUtil.buildScenes(scenes));
            appDO.setConfig(JSON.toJSONString(config));
            appDO.setStepIcons(AppUtil.buildStepIcons(config));
        } else if (AppModelEnum.CHAT.name().equals(model)) {
            // 聊天模式
            AppChatConfigDTO chatConfig = AppValidate.validateChatConfig(request.getChatConfig());
            appDO.setConfig(JSON.toJSONString(chatConfig));
        } else {
            // 未知模式
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MODEL_IS_UNKNOWN, model);
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
        Assert.notNull(request, () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_DATA_IS_NULL, "AppUpdateRequest"));
        Assert.notBlank(request.getUid(), () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_IS_REQUIRED, "uid"));
        return convertCreate(request);
    }

}
