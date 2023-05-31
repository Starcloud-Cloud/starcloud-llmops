package com.starcloud.ops.framework.common.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 一般下拉框时使用
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "选项对象", description = "选项对象, 一般用于下拉框，用于描述 label 和 value")
public class Option implements Serializable {

    private static final long serialVersionUID = 178678534536774534L;

    /**
     * label：属性名，用于展示
     */
    @ApiModelProperty(value = "属性名，用于展示")
    private String label;

    /**
     * value：属性值，用于传递
     */
    @ApiModelProperty(value = "属性值，用于传递")
    private String value;

    /**
     * 描述，用于展示，对该选项的描述
     */
    @ApiModelProperty(value = "描述，用于展示，对该选项的描述")
    private String description;

    /**
     * 创建一个选项
     *
     * @param label label
     * @param value value
     *
     * @return LabelValue 对象
     */
    public static Option of(String label, String value) {
        Option option = new Option();
        option.setLabel(label);
        option.setValue(value);
        return option;
    }

}
