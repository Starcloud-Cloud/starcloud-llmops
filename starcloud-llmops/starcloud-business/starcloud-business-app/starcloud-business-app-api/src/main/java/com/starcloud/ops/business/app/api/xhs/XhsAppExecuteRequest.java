package com.starcloud.ops.business.app.api.xhs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
@Schema(name = "XhsAppRequest", description = "小红书应用请求")
public class XhsAppExecuteRequest implements java.io.Serializable {

    private static final long serialVersionUID = 8420397508429949580L;

    /**
     * 应用UID
     */
    @Schema(description = "应用UID")
    @NotBlank(message = "应用UID不能为空")
    private String uid;

    /**
     * 应用生成参数
     */
    @Schema(description = "应用生成参数")
    private Map<String, Object> params;
}
