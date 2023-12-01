package com.starcloud.ops.business.app.controller.admin.xhs.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0
 * @since 2021/9/15
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "创建创作计划")
public class XhsCreativeContentBusinessReq implements Serializable {

    private static final long serialVersionUID = 7972914997707881764L;

    /**
     * 创作方案UID
     */
    @Schema(description = "业务uid")
    @NotBlank(message = "业务uid 不能为空")
    private String businessUid;

}
