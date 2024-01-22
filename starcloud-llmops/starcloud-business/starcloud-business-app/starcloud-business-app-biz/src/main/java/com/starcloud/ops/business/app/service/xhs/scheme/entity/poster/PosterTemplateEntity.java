package com.starcloud.ops.business.app.service.xhs.scheme.entity.poster;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;
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
@Schema(name = "PosterTemplateEntity", description = "创作中心图片模板对象")
public class PosterTemplateEntity implements java.io.Serializable {

    private static final long serialVersionUID = 20366484377479364L;

    /**
     * 图片模板ID
     */
    @Schema(description = "图片模板ID")
    @NotBlank(message = "图片模板ID不能为空！")
    private String id;

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
     * 图片数量
     */
    @Schema(description = "图片数量")
    private Integer imageNumber;

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
    private List<PosterVariableEntity> variableList;

    /**
     * 校验
     */
    public void validate() {
        if (StringUtils.isBlank(id)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_ID_REQUIRED);
        }
        if (CollectionUtil.isEmpty(this.variableList)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_PARAMS_REQUIRED);
        }
    }

    /**
     * 获取变量为 标题 的变量列表
     *
     * @return 标题变量列表
     */
    public List<PosterVariableEntity> getVariableTitleListByModel(String model) {
        return CollectionUtil.emptyIfNull(this.variableList).stream()
                .filter(variableItem -> "TITLE".equals(variableItem.getField()))
                .filter(variableItem -> model.equals(variableItem.getModel()))
                .collect(Collectors.toList());
    }

    /**
     * 获取主图模板
     *
     * @return 主图模板
     */
    public static PosterTemplateEntity ofMain() {
        PosterTemplateEntity posterTemplate = new PosterTemplateEntity();
        posterTemplate.setId("");
        posterTemplate.setName("首图");
        posterTemplate.setImageNumber(0);
        posterTemplate.setExample("");
        posterTemplate.setVariableList(Collections.emptyList());
        return posterTemplate;
    }

}
