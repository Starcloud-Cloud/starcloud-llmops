package com.starcloud.ops.business.app.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.framework.common.api.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 模板分页查询条件
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "模版查询类")
public class TemplatePageQuery extends PageQuery {

    /**
     * 模版名称
     */
    @Schema(description = "模版名称")
    private String name;

    /**
     * 模版标签
     */
    @Schema(description = "模版标签")
    private List<String> tags;

    /**
     * 模版分类
     */
    @Schema(description = "模版分类")
    private List<String> categories;

}
