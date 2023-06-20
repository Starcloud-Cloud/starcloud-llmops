package com.starcloud.ops.business.app.validate.app;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.app.dto.config.ChatConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.config.WorkflowConfigDTO;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
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
        if (IEnumable.contains(model.toUpperCase().trim(), AppModelEnum.class)) {
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
        if (IEnumable.contains(type.toUpperCase().trim(), AppTypeEnum.class)) {
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
        if (IEnumable.contains(source.toUpperCase().trim(), AppSourceEnum.class)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_FIELD_NOT_SUPPORT, "source", source);
        }
    }

    /**
     * 校验应用配置
     *
     * @param config 应用配置
     * @return 应用配置
     */
    public static void validateConfig(WorkflowConfigDTO config) {
        assertNotNull(config, "App Config Data");
    }

    public static void validateChatConfig(ChatConfigDTO chatConfig) {
        assertNotNull(chatConfig, "App Chat Config Data");

    }

    /**
     * 校验应用的字段信息
     */
    public static void validate(AppReqVO request) {
        assertNotNull(request, "App Request Data");
        validateNotBlank(request.getName(), "name");
        validateModel(request.getModel());
        validateType(request.getType());
        validateSource(request.getSource());
        if (AppModelEnum.COMPLETION.name().equals(request.getModel())) {
//            validateConfig(request.getConfig());
        } else if (AppModelEnum.CHAT.name().equals(request.getModel())) {
//            validateChatConfig(request.getChatConfig());
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

    public static void isNull(Object object, ErrorCode code, Object... args) {
        Assert.isNull(object, () -> ServiceExceptionUtil.exception(code, args));
    }

    public static void notNull(Object object, ErrorCode code, Object... args) {
        Assert.notNull(object, () -> ServiceExceptionUtil.exception(code, args));
    }

    public static void notBlank(String str, ErrorCode code, Object... args) {
        Assert.notBlank(str, () -> ServiceExceptionUtil.exception(code, args));
    }

    public static void isTrue(boolean expression, ErrorCode code, Object... args) {
        Assert.isTrue(expression, () -> ServiceExceptionUtil.exception(code, args));
    }

    public static void isFalse(boolean expression, ErrorCode code, Object... args) {
        Assert.isFalse(expression, () -> ServiceExceptionUtil.exception(code, args));
    }

    public static <E, T extends Iterable<E>> void notEmpty(T collection, ErrorCode code, Object... args) {
        Assert.notEmpty(collection, () -> ServiceExceptionUtil.exception(code, args));
    }

}
