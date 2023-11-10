package com.starcloud.ops.business.app.api.xhs;

import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-02
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "XhsAppResponse", description = "小红书应用返回结果")
public class XhsAppResponse implements java.io.Serializable {

    private static final long serialVersionUID = 4792565400771191775L;

    /**
     * 应用 UID, 每个应用的唯一标识
     */
    @Schema(description = "应用 UID, 每个应用的唯一标识")
    private String uid;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用类别
     */
    @Schema(description = "应用类别")
    private String category;

    /**
     * 应用图标
     */
    @Schema(description = "应用图标")
    private String icon;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 变量
     */
    @Schema(description = "变量")
    List<VariableItemRespVO> variables;


}
