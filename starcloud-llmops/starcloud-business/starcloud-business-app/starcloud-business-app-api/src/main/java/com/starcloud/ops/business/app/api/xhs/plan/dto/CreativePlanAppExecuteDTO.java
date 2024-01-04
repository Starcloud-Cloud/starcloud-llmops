package com.starcloud.ops.business.app.api.xhs.plan.dto;

import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "CreativePlanAppExecuteDTO", description = "创作计划应用执行配置信息！")
public class CreativePlanAppExecuteDTO implements java.io.Serializable {

    private static final long serialVersionUID = -5784905584237449503L;

    /**
     * 应用UID
     */
    @Schema(description = "应用UID")
    @NotBlank(message = "应用UID不能为空")
    private String uid;

    /**
     * 场景
     */
    @Schema(description = "场景")
    private String scene;

    /**
     * 生成数量
     */
    @Schema(description = "生成数量")
    @NotNull(message = "生成条数不能为空！")
    @Min(value = 1, message = "生成条数不能小于1！")
    private Integer n;

    /**
     * 应用生成参数
     */
    @Schema(description = "应用生成参数")
    private List<VariableItemRespVO> params;

}
