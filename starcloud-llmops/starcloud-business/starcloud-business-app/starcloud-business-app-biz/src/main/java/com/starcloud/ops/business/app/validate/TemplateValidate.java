package com.starcloud.ops.business.app.validate;

import com.starcloud.ops.business.app.api.template.dto.TemplateConfigDTO;
import com.starcloud.ops.business.app.enums.AppResultCode;
import com.starcloud.ops.business.app.enums.template.TemplateLogotypeEnum;
import com.starcloud.ops.business.app.enums.template.TemplateSourceTypeEnum;
import com.starcloud.ops.business.app.enums.template.TemplateTypeEnum;
import com.starcloud.ops.business.app.exception.TemplateException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-06
 */
public class TemplateValidate {

    /**
     * 校验模版名称
     *
     * @param name 模版名称
     */
    public static String validateName(String name) {
        if (StringUtils.isBlank(name)) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_FIELD_IS_REQUIRED, "name");
        }
        return name.trim();
    }

    /**
     * 校验模版类型
     *
     * @param type 模版类型
     */
    public static String validateType(String type) {
        if (StringUtils.isBlank(type)) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_FIELD_IS_REQUIRED, "type");
        }
        type = type.trim();
        try {
            TemplateTypeEnum.getEnumByName(type);
        } catch (Exception e) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_FIELD_NOT_SUPPORT, "type", type);
        }
        return type;
    }

    /**
     * 校验模版标识
     *
     * @param logotype 模版标识
     * @return 模版标识
     */
    public static String validateLogotype(String logotype) {
        if (StringUtils.isBlank(logotype)) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_FIELD_IS_REQUIRED, "logotype");
        }
        logotype = logotype.trim();
        try {
            TemplateLogotypeEnum.getEnumByName(logotype);
        } catch (Exception e) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_FIELD_NOT_SUPPORT, "logotype", logotype);
        }
        return logotype;
    }

    /**
     * 校验模版来源
     *
     * @param sourceType 模版来源
     * @return 模版来源
     */
    public static String validateSourceType(String sourceType) {
        if (StringUtils.isBlank(sourceType)) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_FIELD_IS_REQUIRED, "sourceType");
        }
        sourceType = sourceType.trim();
        try {
            TemplateSourceTypeEnum.getEnumByName(sourceType);
        } catch (Exception e) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_FIELD_NOT_SUPPORT, "sourceType", sourceType);
        }
        return sourceType;
    }

    /**
     * 校验模版配置
     *
     * @param config 模版配置
     * @return 模版配置
     */
    public static TemplateConfigDTO validateConfig(TemplateConfigDTO config) {
        if (config == null) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_FIELD_IS_REQUIRED, "config");
        }
        return config;
    }
}
