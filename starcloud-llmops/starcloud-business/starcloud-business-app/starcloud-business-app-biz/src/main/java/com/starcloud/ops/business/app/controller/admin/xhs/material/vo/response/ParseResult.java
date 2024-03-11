package com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response;

import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "解析结果")
public class ParseResult {

    @Schema(description = "解析完成")
    private boolean complete;

    @Schema(description = "素材明细")
    List<AbstractBaseCreativeMaterialDTO> materialDTOList;
}
