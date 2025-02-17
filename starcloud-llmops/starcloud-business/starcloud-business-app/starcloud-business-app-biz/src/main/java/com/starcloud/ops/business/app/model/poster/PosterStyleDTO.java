package com.starcloud.ops.business.app.model.poster;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.AppValidate;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "创作中心图片风格对象")
public class PosterStyleDTO implements java.io.Serializable {

    private static final long serialVersionUID = 3693634357817132472L;

    /**
     * 风格id
     */
    @Schema(description = "风格UUID")
    @NotBlank(message = "风格UUID不能为空！")
    private String uuid;

    /**
     * 风格序号
     */
    @Schema(description = "风格序号")
    private Integer index;

    /**
     * 风格名称
     */
    @Schema(description = "风格名称")
    @NotBlank(message = "风格名称不能为空！")
    private String name;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    @NotNull(message = "是否启用不能为空！")
    private Boolean enable;

    /**
     * 是否是系统风格
     */
    @Schema(description = "是否是系统风格")
    private Boolean system;

    /**
     * 该风格下的图片类型变量总数量
     */
    @Schema(description = "该风格下的图片类型变量总数量")
    private Integer totalImageCount;

    /**
     * 海报风格描述
     */
    @Schema(description = "海报风格描述")
    private String description;

    /**
     * 所有参数为空时候不执行<br>
     * {@code true} 所有参数为空时候不执行<br>
     * {@code null}或者{@code false} 所有参数为空时候报错。
     */
    @Schema(description = "所有参数为空时候不执行")
    private Boolean noExecuteIfEmpty;

    /**
     * 模板列表
     */
    @Schema(description = "模板列表")
    @Valid
    @NotEmpty(message = "请选择图片模板！")
    private List<PosterTemplateDTO> templateList;

    @Schema(description = "售卖配置")
    @Valid
    private SaleConfigDTO saleConfig;

    /**
     * 示例图片
     */
    @Schema(description = "示例图片")
    public String getExample() {
        return CollectionUtil.emptyIfNull(this.posterTemplateList()).stream()
                .filter(posterTemplateDTO -> StrUtil.isNotBlank(posterTemplateDTO.getExample()))
                .findFirst().map(PosterTemplateDTO::getExample).orElse("");
    }

    /**
     * 获取海报模板列表<br>
     * 过滤空对象
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public List<PosterTemplateDTO> posterTemplateList() {
        return CollectionUtil.emptyIfNull(this.getTemplateList()).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 校验风格对象
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate() {
        AppValidate.notBlank(uuid, "{}, 风格UUID不能为空！请联系管理员！", this.name);
        AppValidate.notEmpty(templateList, "{}, 请选择图片模板！", this.name);
        templateList.forEach(PosterTemplateDTO::validate);
    }


}
