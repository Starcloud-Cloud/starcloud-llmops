package com.starcloud.ops.business.app.api.xhs.execute;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

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
@Schema(name = "XhsAppExecuteRequest", description = "小红书应用请求")
public class XhsAppExecuteRequest implements java.io.Serializable {

    private static final long serialVersionUID = 8420397508429949580L;

    /**
     * 应用UID
     */
    @Schema(description = "应用UID")
    @NotBlank(message = "应用UID不能为空")
    private String uid;

    /**
     * 步骤
     */
    private String step;

    /**
     * 场景
     */
    @Schema(description = "场景")
    private String scene;


    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 步骤ID
     */
    @Schema(description = "步骤ID")
    private String stepId;

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
    private Map<String, Object> params;

}