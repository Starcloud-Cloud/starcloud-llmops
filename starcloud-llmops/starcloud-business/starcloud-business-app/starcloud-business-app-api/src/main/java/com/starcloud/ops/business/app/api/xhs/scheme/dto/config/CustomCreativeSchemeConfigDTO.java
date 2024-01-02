package com.starcloud.ops.business.app.api.xhs.scheme.dto.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "自定义创作方案配置")
public class CustomCreativeSchemeConfigDTO implements java.io.Serializable {

    private static final long serialVersionUID = -1278431625060498370L;

    /**
     * 创作方案步骤
     */
    @Schema(description = "创作方案步骤")
    private List<CreativeSchemeStepDTO> steps;

}
