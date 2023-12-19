package com.starcloud.ops.business.app.controller.admin.xhs.scheme.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeReferenceDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
@Schema(name = "CreativeSchemeSseReqVO", description = "创作方案总结参考内容SSE请求")
public class CreativeSchemeSseReqVO implements java.io.Serializable {

    private static final long serialVersionUID = -6918320097140797904L;

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
     * 创作方案模式
     */
    @Schema(description = "创作方案模式")
    @NotBlank(message = "创作方案模式不能为空")
    private String mode;

    /**
     * 创作方案参考
     */
    @Valid
    @NotEmpty(message = "创作方案参考内容不能为空！")
    @Schema(description = "创作方案参考内容")
    private List<CreativeSchemeReferenceDTO> refers;

    /**
     * SSE
     */
    @Schema(description = "SSE")
    @JsonIgnore
    @JSONField(serialize = false)
    private SseEmitter sseEmitter;

}
