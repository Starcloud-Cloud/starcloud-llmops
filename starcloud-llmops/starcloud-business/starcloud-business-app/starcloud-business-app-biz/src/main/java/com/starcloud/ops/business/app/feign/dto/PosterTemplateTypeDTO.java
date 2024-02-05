package com.starcloud.ops.business.app.feign.dto;

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
@Schema(name = "海报模板类型", description = "海报模板类型")
public class PosterTemplateTypeDTO implements java.io.Serializable {

    private static final long serialVersionUID = -3326228285861270645L;

    /**
     * 海报模板类型id
     */
    @Schema(description = "海报模板类型id")
    private String id;

    /**
     * 海报模板类型名称
     */
    @Schema(description = "海报模板类型名称")
    private String label;

    /**
     * 海报模板类型状态
     */
    @Schema(description = "海报模板类型状态")
    private String status;

    /**
     * 海报模板类型排序
     */
    @Schema(description = "海报模板类型排序")
    private Integer order;

    /**
     * 海报模板类型是否删除
     */
    @Schema(description = "海报模板类型是否删除")
    private Integer isDelete;

    /**
     * 海报模板类型创建着用户ID
     */
    @Schema(description = "创建着用户ID")
    private String createUserId;

    /**
     * 海报模板类型创建时间
     */
    @Schema(description = "海报模板类型创建时间")
    private String createAt;

    /**
     * 海报模板类型修改时间
     */
    @Schema(description = "海报模板类型修改时间")
    private String updateAt;

    /**
     * 海报模板类型下的海报模板
     */
    @Schema(description = "海报模板类型下的海报模板")
    private List<PosterTemplate> list;
}
