package com.starcloud.ops.business.app.controller.admin.opus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "作品集目录")
public class OpusDirBaseVO {

    @Schema(description = "作品集uid")
    @NotBlank(message = "作品集uid必填")
    private String opusUid;

    @Schema(description = "目录名称")
    @NotBlank(message = "目录名称必填")
    private String dirName;

    @Schema(description = "目录描述")
    private String dirDesc;

    @Schema(description = "父目录uid")
    private String parentUid;

    @Schema(description = "目录树顺序")
    @NotNull(message = "目录树顺序必填")
    private Integer order;

    public static OpusDirBaseVO defaultDir(String opusUid) {
        OpusDirBaseVO dirBaseVO = new OpusDirBaseVO();
        dirBaseVO.setOpusUid(opusUid);
        dirBaseVO.setDirName("一级目录");
        dirBaseVO.setDirDesc("一级目录");
        dirBaseVO.setOrder(0);
        return dirBaseVO;
    }
}
