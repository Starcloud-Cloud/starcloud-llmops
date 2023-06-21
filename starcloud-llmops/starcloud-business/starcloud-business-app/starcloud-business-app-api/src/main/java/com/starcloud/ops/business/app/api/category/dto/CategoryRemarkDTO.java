package com.starcloud.ops.business.app.api.category.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 应用类别。从字典的 remark 中获取到的内容
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
public class CategoryRemarkDTO implements Serializable {

    private static final long serialVersionUID = 5378928946617696520L;

    /**
     * 应用类别编号
     */
    @Schema(description = "类别图标")
    private String icon;

    /**
     * 应用类别图片
     */
    @Schema(description = "类别图片")
    private String image;

    /**
     * 应用类别标签
     */
    @Schema(description = "类别名称")
    private Map<String, String> label;

    /**
     * 应用类别描述
     */

    @Schema(description = "类别描述")
    @JSONField(name = "desc")
    private Map<String, String> description;

}