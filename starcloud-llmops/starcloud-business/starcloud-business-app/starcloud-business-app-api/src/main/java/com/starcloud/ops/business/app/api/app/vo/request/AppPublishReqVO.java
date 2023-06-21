package com.starcloud.ops.business.app.api.app.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-14
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用发布到应用市场请求对象")
public class AppPublishReqVO implements Serializable {

    private static final long serialVersionUID = -259649117327475212L;

    /**
     * 应用 UID
     */
    @Schema(description = "应用 UID")
    @NotBlank(message = "应用 UID 不能为空")
    private String uid;

    /**
     * 应用分类
     */
    @Schema(description = "应用分类")
    @NotEmpty(message = "应用分类不能为空")
    private List<String> categories;

    /**
     * 应用市场语言
     */
    @Schema(description = "应用市场语言")
    @NotBlank(message = "应用市场语言不能为空")
    private String language;

    /**
     * 应用市场示例 Example
     */
    @Schema(description = "模版市场示例 Example")
    private String example;


}
