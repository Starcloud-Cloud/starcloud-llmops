package com.starcloud.ops.business.app.api.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "模板", description = "模板")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequestParams {

    private Long id;

    private String templateId;

    private Integer version;

    private String operate;


}
