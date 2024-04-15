package com.starcloud.ops.business.app.feign.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "海报模板")
public class PosterTemplate implements java.io.Serializable {

    private static final long serialVersionUID = -3326228285861270645L;

    /**
     * 海报模板id
     */
    @Schema(description = "海报模板id")
    private String id;

    /**
     * 海报模板名称
     */
    @Schema(description = "海报模板名称")
    private String label;

    /**
     * 海报模板URL
     */
    @Schema(description = "海报模板URL")
    private String tempUrl;

    /**
     * 海报模板创建时间
     */
    @Schema(description = "创建着用户ID")
    private String createUserId;

    /**
     * 海报模板类型ID
     */
    @Schema(description = "模板类型ID")
    private String templateTypeId;

    /**
     * 海报模板参数
     */
    @Schema(description = "参数")
    private List<PosterParam> params;

    /**
     * 海报JSON配置
     */
    @Schema(description = "海报JSON配置")
    private String json;

}
