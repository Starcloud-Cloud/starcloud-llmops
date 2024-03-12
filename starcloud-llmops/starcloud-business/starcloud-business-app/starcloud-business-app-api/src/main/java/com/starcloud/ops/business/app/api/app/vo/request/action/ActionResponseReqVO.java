package com.starcloud.ops.business.app.api.app.vo.request.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.params.JsonDataVO;
import com.starcloud.ops.business.app.enums.app.AppStepResponseStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 应用 action 响应请求对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用 action 响应请求对象 VO")
public class ActionResponseReqVO implements Serializable {

    private static final long serialVersionUID = 1258993173829362171L;

    /**
     * 响应状态
     */
    @Schema(description = "响应状态", requiredMode = Schema.RequiredMode.REQUIRED)
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
    @Schema(description = "响应类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "响应类型不能为空")
    @InEnum(value = AppStepResponseTypeEnum.class, message = "响应类型[{value}]必须属于: {values}")
    private String type;

    /**
     * 响应样式
     */
    @Schema(description = "响应样式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "响应样式不能为空")
    @InEnum(value = AppStepResponseStyleEnum.class, message = "响应样式[{value}]必须属于: {values}")
    private String style;

    /**
     * 是否显示
     */
    @Schema(description = "是否显示", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否显示不能为空")
    private Boolean isShow;

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
}
