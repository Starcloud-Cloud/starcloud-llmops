package com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MaterialUploadReqVO {

    @Schema(description = "文件附件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件附件不能为空")
    private MultipartFile file;

    @Schema(description = "uid")
    @NotBlank(message = "uid 不能为空")
    private String uid;

    @Schema(description = "来源", example = "app,market")
    @NotBlank(message = "来源不能为空")
    private String planSource;
}
