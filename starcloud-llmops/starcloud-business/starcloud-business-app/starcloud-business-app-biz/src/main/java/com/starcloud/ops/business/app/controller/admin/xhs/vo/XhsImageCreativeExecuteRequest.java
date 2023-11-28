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
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "XhsImageCreativeExecuteRequest", description = "小红书创作执行请求")
public class XhsImageCreativeExecuteRequest implements java.io.Serializable {

    private static final long serialVersionUID = -2581352572917982820L;

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

    /**
     * 图片风格请求参数
     */
    @Schema(description = "图片风格请求参数")
    private XhsImageStyleExecuteRequest imageStyleRequest;

}
