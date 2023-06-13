package com.starcloud.ops.business.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppStepResponse {

    /**
     * 响应状态
     */
    private Boolean success;

    /**
     * 响应错误码
     */
    private String errorCode;

    /**
     * 响应错误码
     */
    private String errorMsg;

    /**
     * 相应类型
     */
    private String type;

    /**
     * 响应数据
     */
    private String style;

    /**
     * 是否显示
     */
    private Boolean isShow;

    /**
     * 请求数据
     */
    private String message;

    /**
     * 响应数据
     */
    private String answer;

    /**
     * step 执行的参数
     */
    private Object stepVariables;

    /**
     * step 执行的参数
     */
    private Object stepConfig;

    private Long messageTokens;

    private BigDecimal messageUnitPrice;

    private Long answerTokens;

    private BigDecimal answerUnitPrice;

    private Long totalTokens;

    private BigDecimal totalPrice;

}
