package com.starcloud.ops.business.app.api.verification;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 校验消息，用于校验失败时返回给前端的消息
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class Verification implements java.io.Serializable {

    private static final long serialVersionUID = 3863921304380333365L;

    /**
     * 业务编码
     */
    private String bizCode;

    /**
     * 类型:
     * <ul>
     *     <li>1: 应用错误(我的应用，应用市场)</li>
     *     <li>2: 聊天</li>
     *     <li>3: 图片错误</li>
     *     <li>4: 步骤错误</li>
     *     <li>5: 创作错误</li>
     *     <li>6: 素材库错误</li>
     * </ul>
     */
    private Integer type;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 应用类型验证消息
     *
     * @param bizCode 业务编码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证消息
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static Verification ofApp(String bizCode, Integer code, String message) {
        return of(bizCode, VerificationType.APP.getCode(), code, message);
    }

    /**
     * 聊天类型验证消息
     *
     * @param bizCode 业务编码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证消息
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static Verification ofChat(String bizCode, Integer code, String message) {
        return of(bizCode, VerificationType.CHAT.getCode(), code, message);
    }

    /**
     * 图片类型验证消息
     *
     * @param bizCode 业务编码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证消息
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static Verification ofImage(String bizCode, Integer code, String message) {
        return of(bizCode, VerificationType.IMAGE.getCode(), code, message);
    }

    /**
     * 步骤类型验证消息
     *
     * @param bizCode 业务编码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证消息
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static Verification ofStep(String bizCode, Integer code, String message) {
        return of(bizCode, VerificationType.STEP.getCode(), code, message);
    }

    /**
     * 创作类型验证消息
     *
     * @param bizCode 业务编码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证消息
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static Verification ofCreative(String bizCode, Integer code, String message) {
        return of(bizCode, VerificationType.CREATIVE.getCode(), code, message);
    }

    /**
     * 素材库类型验证消息
     *
     * @param bizCode 业务编码
     * @param code    错误码
     * @param message 错误信息
     * @return 验证消息
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static Verification ofMaterial(String bizCode, Integer code, String message) {
        return of(bizCode, VerificationType.MATERIAL.getCode(), code, message);
    }

    /**
     * 创建校验消息
     *
     * @param bizCode 业务编码
     * @param type    错误类型
     * @param code    错误码
     * @param message 错误信息
     * @return 校验消息
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static Verification of(String bizCode, Integer type, Integer code, String message) {
        Verification validationMessage = new Verification();
        validationMessage.setBizCode(bizCode);
        validationMessage.setType(type);
        validationMessage.setCode(code);
        validationMessage.setMessage(message);
        return validationMessage;
    }


}
