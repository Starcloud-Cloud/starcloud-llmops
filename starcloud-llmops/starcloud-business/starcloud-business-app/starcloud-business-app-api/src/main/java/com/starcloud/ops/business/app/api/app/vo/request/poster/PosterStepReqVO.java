package com.starcloud.ops.business.app.api.app.vo.request.poster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.request.action.ActionReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "海报步请求应对象 VO")
public class PosterStepReqVO extends ActionReqVO {

    private static final long serialVersionUID = 8739880429063127513L;

    /**
     * 是否自动执行
     */
    @Schema(description = "是否自动执行", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否自动执行不能为空")
    private Boolean isAuto;

    /**
     * 是否是可编辑步骤
     */
    @Schema(description = "是否是可编辑步骤", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否是可编辑步骤不能为空")
    private Boolean isCanEditStep;

    /**
     * 步骤版本，默认版本 1
     */
    @Schema(description = "步骤版本，默认版本 1")
    private Integer version;

    /**
     * 步骤标签
     */
    @Schema(description = "步骤标签")
    private List<String> tags;

    /**
     * 步骤场景
     */
    @Schema(description = "步骤场景")
    private List<String> scenes;

    /**
     * 步骤图标
     */
    @Schema(description = "步骤图标")
    private String icon;

    /**
     * 图片素材
     */
    @Schema(description = "图片素材")
    private List<String> imageMaterials;

    /**
     * 图片风格列表
     */
    @Schema(description = "图片风格列表")
    @Valid
    private List<PosterStyleReqVO> styleList;
}
