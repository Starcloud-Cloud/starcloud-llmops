package com.starcloud.ops.business.app.feign.request.poster;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "PosterTemplateQuery", description = "海报请求")
public class PosterTemplateQuery implements java.io.Serializable {

    private static final long serialVersionUID = -3977808759871963692L;

    /**
     * 模板类型ID
     */
    @Schema(description = "模板类型ID")
    private String templateTypeId;
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String createUserId;

}
