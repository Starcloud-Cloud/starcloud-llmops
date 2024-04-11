package com.starcloud.ops.business.app.api.xhs.plan.dto.poster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.AppValidate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;

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
     * 标题生成模式
     */
    @Schema(description = "标题生成模式")
    private String titleGenerateMode;

    /**
     * 标题生成规则
     */
    @Schema(description = "标题生成要求")
    private String titleGenerateRequirement;

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
     * 校验
     */
    public void validate() {
        AppValidate.notEmpty(this.variableList, "缺少系统必填项！图片模板变量不能为空！请联系管理员！");
        this.variableList.forEach(PosterVariableDTO::validate);
    }

    /**
     * 获取主图模板
     *
     * @return 主图模板
     */
    public static PosterTemplateDTO ofMain() {
        PosterTemplateDTO posterTemplate = new PosterTemplateDTO();
        posterTemplate.setName("首图");
        posterTemplate.setTotalImageCount(0);
        posterTemplate.setExample("");
        posterTemplate.setVariableList(Collections.emptyList());
        return posterTemplate;
    }
}
