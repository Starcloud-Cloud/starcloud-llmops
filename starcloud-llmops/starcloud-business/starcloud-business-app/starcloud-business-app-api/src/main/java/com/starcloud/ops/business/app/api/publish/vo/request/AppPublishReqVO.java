package com.starcloud.ops.business.app.api.publish.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-25
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AppPublishReqVO", description = "应用发布请求")
public class AppPublishReqVO implements Serializable {

    private static final long serialVersionUID = 1645079047517976084L;

    /**
     * 应用 UID
     */
    @Schema(description = "应用 UID")
    @NotBlank(message = "应用 UID 不能为空")
    private String appUid;

    /**
     * 应用语言
     */
    @Schema(description = "应用语言")
    private String language;

    /**
     * 应用市场类型
     */
    @Schema(description = "应用市场类型")
    private String type;

    /**
     * 应用市场排序
     */
    @Schema(description = "应用市场排序")
    private Long sort;

}
