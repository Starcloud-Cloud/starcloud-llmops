package com.starcloud.ops.business.app.api.scheme.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

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
public class CreativeSchemeModifyReqVO extends CreativeSchemeReqVO {

    private static final long serialVersionUID = -3509797280068850307L;

    /**
     * 创作方案名称
     */
    @NotBlank(message = "创作方案UID不能为空")
    @Schema(description = "创作方案UID")
    private String uid;


}
