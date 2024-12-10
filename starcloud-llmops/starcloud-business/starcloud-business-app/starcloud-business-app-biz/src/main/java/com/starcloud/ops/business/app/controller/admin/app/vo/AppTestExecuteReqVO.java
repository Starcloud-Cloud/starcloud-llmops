package com.starcloud.ops.business.app.controller.admin.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 应用类别 DTO 对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-14
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@Schema(description = "应用执行请求对象")
public class AppTestExecuteReqVO extends AppExecuteReqVO {

    private static final long serialVersionUID = -7780443181168293584L;

    /**
     * 来源
     * APP: 应用
     * MARKET: 应用市场
     */
    @NotBlank(message = "来源不能为空")
    private String source;
}
