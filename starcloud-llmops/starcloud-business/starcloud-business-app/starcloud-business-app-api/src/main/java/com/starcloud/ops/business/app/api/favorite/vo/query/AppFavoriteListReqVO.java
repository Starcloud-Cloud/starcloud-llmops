package com.starcloud.ops.business.app.api.favorite.vo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-24
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class AppFavoriteListReqVO implements Serializable {

    private static final long serialVersionUID = 1123451879829266028L;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用名称拼音
     */
    @Schema(description = "应用名称拼音")
    private String spell;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", hidden = true)
    private String userId;

    /**
     * 应用模型
     */
    @Schema(description = "应用模型")
    private String model;

    /**
     * 分类
     */
    @Schema(description = "分类")
    private String category;
}
