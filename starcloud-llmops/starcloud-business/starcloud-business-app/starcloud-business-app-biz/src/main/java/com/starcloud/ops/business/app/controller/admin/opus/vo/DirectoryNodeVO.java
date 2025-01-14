package com.starcloud.ops.business.app.controller.admin.opus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "目录树结构")
public class DirectoryNodeVO {

    @Schema(description = "目录uid")
    private String dirUid;

    @Schema(description = "目录名称")
    private String dirName;

    @Schema(description = "目录描述")
    private String dirDesc;

    @Schema(description = "父解读uid")
    private String parentUid;

    @Schema(description = "目录树顺序")
    private Integer order;

    @Schema(description = "子目录")
    private List<DirectoryNodeVO> children;
}
