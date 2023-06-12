package com.starcloud.ops.business.app.api.market.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.framework.common.api.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "模版查询类")
public class AppMarketPageQuery extends PageQuery {

    private static final long serialVersionUID = 1938148907339030050L;

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

    /**
     * 模版场景
     */
    @Schema(description = "模版场景")
    private List<String> scenes;

}
