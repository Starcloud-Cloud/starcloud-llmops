package com.starcloud.ops.business.app.validate;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.app.dto.AppChatConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppConfigDTO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppLogotypeEnum;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-06
 */
public class AppValidate {

    /**
     * 校验应用名称
     *
     * @param name 应用名称
     */
    public static String validateName(String name) {
        if (StringUtils.isBlank(name)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_IS_REQUIRED, "name");
        }
        return name.trim();
    }

    /**
     * 校验应用模型
     *
     * @param model 应用模型
     * @return 应用模型
     */
    public static String validateModel(String model) {
        if (StringUtils.isBlank(model)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_IS_REQUIRED, "model");
        }
        model = model.trim();
        try {
            AppModelEnum.getEnumByName(model);
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_NOT_SUPPORT, "model", model);
        }
        return model;
    }

    /**
     * 校验应用类型
     *
     * @param type 应用类型
     */
    public static String validateType(String type) {
        if (StringUtils.isBlank(type)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_IS_REQUIRED, "type");
        }
        type = type.trim();
        try {
            AppTypeEnum.getEnumByName(type);
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_NOT_SUPPORT, "type", type);
        }
        return type;
    }

    /**
     * 校验应用标识
     *
     * @param logotype 应用标识
     * @return 应用标识
     */
    public static String validateLogotype(String logotype) {
        if (StringUtils.isBlank(logotype)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_IS_REQUIRED, "logotype");
        }
        logotype = logotype.trim();
        try {
            AppLogotypeEnum.getEnumByName(logotype);
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_NOT_SUPPORT, "logotype", logotype);
        }
        return logotype;
    }

    /**
     * 校验应用来源
     *
     * @param sourceType 应用来源
     * @return 应用来源
     */
    public static String validateSourceType(String sourceType) {
        if (StringUtils.isBlank(sourceType)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_IS_REQUIRED, "sourceType");
        }
        sourceType = sourceType.trim();
        try {
            AppSourceTypeEnum.getEnumByName(sourceType);
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_NOT_SUPPORT, "sourceType", sourceType);
        }
        return sourceType;
    }

    /**
     * 校验应用配置
     *
     * @param config 应用配置
     * @return 应用配置
     */
    public static AppConfigDTO validateConfig(AppConfigDTO config) {
        Assert.notNull(config, () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_IS_REQUIRED, "config"));
        return config;
    }

    public static AppChatConfigDTO validateChatConfig(AppChatConfigDTO chatConfig) {
        Assert.notNull(chatConfig, () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_IS_REQUIRED, "chatConfig"));

        return chatConfig;
    }
}
