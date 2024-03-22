package com.starcloud.ops.business.app.api.xhs.scheme.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "创作方案详情")
public class CreativeSchemeTemplateGroupRespVO implements java.io.Serializable {

    private static final long serialVersionUID = -1820002100761132178L;

    /**
     * 父级分类CODE
     */
    private String parentCode;

    /**
     * 应用分类CODE
     */
    @Schema(description = "应用分类CODE")
    private String code;

    /**
     * 应用分类名称
     */
    @Schema(description = "应用分类名称")
    private String name;

    /**
     * 应用分类应用配置
     */
    @Schema(description = "应用分类应用配置")
    private List<CreativeSchemeTemplateRespVO> templateList;

    /**
     * 子级分类
     */
    @Schema(description = "子级分类")
    private List<CreativeSchemeTemplateGroupRespVO> children;
}
