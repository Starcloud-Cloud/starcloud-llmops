package com.starcloud.ops.business.app.api.app.vo.request.config;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "描述")
@AllArgsConstructor
@NoArgsConstructor
public class DescriptionReqVo {

    private Boolean enabled;
}
