package com.starcloud.ops.business.app.api.limit.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-28
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "AppPublishLimitOperateReqVO", description = "应用发布限流操作请求VO")
public class AppPublishLimitOperateReqVO implements Serializable {

    private static final long serialVersionUID = 3772015206912337795L;

    /**
     * 应用发布限流 UID
     */
    @Schema(description = "应用发布限流 UID")
    @NotBlank(message = "应用发布限流 UID不能为空")
    private String uid;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型")
    @NotBlank(message = "操作类型不能为空")
    private String operate;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    @NotNull(message = "是否启用不能为空")
    private Boolean enable;
}
