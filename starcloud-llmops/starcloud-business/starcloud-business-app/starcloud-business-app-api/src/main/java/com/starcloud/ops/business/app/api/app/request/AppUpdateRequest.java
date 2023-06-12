package com.starcloud.ops.business.app.api.app.request;

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
@Schema(description = "应用更新请求实体")
public class AppUpdateRequest extends AppRequest {

    private static final long serialVersionUID = 1578943423437574534L;

    /**
     * 应用ID
     */
    @Schema(description = "应用UID")
    @NotNull(message = "应用UID不能为空")
    private String uid;

}
