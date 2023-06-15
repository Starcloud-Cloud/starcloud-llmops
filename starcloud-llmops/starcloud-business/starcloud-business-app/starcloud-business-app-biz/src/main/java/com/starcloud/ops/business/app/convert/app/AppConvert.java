package com.starcloud.ops.business.app.convert.app;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.app.dto.AppChatConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppDTO;
import com.starcloud.ops.business.app.api.app.request.AppPublishRequest;
import com.starcloud.ops.business.app.api.app.request.AppRequest;
import com.starcloud.ops.business.app.api.app.request.AppUpdateRequest;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import lombok.experimental.UtilityClass;

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
        appDTO.setImages(AppUtils.split(appDO.getImages()));
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
        appDO.setImages(AppUtils.join(request.getImages()));
        appDO.setIcon(request.getIcon());
        appDO.setDescription(request.getDescription());
        appDO.setStatus(StateEnum.ENABLE.getCode());
        appDO.setDeleted(Boolean.FALSE);
        if (AppModelEnum.COMPLETION.name().equals(request.getModel())) {
            AppConfigDTO config = request.getConfig();
            config.setType(request.getType());
            config.setSource(request.getSource());
            config.setTags(request.getTags());
            config.setCategories(request.getCategories());
            config.setScenes(request.getScenes());
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

}
