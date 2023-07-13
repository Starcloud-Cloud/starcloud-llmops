package com.starcloud.ops.business.app.api.app.vo.request.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.request.variable.VariableReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;

/**
 * 生成图片配置请求对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "生成图片配置请求对象")
public class ImageConfigReqVO extends BaseConfigReqVO {

    private static final long serialVersionUID = 6774663656090478009L;

    /**
     * 图片变量
     */
    @Schema(description = "模版变量")
    @Valid
    private VariableReqVO variable;

}
