package com.starcloud.ops.business.app.enums;

import com.starcloud.ops.framework.common.api.dto.ResultCode;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
public enum AppResultCode implements ResultCode {

    // ========== 模版错误码 ==========

    /**
     * 模版不存在
     */
    TEMPLATE_NOT_EXISTS(300100000, "The template [ID: %s] does not exist"),

    /**
     * 模版已经存在
     */
    TEMPLATE_EXISTS(300100001, "The template [ID: %s] already exists"),

    /**
     * 创建模版失败
     */
    TEMPLATE_CREATE_FAILED(300100002, "Failed to create template, %s"),

    /**
     * 复制模版失败
     */
    TEMPLATE_COPY_FAILED(300100003, "Failed to copy template, %s"),

    /**
     * 更新模版失败
     */
    TEMPLATE_MODIFY_FAILED(300100004, "Failed to update template, %s"),

    /**
     * 删除模版失败
     */
    TEMPLATE_DELETE_FAILED(300100005, "Failed to delete template, %s"),

    /**
     * 模版名称重复
     */
    TEMPLATE_NAME_DUPLICATE(300100006,"The template name: %s has been exist, please change the template name and try again." ),

    // ========== 模版市场错误码 3-002-000-000 ==========
    /**
     * 该模版在模版市场不存在
     */
    TEMPLATE_MARKET_NOT_EXISTS(300200000, "The template [ID: %s] does not exist in the template market"),

    /**
     * 该模版在模版市场已经存在
     */
    TEMPLATE_MARKET_EXISTS(300200001, "The template [ID: %s] already exists in the template market"),

    /**
     * 新增模版到模版市场失败
     */
    TEMPLATE_MARKET_CREATE_FAILED(300200002, "Failed to create template in the template market, %s"),

    /**
     * 更新模版市场模版失败
     */
    TEMPLATE_MARKET_MODIFY_FAILED(300200003, "Failed to update template in the template market, %s"),

    /**
     * 删除模版市场模版失败
     */
    TEMPLATE_MARKET_DELETE_FAILED(300200004, "Failed to delete template in the template market, %s");


    @Getter
    private final Integer code;

    @Getter
    private final String message;

    AppResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    }
