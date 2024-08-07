package com.starcloud.ops.business.app.verification;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.api.verification.VerificationType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@SuppressWarnings("all")
public class VerificationUtils {

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNullApp(Object object, String bizCode, String message) {
        return notNull(object, bizCode, VerificationType.APP.getCode(), message);
    }

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNullApp(Object object, String bizCode, Integer code, String message) {
        return notNull(object, bizCode, VerificationType.APP.getCode(), code, message);
    }

    /**
     * 判断对象是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param object           对象
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notNullApp(List<Verification> verificationList, Object object, String bizCode, String message) {
        return notNull(verificationList, object, bizCode, VerificationType.APP.getCode(), message);
    }

    /**
     * 判断对象是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param object           对象
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notNullApp(List<Verification> verificationList, Object object, String bizCode, Integer code, String message) {
        return notNull(verificationList, object, bizCode, VerificationType.APP.getCode(), code, message);
    }

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNullChat(Object object, String bizCode, String message) {
        return notNull(object, bizCode, VerificationType.CHAT.getCode(), message);
    }

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNullChat(Object object, String bizCode, Integer code, String message) {
        return notNull(object, bizCode, VerificationType.CHAT.getCode(), code, message);
    }

    /**
     * 判断对象是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param object           对象
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notNullChat(List<Verification> verificationList, Object object, String bizCode, String message) {
        return notNull(verificationList, object, bizCode, VerificationType.CHAT.getCode(), message);
    }

    /**
     * 判断对象是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param object           对象
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notNullChat(List<Verification> verificationList, Object object, String bizCode, Integer code, String message) {
        return notNull(verificationList, object, bizCode, VerificationType.CHAT.getCode(), code, message);
    }

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNullImage(Object object, String bizCode, String message) {
        return notNull(object, bizCode, VerificationType.IMAGE.getCode(), message);
    }

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNullImage(Object object, String bizCode, Integer code, String message) {
        return notNull(object, bizCode, VerificationType.IMAGE.getCode(), code, message);
    }

    /**
     * 判断对象是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param object           对象
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notNullImage(List<Verification> verificationList, Object object, String bizCode, String message) {
        return notNull(verificationList, object, bizCode, VerificationType.IMAGE.getCode(), message);
    }

    /**
     * 判断对象是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param object           对象
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notNullImage(List<Verification> verificationList, Object object, String bizCode, Integer code, String message) {
        return notNull(verificationList, object, bizCode, VerificationType.IMAGE.getCode(), code, message);
    }

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNullStep(Object object, String bizCode, String message) {
        return notNull(object, bizCode, VerificationType.STEP.getCode(), message);
    }

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNullStep(Object object, String bizCode, Integer code, String message) {
        return notNull(object, bizCode, VerificationType.STEP.getCode(), code, message);
    }

    /**
     * 判断对象是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param object           对象
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notNullStep(List<Verification> verificationList, Object object, String bizCode, String message) {
        return notNull(verificationList, object, bizCode, VerificationType.STEP.getCode(), message);
    }

    /**
     * 判断对象是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param object           对象
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notNullStep(List<Verification> verificationList, Object object, String bizCode, Integer code, String message) {
        return notNull(verificationList, object, bizCode, VerificationType.STEP.getCode(), code, message);
    }

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNullCreative(Object object, String bizCode, String message) {
        return notNull(object, bizCode, VerificationType.CREATIVE.getCode(), message);
    }

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNullCreative(Object object, String bizCode, Integer code, String message) {
        return notNull(object, bizCode, VerificationType.CREATIVE.getCode(), code, message);
    }

    /**
     * 判断对象是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param object           对象
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notNullCreative(List<Verification> verificationList, Object object, String bizCode, String message) {
        return notNull(verificationList, object, bizCode, VerificationType.CREATIVE.getCode(), message);
    }

    /**
     * 判断对象是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param object           对象
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notNullCreative(List<Verification> verificationList, Object object, String bizCode, Integer code, String message) {
        return notNull(verificationList, object, bizCode, VerificationType.CREATIVE.getCode(), code, message);
    }

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNullMaterial(Object object, String bizCode, String message) {
        return notNull(object, bizCode, VerificationType.MATERIAL.getCode(), message);
    }

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNullMaterial(Object object, String bizCode, Integer code, String message) {
        return notNull(object, bizCode, VerificationType.MATERIAL.getCode(), code, message);
    }

    /**
     * 判断对象是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param object           对象
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notNullMaterial(List<Verification> verificationList, Object object, String bizCode, String message) {
        return notNull(verificationList, object, bizCode, VerificationType.MATERIAL.getCode(), message);
    }

    /**
     * 判断对象是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param object           对象
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notNullMaterial(List<Verification> verificationList, Object object, String bizCode, Integer code, String message) {
        return notNull(verificationList, object, bizCode, VerificationType.MATERIAL.getCode(), code, message);
    }

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param type    类型
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNull(Object object, String bizCode, Integer type, String message) {
        return notNull(object, bizCode, type, GlobalErrorCodeConstants.BAD_REQUEST.getCode(), message);
    }

    /**
     * 判断对象是否为空,并添加到验证结果列表中。
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param type    类型
     * @param message 错误信息
     * @return 验证结果
     */
    public static List<Verification> notNull(List<Verification> verificationList, Object object, String bizCode, Integer type, String message) {
        return addVerification(verificationList, notNull(object, bizCode, type, message));
    }

