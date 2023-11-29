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
     * 创作计划UID
     */
    @Schema(description = "创作计划UID")
    private String planUid;

    /**
     * 创作方案UID
     */
    @Schema(description = "创作方案UID")
    private String schemeUid;

    /**
     * 业务UID
     */
    @Schema(description = "业务UID")
    private String businessUid;

    /**
     * 创作任务UID
     */
    @Schema(description = "创作任务UID")
    private String contentUid;

}
