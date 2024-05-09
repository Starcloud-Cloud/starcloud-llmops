package com.starcloud.ops.business.app.domain.entity.workflow;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

@Data
public class JsonDocsDefSchema<T> {

    @JsonPropertyDescription("集合")
    private List<T> docs;
}
