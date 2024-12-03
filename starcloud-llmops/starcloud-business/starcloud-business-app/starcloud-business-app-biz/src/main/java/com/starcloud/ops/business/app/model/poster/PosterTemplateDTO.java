package com.starcloud.ops.business.app.model.poster;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-02
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "PosterTemplateDTO", description = "创作中心图片模板对象")
public class PosterTemplateDTO implements java.io.Serializable {

    private static final long serialVersionUID = 20366484377479364L;

    /**
     * 图片模板UUID
     */
    @Schema(description = "图片模板ID")
    private String uuid;

    /**
     * 海报Code，海报系统海报的唯一code
     */
    @Schema(description = "海报Code")
    @NotBlank(message = "海报code不能为空！")
    private String code;

    /**
     * 图片模板名称
     */
    @Schema(description = "图片模板名称")
    @NotBlank(message = "图片模板名称不能为空!")
    private String name;

    /**
     * 图片模板分组
     */
    @Schema(description = "图片模板分组")
    private Long group;

    /**
     * 图片模板分组名称
     */
    @Schema(description = "图片模板分组名称")
    private String groupName;

    /**
     * 素材分类编号
     */
    @Schema(description = "素材分类编号")
    private Long category;

    /**
     * 应用UID
     */
    @Schema(description = "图片序号")
    private Integer index;

    /**
     * 是否是主图
     */
    @Schema(description = "是否是主图")
    private Boolean isMain;

    /**
     * 图片模板生成模式
     */
    @Schema(description = "图片模板生成模式")
    private String mode;

    /**
     * 图片数量
     */
    @Schema(description = "图片数量")
    private Integer totalImageCount;

    /**
     * 是否进行多模态生成标题，副标题文字内容等
     */
    @Schema(description = "标题生成模式")
    private Boolean isMultimodalTitle;

    /**
     * 多模态生成要求
     */
    @Schema(description = "标题生成要求")
    private String multimodalTitleRequirement;

    /**
     * 是否复制图片
     */
    @Schema(description = "是否复制")
    private Boolean isCopy;

    /**
     * 是否执行
     */
    @Schema(description = "是否执行")
    private Boolean isExecute;

    /**
     * 是否使用所有素材
     */
    @Schema(description = "是否使用所有素材")
    private Boolean isUseAllMaterial;

    /**
     * 是否依赖图片生成结果，目前只针对图片变量<br>
     * 有一个图片变量依赖图片生成结果，则整个模板依赖图片生成结果
     */
    @Schema(description = "是否依赖图片生成结果")
    private Boolean isDependency;

    /**
     * 海报模板描述
     */
    @Schema(description = "海报模板描述")
    private String description;

    /**
     * 示例图片
     */
    @Schema(description = "示例图片")
    private String example;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 图片模板变量
     */
    @Schema(description = "图片模板变量")
    private List<PosterVariableDTO> variableList;

    /**
     * json
     */
    @Schema(description = "json")
    private String json;

    /**
     * 获取图片模板变量列表<br>
     * 过滤空对象
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public List<PosterVariableDTO> posterVariableList() {
        return CollectionUtil.emptyIfNull(this.getVariableList()).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 校验
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate() {
        CollectionUtil.emptyIfNull(this.variableList).forEach(PosterVariableDTO::validate);
    }

}
