package com.starcloud.ops.business.app.api.market.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用更新请求实体")
public class AppMarketUpdateReqVO extends AppMarketReqVO {

    private static final long serialVersionUID = -7262473272603640184L;

    /**
     * 应用ID
     */
    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "应用ID不能为空")
    private String uid;

    /**
     * 应用市场应用版本号
     */
    @Schema(description = "应用市场应用版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "应用市场应用版本号不能为空")
    private Integer version;

}
