package com.starcloud.ops.business.app.service.upload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
public class UploadRequest implements java.io.Serializable {

    private static final long serialVersionUID = -1132923407346534349L;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    private String name;

    /**
     * 文件路径
     */
    @Schema(description = "文件路径")
    private String path;

    /**
     * 文件内容
     */
    @Schema(description = "文件内容")
    private byte[] content;

}