    /**
     * 判断对象是否为空
     *
     * @param object  对象
     * @param bizCode 业务码
     * @param type    类型
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notNull(Object object, String bizCode, Integer type, Integer code, String message) {
        if (Objects.isNull(object)) {
            return Verification.of(bizCode, type, code, message);
        }
        return null;
    }

    /**
     * 判断对象是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param object           对象
     * @param bizCode          业务码
     * @param type             类型
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notNull(List<Verification> verificationList, Object object, String bizCode, Integer type, Integer code, String message) {
        return addVerification(verificationList, notNull(object, bizCode, type, code, message));
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlankApp(String string, String bizCode, String message) {
        return notBlank(string, bizCode, VerificationType.APP.getCode(), message);
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlankApp(String string, String bizCode, Integer code, String message) {
        return notBlank(string, bizCode, VerificationType.APP.getCode(), code, message);
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlankApp(List<Verification> verificationList, String string, String bizCode, String message) {
        return notBlank(verificationList, string, bizCode, VerificationType.APP.getCode(), message);
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlankApp(List<Verification> verificationList, String string, String bizCode, Integer code, String message) {
        return notBlank(verificationList, string, bizCode, VerificationType.APP.getCode(), code, message);
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlankChat(String string, String bizCode, String message) {
        return notBlank(string, bizCode, VerificationType.CHAT.getCode(), message);
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlankChat(String string, String bizCode, Integer code, String message) {
        return notBlank(string, bizCode, VerificationType.CHAT.getCode(), code, message);
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlankChat(List<Verification> verificationList, String string, String bizCode, String message) {
        return notBlank(verificationList, string, bizCode, VerificationType.CHAT.getCode(), message);
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlankChat(List<Verification> verificationList, String string, String bizCode, Integer code, String message) {
        return notBlank(verificationList, string, bizCode, VerificationType.CHAT.getCode(), code, message);
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlankImage(String string, String bizCode, String message) {
        return notBlank(string, bizCode, VerificationType.IMAGE.getCode(), message);
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlankImage(String string, String bizCode, Integer code, String message) {
        return notBlank(string, bizCode, VerificationType.IMAGE.getCode(), code, message);
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlankImage(List<Verification> verificationList, String string, String bizCode, String message) {
        return notBlank(verificationList, string, bizCode, VerificationType.IMAGE.getCode(), message);
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlankImage(List<Verification> verificationList, String string, String bizCode, Integer code, String message) {
        return notBlank(verificationList, string, bizCode, VerificationType.IMAGE.getCode(), code, message);
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlankStep(String string, String bizCode, String message) {
        return notBlank(string, bizCode, VerificationType.STEP.getCode(), message);
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlankStep(String string, String bizCode, Integer code, String message) {
        return notBlank(string, bizCode, VerificationType.STEP.getCode(), code, message);
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlankStep(List<Verification> verificationList, String string, String bizCode, String message) {
        return notBlank(verificationList, string, bizCode, VerificationType.STEP.getCode(), message);
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlankStep(List<Verification> verificationList, String string, String bizCode, Integer code, String message) {
        return notBlank(verificationList, string, bizCode, VerificationType.STEP.getCode(), code, message);
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlankCreative(String string, String bizCode, String message) {
        return notBlank(string, bizCode, VerificationType.CREATIVE.getCode(), message);
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlankCreative(String string, String bizCode, Integer code, String message) {
        return notBlank(string, bizCode, VerificationType.CREATIVE.getCode(), code, message);
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlankCreative(List<Verification> verificationList, String string, String bizCode, String message) {
        return notBlank(verificationList, string, bizCode, VerificationType.CREATIVE.getCode(), message);
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlankCreative(List<Verification> verificationList, String string, String bizCode, Integer code, String message) {
        return notBlank(verificationList, string, bizCode, VerificationType.CREATIVE.getCode(), code, message);
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlankMaterial(String string, String bizCode, String message) {
        return notBlank(string, bizCode, VerificationType.MATERIAL.getCode(), message);
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlankMaterial(String string, String bizCode, Integer code, String message) {
        return notBlank(string, bizCode, VerificationType.MATERIAL.getCode(), code, message);
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlankMaterial(List<Verification> verificationList, String string, String bizCode, String message) {
        return notBlank(verificationList, string, bizCode, VerificationType.MATERIAL.getCode(), message);
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlankMaterial(List<Verification> verificationList, String string, String bizCode, Integer code, String message) {
        return notBlank(verificationList, string, bizCode, VerificationType.MATERIAL.getCode(), code, message);
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param type    类型
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlank(String string, String bizCode, Integer type, String message) {
        return notBlank(string, bizCode, type, GlobalErrorCodeConstants.BAD_REQUEST.getCode(), message);
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param type             类型
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlank(List<Verification> verificationList, String string, String bizCode, Integer type, String message) {
        return addVerification(verificationList, notBlank(string, bizCode, type, message));
    }

    /**
     * 判断字符串是否为空
     *
     * @param string  字符串
     * @param bizCode 业务码
     * @param type    类型
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notBlank(String string, String bizCode, Integer type, Integer code, String message) {
        if (StrUtil.isBlank(string)) {
            return Verification.of(bizCode, type, code, message);
        }
        return null;
    }

    /**
     * 判断字符串是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param string           字符串
     * @param bizCode          业务码
     * @param type             类型
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notBlank(List<Verification> verificationList, String string, String bizCode, Integer type, Integer code, String message) {
        return addVerification(verificationList, notBlank(string, bizCode, type, code, message));
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmptyApp(Collection<?> collection, String bizCode, String message) {
        return notEmpty(collection, bizCode, VerificationType.APP.getCode(), message);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param code       错误码
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmptyApp(Collection<?> collection, String bizCode, Integer code, String message) {
        return notEmpty(collection, bizCode, VerificationType.APP.getCode(), code, message);
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param collection       集合
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyApp(List<Verification> verificationList, Collection<?> collection, String bizCode, String message) {
        return notEmpty(verificationList, collection, bizCode, VerificationType.APP.getCode(), message);
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param collection       集合
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyApp(List<Verification> verificationList, Collection<?> collection, String bizCode, Integer code, String message) {
        return notEmpty(verificationList, collection, bizCode, VerificationType.APP.getCode(), code, message);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmptyChat(Collection<?> collection, String bizCode, String message) {
        return notEmpty(collection, bizCode, VerificationType.CHAT.getCode(), message);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param code       错误码
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmptyChat(Collection<?> collection, String bizCode, Integer code, String message) {
        return notEmpty(collection, bizCode, VerificationType.CHAT.getCode(), code, message);
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param collection       集合
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyChat(List<Verification> verificationList, Collection<?> collection, String bizCode, String message) {
        return notEmpty(verificationList, collection, bizCode, VerificationType.CHAT.getCode(), message);
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param collection       集合
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyChat(List<Verification> verificationList, Collection<?> collection, String bizCode, Integer code, String message) {
        return notEmpty(verificationList, collection, bizCode, VerificationType.CHAT.getCode(), code, message);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmptyImage(Collection<?> collection, String bizCode, String message) {
        return notEmpty(collection, bizCode, VerificationType.IMAGE.getCode(), message);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param code       错误码
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmptyImage(Collection<?> collection, String bizCode, Integer code, String message) {
        return notEmpty(collection, bizCode, VerificationType.IMAGE.getCode(), code, message);
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param collection       集合
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyImage(List<Verification> verificationList, Collection<?> collection, String bizCode, String message) {
        return notEmpty(verificationList, collection, bizCode, VerificationType.IMAGE.getCode(), message);
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param collection       集合
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyImage(List<Verification> verificationList, Collection<?> collection, String bizCode, Integer code, String message) {
        return notEmpty(verificationList, collection, bizCode, VerificationType.IMAGE.getCode(), code, message);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmptyStep(Collection<?> collection, String bizCode, String message) {
        return notEmpty(collection, bizCode, VerificationType.STEP.getCode(), message);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param code       错误码
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmptyStep(Collection<?> collection, String bizCode, Integer code, String message) {
        return notEmpty(collection, bizCode, VerificationType.STEP.getCode(), code, message);
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param collection       集合
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyStep(List<Verification> verificationList, Collection<?> collection, String bizCode, String message) {
        return notEmpty(verificationList, collection, bizCode, VerificationType.STEP.getCode(), message);
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param collection       集合
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyStep(List<Verification> verificationList, Collection<?> collection, String bizCode, Integer code, String message) {
        return notEmpty(verificationList, collection, bizCode, VerificationType.STEP.getCode(), code, message);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmptyCreative(Collection<?> collection, String bizCode, String message) {
        return notEmpty(collection, bizCode, VerificationType.CREATIVE.getCode(), message);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param code       错误码
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmptyCreative(Collection<?> collection, String bizCode, Integer code, String message) {
        return notEmpty(collection, bizCode, VerificationType.CREATIVE.getCode(), code, message);
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param collection       集合
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyCreative(List<Verification> verificationList, Collection<?> collection, String bizCode, String message) {
        return notEmpty(verificationList, collection, bizCode, VerificationType.CREATIVE.getCode(), message);
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param collection       集合
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyCreative(List<Verification> verificationList, Collection<?> collection, String bizCode, Integer code, String message) {
        return notEmpty(verificationList, collection, bizCode, VerificationType.CREATIVE.getCode(), code, message);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmptyMaterial(Collection<?> collection, String bizCode, String message) {
        return notEmpty(collection, bizCode, VerificationType.MATERIAL.getCode(), message);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param code       错误码
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmptyMaterial(Collection<?> collection, String bizCode, Integer code, String message) {
        return notEmpty(collection, bizCode, VerificationType.MATERIAL.getCode(), code, message);
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param collection       集合
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyMaterial(List<Verification> verificationList, Collection<?> collection, String bizCode, String message) {
        return notEmpty(verificationList, collection, bizCode, VerificationType.MATERIAL.getCode(), message);
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param collection       集合
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyMaterial(List<Verification> verificationList, Collection<?> collection, String bizCode, Integer code, String message) {
        return notEmpty(verificationList, collection, bizCode, VerificationType.MATERIAL.getCode(), code, message);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param type       类型
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmpty(Collection<?> collection, String bizCode, Integer type, String message) {
        return notEmpty(collection, bizCode, type, GlobalErrorCodeConstants.BAD_REQUEST.getCode(), message);
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param type       类型
     * @param message    错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmpty(List<Verification> verificationList, Collection<?> collection, String bizCode, Integer type, String message) {
        return addVerification(verificationList, notEmpty(collection, bizCode, type, message));
    }

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @param bizCode    业务码
     * @param type       类型
     * @param code       错误码
     * @param message    错误信息
     * @return 验证结果
     */
    public static Verification notEmpty(Collection<?> collection, String bizCode, Integer type, Integer code, String message) {
        if (CollectionUtil.isEmpty(collection)) {
            return Verification.of(bizCode, type, code, message);
        }
        return null;
    }

