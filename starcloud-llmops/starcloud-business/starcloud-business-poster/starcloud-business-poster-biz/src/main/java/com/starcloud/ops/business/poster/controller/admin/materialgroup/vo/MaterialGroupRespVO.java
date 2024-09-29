package com.starcloud.ops.business.poster.controller.admin.materialgroup.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialRespVO;
import com.starcloud.ops.business.poster.enums.material.MaterialTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 海报素材分组 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MaterialGroupRespVO {

    @Schema(description = "主键id", requiredMode = Schema.RequiredMode.REQUIRED, example = "19172")
    @ExcelProperty("主键id")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "6110")
    @ExcelProperty("编号")
    private String uid;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "缩略图", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("缩略图")
    private String thumbnail;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("类型")
    @InEnum(value = MaterialTypeEnum.class, message = "分佣模式必须是 {value}")
    private Integer type;

    @Schema(description = "标签")
    @ExcelProperty("标签")
    private String materialTags;

    @Schema(description = "开启状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("开启状态")
    private Boolean status;

    @Schema(description = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("用户类型")
    private String userType;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "素材分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "881")
    @ExcelProperty("素材分类编号")
    private Long categoryId;

    @Schema(description = "公开状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "881")
    @ExcelProperty("公开状态")
    private Boolean overtStatus;

    @Schema(description = "素材数据", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "素材数据不能为空")
    private List<MaterialRespVO> materialRespVOS;


    @Schema(description = "素材数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("素材数量")
    private Integer materialCount;

}