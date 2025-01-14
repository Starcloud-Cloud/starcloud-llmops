package com.starcloud.ops.business.app.api.app.vo.response.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.params.JsonDataVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 应用 action 响应请求对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用 action 响应响应对象 VO")
public class ActionResponseRespVO implements Serializable {

    private static final long serialVersionUID = -7520511047146965514L;

    /**
     * 响应状态
     */
    @Schema(description = "响应状态")
    private Boolean success;

    /**
     * 响应错误码
     */
    @Schema(description = "响应错误码")
    private String errorCode;

    /**
     * 响应信息
     */
    @Schema(description = "响应信息")
    private String errorMsg;

    /**
     * 响应类型
     */
    @Schema(description = "响应类型")
    private String type;

    /**
     * 响应样式
     */
    @Schema(description = "响应样式")
    private String style;

    /**
     * 是否显示
     */
    @Schema(description = "是否显示")
    private Boolean isShow;

    /**
     * 是否只读
     */
    @Schema(description = "是否只读")
    private Boolean readOnly = Boolean.FALSE;

    /**
     * 请求数据
     */
    @Schema(description = "请求数据")
    private String message;

    /**
     * 响应数据
     */
    @Schema(description = "响应数据")
    private String answer;

    /**
     * 返回数据
     */
    @Schema(description = "返回数据")
    private JsonDataVO output;

    /**
     * 请求 token 使用
     */
    @Schema(description = "请求 token 使用")
    private Long messageTokens;

    /**
     * 请求单价
     */
    @Schema(description = "请求单价")
    private BigDecimal messageUnitPrice;

    /**
     * 响应 token 使用
     */
    @Schema(description = "响应 token 使用")
    private Long answerTokens;

    /**
     * 响应单价
     */
    @Schema(description = "响应单价")
    private BigDecimal answerUnitPrice;

    /**
     * 总 token 数量
     */
    @Schema(description = "总 token 数量")
    private Long totalTokens;

    /**
     * 总价格
     */
    @Schema(description = "总价格")
    private BigDecimal totalPrice;

    /**
     * step 执行的参数
     */
    @Schema(description = "step 执行的参数")
    private Object stepConfig;
}
