package com.starcloud.ops.business.app.api.xhs.execute;

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
