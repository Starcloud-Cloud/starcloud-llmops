package com.starcloud.ops.business.app.api.scheme.vo.request;

import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeConfigDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeReferenceDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创作方案DO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class CreativeSchemeReqVO implements java.io.Serializable {

    private static final long serialVersionUID = 33863755137653429L;

    /**
     * 创作方案名称
     */
    @NotBlank(message = "创作方案名称不能为空")
    @Schema(description = "创作方案名称")
    private String name;

    /**
     * 创作方案类型
     */
    @Schema(description = "创作方案类型")
    private String type;

    /**
     * 创作方案类目
     */

    @NotBlank(message = "创作方案类目不能为空")
    @Schema(description = "创作方案类目")
    private String category;

    /**
     * 创作方案标签
     */
    @Schema(description = "创作方案标签")
    private List<String> tags;

    /**
     * 创作方案描述
     */
    @Schema(description = "创作方案描述")
    private String description;

    /**
     * 创作方案参考
     */
    @Valid
    @NotEmpty(message = "创作方案参考账号不能为空！")
    @Schema(description = "创作方案参考账号")
    private List<CreativeSchemeReferenceDTO> references;

    /**
     * 创作方案配置信息
     */
    @Valid
    @NotNull(message = "创作方案配置信息不能为空！")
    @Schema(description = "创作方案配置信息")
    private CreativeSchemeConfigDTO configuration;

}
