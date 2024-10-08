package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import com.starcloud.ops.business.app.enums.materiallibrary.MaterialBindTypeEnum;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialFormatTypeEnum;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialLibraryTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Schema(description = "管理后台 - 素材知识库新增/修改 Request VO")
@Data
public class MaterialLibrarySaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "26459")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "名称不能为空")
    @Size(max = 100, message = "素材库名称不能超过100个字符")
    private String name;

    @Schema(description = "图标链接", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    private String iconUrl;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
    @Size(max = 300, message = "素材库描述不能超过100个字符")
    private String description;

    @Schema(description = "素材类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer formatType = MaterialFormatTypeEnum.EXCEL.getCode();

    @Schema(description = "素材库类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer libraryType = MaterialLibraryTypeEnum.MEMBER.getCode();

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Boolean status;

    @Schema(description = "素材库创建来源", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer createSource = MaterialBindTypeEnum.MEMBER.getCode();


}