    /**
     * 判断集合是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param collection       集合
     * @param bizCode          业务码
     * @param type             类型
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmpty(List<Verification> verificationList, Collection<?> collection, String bizCode, Integer type, Integer code, String message) {
        return addVerification(verificationList, notEmpty(collection, bizCode, type, code, message));
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmptyApp(Map<?, ?> map, String bizCode, String message) {
        return notEmpty(map, bizCode, VerificationType.APP.getCode(), message);
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmptyApp(Map<?, ?> map, String bizCode, Integer code, String message) {
        return notEmpty(map, bizCode, VerificationType.APP.getCode(), code, message);
    }

    /**
     * 判断Map是否为空,并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyApp(List<Verification> verificationList, Map<?, ?> map, String bizCode, String message) {
        return notEmpty(verificationList, map, bizCode, VerificationType.APP.getCode(), message);
    }

    /**
     * 判断Map是否为空,并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyApp(List<Verification> verificationList, Map<?, ?> map, String bizCode, Integer code, String message) {
        return notEmpty(verificationList, map, bizCode, VerificationType.APP.getCode(), code, message);
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmptyChat(Map<?, ?> map, String bizCode, String message) {
        return notEmpty(map, bizCode, VerificationType.CHAT.getCode(), message);
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmptyChat(Map<?, ?> map, String bizCode, Integer code, String message) {
        return notEmpty(map, bizCode, VerificationType.CHAT.getCode(), code, message);
    }

    /**
     * 判断Map是否为空,并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyChat(List<Verification> verificationList, Map<?, ?> map, String bizCode, String message) {
        return notEmpty(verificationList, map, bizCode, VerificationType.CHAT.getCode(), message);
    }

    /**
     * 判断Map是否为空,并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyChat(List<Verification> verificationList, Map<?, ?> map, String bizCode, Integer code, String message) {
        return notEmpty(verificationList, map, bizCode, VerificationType.CHAT.getCode(), code, message);
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmptyImage(Map<?, ?> map, String bizCode, String message) {
        return notEmpty(map, bizCode, VerificationType.IMAGE.getCode(), message);
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmptyImage(Map<?, ?> map, String bizCode, Integer code, String message) {
        return notEmpty(map, bizCode, VerificationType.IMAGE.getCode(), code, message);
    }

    /**
     * 判断Map是否为空,并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyImage(List<Verification> verificationList, Map<?, ?> map, String bizCode, String message) {
        return notEmpty(verificationList, map, bizCode, VerificationType.IMAGE.getCode(), message);
    }

    /**
     * 判断Map是否为空,并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyImage(List<Verification> verificationList, Map<?, ?> map, String bizCode, Integer code, String message) {
        return notEmpty(verificationList, map, bizCode, VerificationType.IMAGE.getCode(), code, message);
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmptyStep(Map<?, ?> map, String bizCode, String message) {
        return notEmpty(map, bizCode, VerificationType.STEP.getCode(), message);
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmptyStep(Map<?, ?> map, String bizCode, Integer code, String message) {
        return notEmpty(map, bizCode, VerificationType.STEP.getCode(), code, message);
    }

    /**
     * 判断Map是否为空,并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyStep(List<Verification> verificationList, Map<?, ?> map, String bizCode, String message) {
        return notEmpty(verificationList, map, bizCode, VerificationType.STEP.getCode(), message);
    }

    /**
     * 判断Map是否为空,并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyStep(List<Verification> verificationList, Map<?, ?> map, String bizCode, Integer code, String message) {
        return notEmpty(verificationList, map, bizCode, VerificationType.STEP.getCode(), code, message);
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmptyCreative(Map<?, ?> map, String bizCode, String message) {
        return notEmpty(map, bizCode, VerificationType.CREATIVE.getCode(), message);
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmptyCreative(Map<?, ?> map, String bizCode, Integer code, String message) {
        return notEmpty(map, bizCode, VerificationType.CREATIVE.getCode(), code, message);
    }

    /**
     * 判断Map是否为空,并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyCreative(List<Verification> verificationList, Map<?, ?> map, String bizCode, String message) {
        return notEmpty(verificationList, map, bizCode, VerificationType.CREATIVE.getCode(), message);
    }

    /**
     * 判断Map是否为空,并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyCreative(List<Verification> verificationList, Map<?, ?> map, String bizCode, Integer code, String message) {
        return notEmpty(verificationList, map, bizCode, VerificationType.CREATIVE.getCode(), code, message);
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmptyMaterial(Map<?, ?> map, String bizCode, String message) {
        return notEmpty(map, bizCode, VerificationType.MATERIAL.getCode(), message);
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmptyMaterial(Map<?, ?> map, String bizCode, Integer code, String message) {
        return notEmpty(map, bizCode, VerificationType.MATERIAL.getCode(), code, message);
    }

    /**
     * 判断Map是否为空,并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyMaterial(List<Verification> verificationList, Map<?, ?> map, String bizCode, String message) {
        return notEmpty(verificationList, map, bizCode, VerificationType.MATERIAL.getCode(), message);
    }

    /**
     * 判断Map是否为空,并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmptyMaterial(List<Verification> verificationList, Map<?, ?> map, String bizCode, Integer code, String message) {
        return notEmpty(verificationList, map, bizCode, VerificationType.MATERIAL.getCode(), code, message);
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param type    类型
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmpty(Map<?, ?> map, String bizCode, Integer type, String message) {
        return notEmpty(map, bizCode, type, GlobalErrorCodeConstants.BAD_REQUEST.getCode(), message);
    }

    /**
     * 判断Map是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param type             类型
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmpty(List<Verification> verificationList, Map<?, ?> map, String bizCode, Integer type, String message) {
        return addVerification(verificationList, notEmpty(map, bizCode, type, message));
    }

    /**
     * 判断Map是否为空
     *
     * @param map     Map
     * @param bizCode 业务码
     * @param type    类型
     * @param code    错误码
     * @param message 错误信息
     * @return 验证结果
     */
    public static Verification notEmpty(Map<?, ?> map, String bizCode, Integer type, Integer code, String message) {
        if (CollectionUtil.isEmpty(map)) {
            return Verification.of(bizCode, type, code, message);
        }
        return null;
    }

