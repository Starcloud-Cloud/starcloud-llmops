package com.starcloud.ops.business.poster.controller.admin.materialgroup.vo;

import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 海报素材分组新增/修改 Request VO")
@Data
public class MaterialGroupSaveReqVO {

    @Schema(description = "主键id", requiredMode = Schema.RequiredMode.REQUIRED, example = "19172")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "6110")
    private String uid;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(description = "缩略图", requiredMode = Schema.RequiredMode.REQUIRED)
    // @URL(message = "缩略图必须是 URL 格式")
    // @NotEmpty(message = "缩略图不能为空")
    private String thumbnail;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "类型不能为空")
    private String type;

    @Schema(description = "素材分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "881")
    @NotNull(message = "素材分类编号不能为空")
    private Long categoryId;

    @Schema(description = "标签")
    private String materialTags;

    @Schema(description = "开启状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Boolean status = Boolean.TRUE;

    @Schema(description = "是否公开", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Boolean overtStatus = Boolean.FALSE;

    @Schema(description = "关联编号")
    private Long associatedId;

    @Schema(description = "素材数据", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "素材数据不能为空")
    private List<MaterialSaveReqVO> materialSaveReqVOS;


}