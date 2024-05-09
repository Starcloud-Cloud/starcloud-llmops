package com.starcloud.ops.business.app.domain.entity.workflow;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class JsonDataDefSchema {

    @JsonPropertyDescription("返回结果")
    private String data;
}
