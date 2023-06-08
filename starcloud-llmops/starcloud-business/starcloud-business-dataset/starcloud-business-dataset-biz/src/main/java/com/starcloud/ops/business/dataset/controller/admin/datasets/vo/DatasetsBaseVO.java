package com.starcloud.ops.business.dataset.controller.admin.datasets.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @className    : DatasetsBaseVO
 * @description  : [数据集 Base VO，提供给添加、修改、详细的子 VO 使用]
 * @author       : [wuruiqiang]
 * @version      : [v1.0]
 * @createTime   : [2023/5/31 16:02]
 * @updateUser   : [AlanCusack]
 * @updateTime   : [2023/5/31 16:02]
 * @updateRemark : [暂无修改]
 */
@Data
public class DatasetsBaseVO {

    @Schema(description = "名称", required = true)
    @NotNull(message = "数据集名称不能为空")
    private String name;

    @Schema(description = "描述", required = true)
    @NotNull(message = "数据集描述不能为空")
    private String description;

    @Schema(description = "权限 (0-私有，1-租户共享，2-全体共享)", required = true)
    @NotNull(message = "权限不能为空")
    private Integer permission;

    @Schema(description = "索引技术", required = true)
    private String indexingModel;

}