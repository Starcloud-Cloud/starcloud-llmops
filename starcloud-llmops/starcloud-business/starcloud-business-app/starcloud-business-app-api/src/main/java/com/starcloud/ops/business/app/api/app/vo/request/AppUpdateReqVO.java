package com.starcloud.ops.business.app.api.app.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用更新请求实体")
public class AppUpdateReqVO extends AppReqVO {

    private static final long serialVersionUID = 1578943423437574534L;

    /**
     * 应用ID
     */
    @Schema(description = "应用UID")
    @NotBlank(message = "应用UID不能为空")
    private String uid;

    /**
     * 区分修改类型
     */
    private Integer e;
}
