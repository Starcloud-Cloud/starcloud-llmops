package com.starcloud.ops.business.app.validate.app;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.app.dto.AppChatConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppConfigDTO;
import com.starcloud.ops.business.app.api.app.request.AppRequest;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-06
 */
public class AppValidate {

    /**
     * 校验应用模型字段为否为空字符串
     *
     * @param value 字段值
     * @param field 字段名
     */
    public static void validateNotBlank(String value, String field) {
        if (StringUtils.isBlank(value)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_IS_REQUIRED, field);
        }
    }

    /**
     * 校验应用模型
     *
     * @param model 应用模型
     */
    public static void validateModel(String model) {
        validateNotBlank(model, "model");
        if (!AppModelEnum.cache().containsKey(model.toUpperCase().trim())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_NOT_SUPPORT, "model", model);
        }
    }

    /**
     * 校验应用类型
     *
     * @param type 应用类型
     */
    public static void validateType(String type) {
        validateNotBlank(type, "type");
        if (!AppTypeEnum.cache().containsKey(type.toUpperCase().trim())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_NOT_SUPPORT, "type", type);
        }
    }

    /**
     * 校验应用来源
     *
     * @param source 应用来源
     * @return 应用来源
     */
    public static void validateSource(String source) {
        validateNotBlank(source, "source");
        if (!AppSourceEnum.cache().containsKey(source.toUpperCase().trim())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_NOT_SUPPORT, "source", source);
        }
    }

    /**
     * 校验应用配置
     *
     * @param config 应用配置
     * @return 应用配置
     */
    public static void validateConfig(AppConfigDTO config) {
        assertNotNull(config, "App Config Data");
    }

    public static void validateChatConfig(AppChatConfigDTO chatConfig) {
        assertNotNull(chatConfig, "App Chat Config Data");

    }

    /**
     * 校验应用的字段信息
     */
    public static void validate(AppRequest request) {
        assertNotNull(request, "App Request Data");
        validateNotBlank(request.getName(), "name");
        validateModel(request.getModel());
        validateType(request.getType());
        validateSource(request.getSource());
        if (AppModelEnum.COMPLETION.name().equals(request.getModel())) {
            validateConfig(request.getConfig());
        } else if (AppModelEnum.CHAT.name().equals(request.getModel())) {
            validateChatConfig(request.getChatConfig());
        }
    }

    /**
     * 校验对象是否为空
     *
     * @param object  对象
     * @param message 异常信息
     */
    public static void assertNotNull(Object object, String message) {
        Assert.notNull(object, () -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_DATA_IS_NULL, message));
    }
}
