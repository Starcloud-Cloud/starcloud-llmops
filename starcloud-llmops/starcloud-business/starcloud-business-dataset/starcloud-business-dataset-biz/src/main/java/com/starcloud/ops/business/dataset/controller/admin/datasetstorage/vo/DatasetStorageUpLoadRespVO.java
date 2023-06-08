package com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
* 数据集源数据存储 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class DatasetStorageUpLoadRespVO {

    @Schema(description = "编号", required = true)
    @NotNull(message = "编号不能为空")
    private String uid;

    @Schema(description = "文件名称", required = true)
    @NotNull(message = "文件名称")
    private String name;

    @Schema(description = "数据类型", required = true)
    @NotNull(message = "数据类型不能为空")
    private String type;

    @Schema(description = "键", required = true)
    @NotNull(message = "键不能为空")
    private String storageKey;

    @Schema(description = "存储类型", required = true)
    @NotNull(message = "存储类型不能为空")
    private String storageType;

    @Schema(description = "MIME类型", example = "2")
    private String mimeType;

}