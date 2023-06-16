package com.starcloud.ops.business.app.api.app.dto;

import com.alibaba.fastjson.annotation.JSONField;
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
public class CategoryRemark implements Serializable {

    private static final long serialVersionUID = 5378928946617696520L;

    /**
     * 应用类别编号
     */
    @Schema(description = "类别图标")
    private String icon;

    /**
     * 应用类别图片
     */
    private String image;

    /**
     * 应用类别 Label 英文
     */
    private String labelEn;

    /**
     * 应用类别 Label 中文
     */
    private String labelZh;

    /**
     * 应用类别描述 英文
     */
    @JSONField(name = "descEn")
    private String descriptionEn;

    /**
     * 应用类别描述 中文
     */
    @JSONField(name = "descZh")
    private String descriptionZh;
}