package com.starcloud.ops.business.app.convert;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.template.dto.TemplateConfigDTO;
import com.starcloud.ops.business.app.api.template.dto.TemplateDTO;
import com.starcloud.ops.business.app.api.template.request.TemplateRequest;
import com.starcloud.ops.business.app.api.template.request.TemplateUpdateRequest;
import com.starcloud.ops.business.app.dal.databoject.template.TemplateDO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.AppResultCode;
import com.starcloud.ops.business.app.exception.TemplateException;
import com.starcloud.ops.business.app.util.TemplateUtil;
import com.starcloud.ops.business.app.validate.TemplateValidate;
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
public class TemplateConvert {

    /**
     * 将 DO 转换为 DTO
     *
     * @param templateDO 模版 DO
     * @return 模版 DTO
     */
    public static TemplateDTO convert(TemplateDO templateDO) {
        Assert.notNull(templateDO, () -> TemplateException.exception(AppResultCode.TEMPLATE_DATA_IS_NULL, "TemplateDO"));
        TemplateDTO template = new TemplateDTO();
        // ID 为 null, 不透传给前端
        template.setId(null);
        template.setUid(templateDO.getUid());
        template.setName(templateDO.getName());
        template.setType(templateDO.getType());
        template.setLogotype(templateDO.getLogotype());
        template.setSourceType(templateDO.getSourceType());
        template.setMarketUid(templateDO.getMarketUid());
        template.setVersion(Optional.ofNullable(templateDO.getVersion()).orElse(AppConstants.DEFAULT_VERSION));
        template.setTags(TemplateUtil.buildField(templateDO.getTags()));
        template.setCategories(TemplateUtil.buildField(templateDO.getCategories()));
        template.setScenes(TemplateUtil.buildScenes(templateDO.getScenes()));
        template.setConfig(JSON.parseObject(templateDO.getConfig(), TemplateConfigDTO.class));
        template.setImages(TemplateUtil.buildField(templateDO.getImages()));
        template.setIcon(templateDO.getIcon());
        template.setStepIcons(TemplateUtil.buildField(templateDO.getStepIcons()));
        template.setDescription(templateDO.getDescription());
        template.setStatus(templateDO.getStatus());
        template.setDeleted(templateDO.getDeleted());
        template.setCreator(templateDO.getCreator());
        template.setCreateTime(templateDO.getCreateTime());
        template.setUpdater(templateDO.getUpdater());
        template.setUpdateTime(templateDO.getUpdateTime());
        template.setLastUpload(templateDO.getLastUpload());
        template.setTenantId(templateDO.getTenantId());
        return template;
    }

    /**
     * 将请求转换为 DO
     *
     * @param request 请求
     * @return 模版 DO
     */
    public static TemplateDO convertCreate(TemplateRequest request) {
        // 基础校验和数据处理
        Assert.notNull(request, () -> TemplateException.exception(AppResultCode.TEMPLATE_DATA_IS_NULL, "TemplateRequest"));
        String name = TemplateValidate.validateName(request.getName());
        String type = TemplateValidate.validateType(request.getType());
        String logotype = TemplateValidate.validateLogotype(request.getLogotype());
        String sourceType = TemplateValidate.validateSourceType(request.getSourceType());
        TemplateConfigDTO config = TemplateValidate.validateConfig(request.getConfig());
        String tags = TemplateUtil.buildField(request.getTags());
        String categories = TemplateUtil.buildField(request.getCategories());
        String scenes = TemplateUtil.buildScenes(request.getScenes());

        TemplateDO template = new TemplateDO();
        template.setName(name);
        template.setType(type);
        template.setLogotype(logotype);
        template.setSourceType(sourceType);
        template.setVersion(Optional.ofNullable(request.getVersion()).orElse(AppConstants.DEFAULT_VERSION));
        template.setTags(tags);
        template.setCategories(categories);
        template.setScenes(scenes);
        template.setImages(TemplateUtil.buildField(request.getImages()));
        template.setIcon(request.getIcon());
        template.setStepIcons(TemplateUtil.buildStepIcons(config));
        template.setDescription(request.getDescription());
        template.setStatus(StateEnum.ENABLE.getCode());
        template.setDeleted(Boolean.FALSE);

        // 保证 config 中的一些数据和 template 中的一致
        config.setType(type);
        config.setLogotype(logotype);
        config.setSourceType(sourceType);
        config.setTags(TemplateUtil.buildField(tags));
        config.setCategories(TemplateUtil.buildField(categories));
        config.setScenes(TemplateUtil.buildScenes(scenes));
        template.setConfig(JSON.toJSONString(config));

        return template;

    }

    /**
     * 将更新请求转换为 DO
     *
     * @param request 更新请求
     * @return 模版 DO
     */
    public static TemplateDO convertModify(TemplateUpdateRequest request) {
        Assert.notNull(request, () -> TemplateException.exception(AppResultCode.TEMPLATE_DATA_IS_NULL, "TemplateUpdateRequest"));
        Assert.notBlank(request.getUid(), () -> TemplateException.exception(AppResultCode.TEMPLATE_UID_IS_REQUIRED));
        return convertCreate(request);
    }

}
