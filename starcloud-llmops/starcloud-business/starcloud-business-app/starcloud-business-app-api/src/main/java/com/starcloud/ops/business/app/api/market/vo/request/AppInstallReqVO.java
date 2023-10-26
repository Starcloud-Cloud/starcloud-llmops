package com.starcloud.ops.business.app.api.market.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 安装应用请求实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-20
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用UID请求实体")
public class AppInstallReqVO {

    /**
     * 应用 uid
     */
    @Schema(description = "应用市场应用 uid", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "应用市场应用 UID 不能为空")
    private String uid;

}
