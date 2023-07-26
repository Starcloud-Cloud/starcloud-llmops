package com.starcloud.ops.business.app.api.app.vo.request.config;


import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "应用变量VO")
public class VariableReqVO {

    /**
     * 应用变量
     */
    @Schema(description = "应用变量")
    private List<VariableItemRespVO> variables;
}
