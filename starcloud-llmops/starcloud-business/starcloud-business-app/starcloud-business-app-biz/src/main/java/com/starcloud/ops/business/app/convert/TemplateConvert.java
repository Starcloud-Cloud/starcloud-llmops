package com.starcloud.ops.business.app.convert;

import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.template.dto.TemplateConfigDTO;
import com.starcloud.ops.business.app.api.template.dto.TemplateDTO;
import com.starcloud.ops.business.app.api.template.request.TemplateRequest;
import com.starcloud.ops.business.app.api.template.request.TemplateUpdateRequest;
import com.starcloud.ops.business.app.dal.databoject.template.TemplateDO;
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
    public static TemplateDO convertCreate(TemplateRequest request) {

        TemplateDO template = new TemplateDO();
        // 雪花算法生成 uid
        template.setUid("");
        template.setName(request.getName());
        template.setType(request.getType());
        template.setLogotype(request.getLogotype());
        template.setSourceType(request.getSourceType());
        template.setTags(StringUtils.join(request.getTags(), ","));
        template.setCategories(StringUtils.join(request.getCategories(), ","));
        template.setScenes(StringUtils.join(request.getScenes(), ","));

        // 保证 config 中的一些数据和 template 中的一致
        TemplateConfigDTO config = request.getConfig();
        config.setType(request.getType());
        config.setLogotype(request.getLogotype());
        config.setSourceType(request.getSourceType());
        config.setTags(request.getTags());
        config.setCategories(request.getCategories());
        config.setScenes(request.getScenes());

        template.setConfig(JSON.toJSONString(config));
        template.setImages(StringUtils.join(request.getImages(), ","));
        template.setIcon(request.getIcon());
        List<String> stepIcons = config.getSteps().stream().map(stepWrapper -> stepWrapper.getStep().getIcon()).collect(Collectors.toList());
        template.setStepIcons(StringUtils.join(stepIcons, ","));
        template.setDescription(request.getDescription());
        template.setStatus(0);
        template.setDeleted(false);

        return template;

    }

    public static TemplateDO convertModify(TemplateUpdateRequest request) {
        TemplateDO template = new TemplateDO();


        return template;
    }

}
