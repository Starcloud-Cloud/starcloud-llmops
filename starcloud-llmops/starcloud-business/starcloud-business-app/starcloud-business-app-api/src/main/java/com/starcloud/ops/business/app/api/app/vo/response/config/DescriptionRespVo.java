package com.starcloud.ops.business.app.api.app.vo.response.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "描述")
@NoArgsConstructor
public class DescriptionRespVo {

    private Boolean enabled ;
}
