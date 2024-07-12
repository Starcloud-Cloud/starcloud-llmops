package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 应用素材绑定 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MaterialLibraryAppBindRespVO {

    @Schema(description = "主键(自增策略)", requiredMode = Schema.RequiredMode.REQUIRED, example = "30393")
    @ExcelProperty("主键(自增策略)")
    private Long id;

    @Schema(description = "素材编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "14790")
    @ExcelProperty("素材编号")
    private Long libraryId;

    @Schema(description = "应用类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("应用类型")
    private Integer appType;

    @Schema(description = "应用编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "7090")
    @ExcelProperty("应用编号")
    private String appUid;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "23875")
    @ExcelProperty("用户编号")
    private Long userId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}