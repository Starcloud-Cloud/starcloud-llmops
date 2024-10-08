package com.starcloud.ops.business.app.api.xhs.scheme.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创作方案DO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "创作方案详情")
public class CreativeSchemeListOptionRespVO implements java.io.Serializable {

    private static final long serialVersionUID = 347884633370912370L;

    /**
     * 创作方案UID
     */
    @Schema(description = "创作方案UID")
    private String uid;

    /**
     * 创作方案名称
     */
    @Schema(description = "创作方案名称")
    private String name;

    /**
     * 资料库类型
     */
    @Schema(description = "资料库类型")
    private String materialType;

    /**
     * 资料库类型名称
     */
    @Schema(description = "资料库类型名称")
    private String materialTypeName;

    /**
     * 创作方案描述
     */
    @Schema(description = "创作方案描述")
    private String description;

    /**
     * 创作方案变量列表
     */
    @Schema(description = "创作方案变量列表")
    private List<VariableItemRespVO> variableList;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 创作方案标签
     */
    @Schema(description = "创作方案标签")
    private List<String> tags;


}
