package com.starcloud.ops.business.app.api.category.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 应用类别 DTO 对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-14
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用类别对象")
public class AppCategoryVO implements Serializable {

    private static final long serialVersionUID = 5973120108905794563L;

    /**
     * 应用类别编号
     */
    @Schema(description = "类别编码")
    private String code;

    /**
     * 应用类别名称
     */
    @Schema(description = "类别名称")
    private String name;

    /**
     * 应用类别图标
     */
    @Schema(description = "类别图标")
    private String icon;

    /**
     * 应用类别图片
     */
    @Schema(description = "类别图片")
    private String image;

    /**
     * 应用类别排序
     */
    @Schema(description = "类别排序")
    private Integer sort;

    /**
     * 应用类别描述
     */
    @Schema(description = "类别描述")
    private String description;

}
