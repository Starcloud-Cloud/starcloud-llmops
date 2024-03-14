package com.starcloud.ops.business.app.api.xhs.scheme.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "AssembleDTO", description = "内容拼接DTO")
public class AssembleDTO implements Serializable {

    private static final long serialVersionUID = -688287993440188775L;

    /**
     * 标题
     */
    @Schema(description = "标题")
    @JsonPropertyDescription("标题")
    private String title;

    /**
     * 内容
     */
    @Schema(description = "内容")
    @JsonPropertyDescription("内容")
    private String content;

}
