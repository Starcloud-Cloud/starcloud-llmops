package com.starcloud.ops.business.app.api.market.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用 marketUid 请求实体")
public class AppMarketUidRequest implements Serializable {

    private static final long serialVersionUID = -8083745861564277538L;

    /**
     * 应用 uid
     */
    @Schema(description = "应用市场应用 uid")
    @NotBlank(message = "应用市场应用 UID 不能为空")
    private String marketUid;

}
