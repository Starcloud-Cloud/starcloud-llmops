package com.starcloud.ops.business.listing.controller.admin.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "导入词库中的关键词")
public class ImportDictReqVO extends DraftReqVO {

    @Schema(description = "词库Uid")
    private List<String> dictUid;

}
