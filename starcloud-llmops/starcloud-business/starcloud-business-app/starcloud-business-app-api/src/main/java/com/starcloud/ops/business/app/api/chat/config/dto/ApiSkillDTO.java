package com.starcloud.ops.business.app.api.chat.config.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(description = "API技能配置")
public class ApiSkillDTO extends BasicAbstractSkillsDTO {

    private String url;

    private String method;

    private Boolean needConfirmation;

    private List<Map<String, String>> headers;

    private Object queryParams;

    private Object requestBody;

    private Object responseBody;

    private Boolean validated;

    private String tips;

    private String mediaType;

    private Map<String, String> mediaFormatMaps;

}
