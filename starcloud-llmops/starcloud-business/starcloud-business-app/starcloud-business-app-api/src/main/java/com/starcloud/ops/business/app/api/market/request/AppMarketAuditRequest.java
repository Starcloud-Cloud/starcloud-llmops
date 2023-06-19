package com.starcloud.ops.business.app.api.market.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-15
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用市场审核请求实体")
public class AppMarketAuditRequest implements Serializable {

    private static final long serialVersionUID = -3562982149587378399L;

    /**
     * 应用 uid
     */
    @Schema(description = "应用市场应用 uid")
    @NotBlank(message = "应用市场应用 UID 不能为空")
    private String uid;

    /**
     * 应用版本号
     */
    @Schema(description = "应用版本号")
    @NotNull(message = "应用版本号不能为空")
    private Integer version;

    /**
     * 审核状态
     */
    @Schema(description = "审核状态")
    @NotNull(message = "审核状态不能为空")
    private Integer audit;

}
