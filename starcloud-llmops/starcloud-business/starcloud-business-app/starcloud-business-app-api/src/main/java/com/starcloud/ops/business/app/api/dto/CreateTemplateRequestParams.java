package com.starcloud.ops.business.app.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
public class CreateTemplateRequestParams {

    private String desc;

    private String icon;

    @JsonProperty("info")
    private Object config;

    private String name;

    private List<String> scenes;

    private List<String> stepIcons;

    private List<String> tags;

    @JsonProperty("temp_key")
    private String tempKey;

    @JsonProperty("template_id")
    private String templateId;

    private List<String> topics;

    private String type;

    private Integer version;

    @JsonProperty("_variableToTips")
    private Object variableToTips;

}
