package com.starcloud.ops.business.app.api.limit.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-25
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "AppPublishLimitModifyReqVO", description = "应用发布限流修改请求VO")
public class AppPublishLimitModifyReqVO extends AppPublishLimitReqVO {

    private static final long serialVersionUID = -3250904732887074251L;

    /**
     * UID
     */
    @Schema(description = "UID")
    private String uid;

}
