package com.starcloud.ops.business.app.api.template.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "模版更新请求实体")
public class TemplateUpdateRequest extends TemplateRequest {

    private static final long serialVersionUID = 1578943423437574534L;

    /**
     * 模版ID
     */
    @Schema(description = "模版UID")
    @NotNull(message = "模版UID不能为空")
    private String uid;

}
