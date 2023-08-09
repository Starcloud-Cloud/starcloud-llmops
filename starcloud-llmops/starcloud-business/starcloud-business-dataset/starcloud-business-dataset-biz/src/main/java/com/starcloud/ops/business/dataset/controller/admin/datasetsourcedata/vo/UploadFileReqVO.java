package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 字符上传 Request VO")
@Data
@ToString(callSuper = true)
public class UploadFileReqVO extends UploadReqVO {

    @Schema(description = "文件附件", required = true)
    @NotNull(message = "文件附件不能为空")
    private MultipartFile file;

}