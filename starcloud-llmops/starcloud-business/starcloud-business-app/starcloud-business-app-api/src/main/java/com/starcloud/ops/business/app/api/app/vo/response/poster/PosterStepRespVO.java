package com.starcloud.ops.business.app.api.app.vo.response.poster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.action.ActionRespVO;
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
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "海报步骤响应对象 VO")
public class PosterStepRespVO extends ActionRespVO {

    private static final long serialVersionUID = 94283505031096681L;

    /**
     * 是否自动执行
     */
    @Schema(description = "是否自动执行")
    private Boolean isAuto;

    /**
     * 是否是可编辑步骤
     */
    @Schema(description = "是否是可编辑步骤")
    private Boolean isCanEditStep;

    /**
     * 步骤版本，默认版本 1.0.0
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
    private List<PosterStyleRespVO> styleList;
}
