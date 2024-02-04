package com.starcloud.ops.business.app.api.xhs.plan.dto;

import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterVariableDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
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
@Schema(name = "CreativePlanImageExecuteDTO", description = "小红书图片请求")
public class CreativePlanImageExecuteDTO implements java.io.Serializable {

    private static final long serialVersionUID = -7821921581869329591L;

    /**
     * 图片模板
     */
    @Schema(description = "图片模板")
    @NotBlank(message = "图片模板不能为空")
    private String id;

    /**
     * 应用UID
     */
    @Schema(description = "模板名称")
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
     * 图片生成参数
     */
    @Schema(description = "图片生成参数")
    private List<PosterVariableDTO> params;
}
