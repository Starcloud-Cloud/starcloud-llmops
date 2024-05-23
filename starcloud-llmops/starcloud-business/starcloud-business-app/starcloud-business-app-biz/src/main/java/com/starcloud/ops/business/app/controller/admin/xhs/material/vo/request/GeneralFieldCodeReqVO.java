package com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request;

import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "生成字段code")
public class GeneralFieldCodeReqVO {

    @NotNull(message = "字段配置不能为空")
    @Schema(description = "字段配置集合")
    private List<MaterialFieldConfigDTO> fieldConfigDTOList;

}
