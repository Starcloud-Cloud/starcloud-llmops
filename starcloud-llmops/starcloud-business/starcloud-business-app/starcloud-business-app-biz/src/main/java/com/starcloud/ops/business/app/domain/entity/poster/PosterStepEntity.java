package com.starcloud.ops.business.app.domain.entity.poster;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionEntity;
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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PosterStepEntity extends ActionEntity {

    private static final long serialVersionUID = -6115973199460489691L;

    /**
     * 是否自动执行
     */
    private Boolean isAuto;

    /**
     * 是否是可编辑步骤
     */
    private Boolean isCanEditStep;

    /**
     * 步骤版本，默认版本 1
     */
    private Integer version;

    /**
     * 步骤标签
     */
    private List<String> tags;

    /**
     * 步骤场景
     */
    private List<String> scenes;

    /**
     * 步骤图标
     */
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
    private List<PosterStyleEntity> styleList;

    /**
     * Action 校验
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate() {

    }

}
