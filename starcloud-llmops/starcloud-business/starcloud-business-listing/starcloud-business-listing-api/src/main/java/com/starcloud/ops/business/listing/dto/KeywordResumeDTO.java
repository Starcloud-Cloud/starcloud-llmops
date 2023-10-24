package com.starcloud.ops.business.listing.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "关键词摘要")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeywordResumeDTO {


    @Schema(description = "关键词")
    private String key;

    @Schema(description = "关键词Id")
    private Long bindId;

    public KeywordResumeDTO(String key) {
        this.key = key;
    }

}
