package com.starcloud.ops.business.listing.controller.admin.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "草稿操作")
public class DraftOperationReqVO {

    @Schema(description = "草稿uid")
    @NotBlank(message = "草稿uid不能为空")
    private String uid;

    @Schema(description = "草稿版本")
    @Min(value = 1, message = "草稿版本必须大于0")
    private Integer version;
}
