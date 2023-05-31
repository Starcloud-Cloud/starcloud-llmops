package com.starcloud.ops.business.app.convert;

import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.dto.TemplateConfigDTO;
import com.starcloud.ops.business.app.api.dto.TemplateDTO;
import com.starcloud.ops.business.app.api.request.TemplateRequest;
import com.starcloud.ops.business.app.api.request.TemplateUpdateRequest;
import com.starcloud.ops.business.app.dal.databoject.TemplateDO;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
     * 将 DTO 转换为 DO
     *
     * @param template 模版 DTO
     * @return 模版 DO
     */
    public static TemplateDO convert(TemplateDTO template) {
        TemplateDO templateDO = new TemplateDO();
        templateDO.setId(template.getId());
        templateDO.setUid(template.getUid());
        templateDO.setName(template.getName());
        templateDO.setType(template.getType());
        templateDO.setLogotype(template.getLogotype());
        templateDO.setSourceType(template.getSourceType());
        templateDO.setMarketKey(template.getMarketKey());
        templateDO.setVersion(template.getVersion());
        templateDO.setTags(StringUtils.join(template.getTags(), ","));
        templateDO.setCategories(StringUtils.join(template.getCategories(), ","));
        templateDO.setScenes(StringUtils.join(template.getScenes(), ","));

        templateDO.setConfig(JSON.toJSONString(buildConfig(template)));
        templateDO.setImages(StringUtils.join(template.getImages(), ","));
        templateDO.setIcon(template.getIcon());
        templateDO.setStepIcons(StringUtils.join(template.getStepIcons(), ","));
        templateDO.setDescription(template.getDescription());
        templateDO.setTenantId(template.getTenantId());
        return templateDO;
    }

    /**
     * 将 DO 转换为 DTO
     *
     * @param templateDO 模版 DO
     * @return 模版 DTO
     */
    public static TemplateDTO convert(TemplateDO templateDO) {
        TemplateDTO template = new TemplateDTO();
        template.setId(templateDO.getId());
        template.setUid(templateDO.getUid());
        template.setName(templateDO.getName());
        template.setType(templateDO.getType());
        template.setLogotype(templateDO.getLogotype());
        template.setSourceType(templateDO.getSourceType());
        template.setMarketKey(templateDO.getMarketKey());
        template.setVersion(templateDO.getVersion());
        template.setTags(Arrays.asList(StringUtils.split(templateDO.getTags(), ",")));
        template.setCategories(Arrays.asList(StringUtils.split(templateDO.getCategories(), ",")));
        template.setScenes(Arrays.asList(StringUtils.split(templateDO.getScenes(), ",")));
        template.setConfig(JSON.parseObject(templateDO.getConfig(), TemplateConfigDTO.class));
        template.setImages(Arrays.asList(StringUtils.split(templateDO.getImages(), ",")));
        template.setIcon(templateDO.getIcon());
        template.setStepIcons(Arrays.asList(StringUtils.split(templateDO.getStepIcons(), ",")));
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
    public static TemplateDO convert(TemplateRequest request) {

        TemplateDO templateDO = new TemplateDO();
        if (request instanceof TemplateUpdateRequest) {
            templateDO.setId(((TemplateUpdateRequest) request).getId());
        }
        // 雪花算法生成 uid
        templateDO.setUid("");
        templateDO.setName(request.getName());
        templateDO.setType(request.getType());
        templateDO.setLogotype(request.getLogotype());
        templateDO.setSourceType(request.getSourceType());
        templateDO.setTags(StringUtils.join(request.getTags(), ","));
        templateDO.setCategories(StringUtils.join(request.getCategories(), ","));
        templateDO.setScenes(StringUtils.join(request.getScenes(), ","));

        // 保证 config 中的一些数据和 template 中的一致
        TemplateConfigDTO config = request.getConfig();
        config.setType(request.getType());
        config.setLogotype(request.getLogotype());
        config.setSourceType(request.getSourceType());
        config.setTags(request.getTags());
        config.setCategories(request.getCategories());
        config.setScenes(request.getScenes());

        templateDO.setConfig(JSON.toJSONString(config));
        templateDO.setImages(StringUtils.join(request.getImages(), ","));
        templateDO.setIcon(request.getIcon());
        List<String> stepIcons = config.getSteps().stream().map(stepWrapper -> stepWrapper.getStep().getIcon()).collect(Collectors.toList());
        templateDO.setStepIcons(StringUtils.join(stepIcons, ","));
        templateDO.setDescription(request.getDescription());
        templateDO.setStatus(0);
        templateDO.setDeleted(false);

        return templateDO;

    }

    public static TemplateConfigDTO buildConfig(TemplateDTO template) {
        // 保证 config 中的一些数据和 template 中的一致
        TemplateConfigDTO config = template.getConfig();
        config.setType(template.getType());
        config.setLogotype(template.getLogotype());
        config.setSourceType(template.getSourceType());
        config.setTags(template.getTags());
        config.setCategories(template.getCategories());
        config.setScenes(template.getScenes());

        return config;
    }

}
