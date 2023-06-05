package com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 数据集源数据存储 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetStorageRespVO extends DatasetStorageBaseVO {

    @Schema(description = "数据集源数据", required = true)
    @NotNull(message = "数据集源数据不能为空")
    private MultipartFile file;

    @Schema(description = "数据集源数据")
    private String path;

}