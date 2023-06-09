package com.starcloud.ops.business.app.domain.entity.action;

import lombok.Data;

import java.math.BigDecimal;

/**
 * action 响应实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class ActionResponse {

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
     * 请求 token 使用
     */
    private Long messageTokens = 0L;

    /**
     * 请求单价
     */
    private BigDecimal messageUnitPrice = BigDecimal.ZERO;

    /**
     * 响应 token 使用
     */
    private Long answerTokens = 0L;

    /**
     * 响应单价
     */
    private BigDecimal answerUnitPrice = BigDecimal.ZERO;

    /**
     * 总 token 数量
     */
    private Long totalTokens = 0L;

    /**
     * 总价格
     */
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /**
     * step 执行的参数
     */
    private Object stepVariables;

    /**
     * step 执行的参数
     */
    private Object stepConfig;


}
