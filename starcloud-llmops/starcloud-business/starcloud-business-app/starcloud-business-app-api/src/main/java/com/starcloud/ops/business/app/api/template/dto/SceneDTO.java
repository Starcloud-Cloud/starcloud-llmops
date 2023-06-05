package com.starcloud.ops.business.app.api.template.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 模版场景
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "模版场景")
public class SceneDTO implements Serializable {

    private static final long serialVersionUID = 157689534536774534L;

    /**
     * 场景名称
     */
    @Schema(description = "场景名称")
    private String name;

    /**
     * 场景来源
     */
    @Schema(description = "场景来源")
    private String sourceType;

    /**
     * 场景描述
     */
    @Schema(description = "场景描述")
    private String description;

    /**
     * 场景文档地址
     */
    @Schema(description = "场景文档地址")
    private String docUrl;


}
