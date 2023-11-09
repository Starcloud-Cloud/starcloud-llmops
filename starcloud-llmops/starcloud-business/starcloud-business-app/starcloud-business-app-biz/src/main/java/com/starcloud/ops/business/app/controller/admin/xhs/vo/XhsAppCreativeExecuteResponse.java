package com.starcloud.ops.business.app.controller.admin.xhs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-09
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(name = "小红书应用创意执行响应", description = "小红书应用创意执行响应")
public class XhsAppCreativeExecuteResponse extends XhsAppExecuteResponse {

    private static final long serialVersionUID = 6728431346119515076L;

    /**
     * 任务uid
     */
    @Schema(description = "任务uid")
    private String creativeContentUid;
}
