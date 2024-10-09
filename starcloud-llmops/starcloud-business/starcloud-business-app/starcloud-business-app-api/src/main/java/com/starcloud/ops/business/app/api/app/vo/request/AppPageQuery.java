package com.starcloud.ops.business.app.api.app.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.framework.common.api.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 模板分页查询条件
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-26
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用查询类")
public class AppPageQuery extends PageQuery {

    private static final long serialVersionUID = 7950105091779298947L;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用模型
     */
    @Schema(description = "应用类型")
    private String model;

    /**
     * 应用类型
     */
    @Schema(description = "应用类型")
    private String type;

    /**
     * 应用类别
     */
    @Schema(description = "应用类别")
    private String category;

    /**
     * 应用标签
     */
    @Schema(description = "应用标签")
    private List<String> tags;

    /**
     * 应用场景
     */
    @Schema(description = "应用场景")
    private List<String> scenes;


}
