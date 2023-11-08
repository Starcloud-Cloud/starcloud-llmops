package com.starcloud.ops.business.app.api.market.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-21
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AppMarketListQuery", description = "应用基础请求实体")
public class AppMarketListQuery implements Serializable {

    private static final long serialVersionUID = -4586870698292817798L;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用类型
     */
    @Schema(description = "应用类型")
    private String type;

    /**
     * 应用模型
     */
    @Schema(description = "应用分类")
    private String model;

    /**
     * 应用分类
     */
    @Schema(description = "应用分类")
    private String category;

    /**
     * 应用场景
     */
    @Schema(description = "应用场景")
    private List<String> scenes;

    /**
     * 应用标签
     */
    @Schema(description = "应用标签")
    private List<String> tags;

    /**
     * 是否查询简单字段
     */
    @Schema(description = "是否只查询简单字段")
    private Boolean isSimple;

}
