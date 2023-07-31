package com.starcloud.ops.business.app.api.publish.vo.request;

import com.starcloud.ops.framework.common.api.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(name = "AppPublishPageReqVO", description = "应用发布分页请求")
public class AppPublishPageReqVO extends PageQuery {

    private static final long serialVersionUID = 4010213842178810121L;

    /**
     * 应用 UID
     */
    @Schema(description = "应用 UID")
    private String appUid;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用模型
     */
    @Schema(description = "应用模型")
    private String model;

    /**
     * 审核状态
     */
    @Schema(description = "审核状态")
    private Integer audit;

    @Schema
    private Boolean isAdmin;

}
