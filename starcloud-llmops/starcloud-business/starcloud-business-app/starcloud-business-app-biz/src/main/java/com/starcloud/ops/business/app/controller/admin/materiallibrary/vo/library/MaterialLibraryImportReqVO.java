package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 素材知识库基础信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MaterialLibraryImportReqVO {

    @Schema(description = "素材库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "15737")
    @NotNull(message = "素材库ID不能为空")
    private Long libraryId;

    @Schema(description = "文件附件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件附件不能为空")
    private MultipartFile[] file;

    @Schema(description = "文件类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @InEnum(value = MaterialTypeEnum.class)
    private Integer materialType;





}