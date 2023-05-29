package com.starcloud.ops.business.app.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "我的模板接口", description = "我的模板接口")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplatePageRequestParams {

    @ApiModelProperty(value = "站点用户唯一标识")
    public String userUniqueId;

    @ApiModelProperty(value = "当前页码")
    public int current;

    @ApiModelProperty(value = "页面大小")
    private int limit;
}