    /**
     * 判断Map是否为空，并添加到验证结果列表中。
     *
     * @param verificationList 验证结果列表
     * @param map              Map
     * @param bizCode          业务码
     * @param type             类型
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> notEmpty(List<Verification> verificationList, Map<?, ?> map, String bizCode, Integer type, Integer code, String message) {
        return addVerification(verificationList, notEmpty(map, bizCode, type, code, message));
    }


    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerificationApp(List<Verification> verificationList, String bizCode, String message) {
        return addVerification(verificationList, bizCode, VerificationType.APP.getCode(), message);
    }

    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerificationApp(List<Verification> verificationList, String bizCode, Integer code, String message) {
        return addVerification(verificationList, bizCode, VerificationType.APP.getCode(), code, message);
    }

    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerificationChat(List<Verification> verificationList, String bizCode, String message) {
        return addVerification(verificationList, bizCode, VerificationType.CHAT.getCode(), message);
    }

    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerificationChat(List<Verification> verificationList, String bizCode, Integer code, String message) {
        return addVerification(verificationList, bizCode, VerificationType.CHAT.getCode(), code, message);
    }

    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerificationImage(List<Verification> verificationList, String bizCode, String message) {
        return addVerification(verificationList, bizCode, VerificationType.IMAGE.getCode(), message);
    }

    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerificationImage(List<Verification> verificationList, String bizCode, Integer code, String message) {
        return addVerification(verificationList, bizCode, VerificationType.IMAGE.getCode(), code, message);
    }

    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerificationStep(List<Verification> verificationList, String bizCode, String message) {
        return addVerification(verificationList, bizCode, VerificationType.STEP.getCode(), message);
    }

    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerificationStep(List<Verification> verificationList, String bizCode, Integer code, String message) {
        return addVerification(verificationList, bizCode, VerificationType.STEP.getCode(), code, message);
    }

    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerificationCreative(List<Verification> verificationList, String bizCode, String message) {
        return addVerification(verificationList, bizCode, VerificationType.CREATIVE.getCode(), message);
    }

    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerificationCreative(List<Verification> verificationList, String bizCode, Integer code, String message) {
        return addVerification(verificationList, bizCode, VerificationType.CREATIVE.getCode(), code, message);
    }

    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerificationMaterial(List<Verification> verificationList, String bizCode, String message) {
        return addVerification(verificationList, bizCode, VerificationType.MATERIAL.getCode(), message);
    }

    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerificationMaterial(List<Verification> verificationList, String bizCode, Integer code, String message) {
        return addVerification(verificationList, bizCode, VerificationType.MATERIAL.getCode(), code, message);
    }

    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param type             类型
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerification(List<Verification> verificationList, String bizCode, Integer type, String message) {
        verificationList = CollectionUtil.emptyIfNull(verificationList);
        Verification verification = Verification.of(bizCode, type, GlobalErrorCodeConstants.BAD_REQUEST.getCode(), message);
        verificationList.add(verification);
        return verificationList;
    }

    /**
     * 添加验证结果
     *
     * @param verificationList 验证结果列表
     * @param bizCode          业务码
     * @param type             类型
     * @param code             错误码
     * @param message          错误信息
     * @return 验证结果
     */
    public static List<Verification> addVerification(List<Verification> verificationList, String bizCode, Integer type, Integer code, String message) {
        verificationList = CollectionUtil.emptyIfNull(verificationList);
        Verification verification = Verification.of(bizCode, type, code, message);
        verificationList.add(verification);
        return verificationList;
    }

    /**
     * 添加验证结果，如果验证结果不为空，添加到验证结果列表中，否则不添加
     *
     * @param verificationList 验证结果列表
     * @param verification     验证结果
     */
    public static List<Verification> addVerification(List<Verification> verificationList, Verification verification) {
        verificationList = CollectionUtil.emptyIfNull(verificationList);
        if (Objects.nonNull(verification)) {
            verificationList.add(verification);
        }
        return verificationList;
    }

}
