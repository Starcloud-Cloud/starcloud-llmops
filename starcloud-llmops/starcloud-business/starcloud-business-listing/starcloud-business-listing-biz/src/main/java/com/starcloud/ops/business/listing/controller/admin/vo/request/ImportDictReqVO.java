package com.starcloud.ops.business.listing.controller.admin.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "导入词库中的关键词")
public class ImportDictReqVO {

    @Schema(description = "草稿Uid")
    @NotBlank(message = "草稿uid不能为空")
    private String uid;

    @Schema(description = "版本")
    @Min(value = 1, message = "草稿版本必须大于零")
    private Integer version;

    @Schema(description = "词库Uid")
    private List<String> dictUid;
}
