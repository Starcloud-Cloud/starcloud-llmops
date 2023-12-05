package com.starcloud.ops.business.app.api.xhs.plan.dto;

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
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "CreativePlanImageStyleExecuteDTO", description = "小红书风格请求")
public class CreativePlanImageStyleExecuteDTO implements java.io.Serializable {

    private static final long serialVersionUID = 8736167940767217842L;

    /**
     * 风格id
     */
    @Schema(description = "风格id")
    private String id;

    /**
     * 风格名称
     */
    @Schema(description = "风格名称")
    private String name;

    /**
     * 图片执行
     */
    @Schema(description = "图片执行")
    private List<CreativePlanImageExecuteDTO> imageRequests;
}
