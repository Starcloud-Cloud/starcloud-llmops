package com.starcloud.ops.business.app.controller.admin.xhs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(name = "XhsAppCreativeExecuteRequest", description = "小红书创意执行请求")
public class XhsAppCreativeExecuteRequest extends XhsAppExecuteRequest {

    private static final long serialVersionUID = -2675544310134972689L;

    /**
     * 计划uid
     */
    @Schema(description = "计划uid")
    private String planUid;

    /**
     * 方案uid
     */
    @Schema(description = "方案uid")
    private String schemeUid;

    /**
     * 任务uid
     */
    @Schema(description = "任务uid")
    private String creativeContentUid;

}
