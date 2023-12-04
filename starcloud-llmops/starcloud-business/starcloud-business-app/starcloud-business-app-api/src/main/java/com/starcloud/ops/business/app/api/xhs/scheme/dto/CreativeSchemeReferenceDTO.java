package com.starcloud.ops.business.app.api.xhs.scheme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "CreativeSchemeReferenceDTO", description = "创作方案配置")
public class CreativeSchemeReferenceDTO implements java.io.Serializable {

    private static final long serialVersionUID = -8215160775720598169L;

    /**
     * 参考ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 参考标题
     */
    @Schema(description = "标题")
    @NotBlank(message = "参考账号：标题不能为空！")
    private String title;

    /**
     * 参考内容
     */
    @Schema(description = "内容")
    @NotBlank(message = "参考账号：内容不能为空！")
    private String content;

    /**
     * 参考来源
     */
    @Schema(description = "来源")
    @NotBlank(message = "参考账号：来源不能为空！")
    private String source;

    /**
     * 参考链接
     */
    @Schema(description = "链接")
    private String link;

    /**
     * 参考图片
     */
    @Schema(description = "图片")
    private List<ReferenceImageDTO> images;


}
