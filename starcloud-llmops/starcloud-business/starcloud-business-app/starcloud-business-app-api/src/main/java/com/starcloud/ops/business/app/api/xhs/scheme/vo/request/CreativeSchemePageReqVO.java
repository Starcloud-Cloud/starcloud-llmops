package com.starcloud.ops.business.app.api.xhs.scheme.vo.request;

import com.starcloud.ops.framework.common.api.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

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
@Schema(name = "CreativeSchemePageReqVO", description = "创作方案分页查询请求")
public class CreativeSchemePageReqVO extends PageQuery {

    private static final long serialVersionUID = -1651898420947771210L;

    /**
     * 创作方案名称
     */
    @Schema(description = "创作方案名称")
    private String name;

    /**
     * 创作方案类目
     */
    @Schema(description = "创作方案类目")
    private String category;

    /**
     * 创作方案标签
     */
    @Schema(description = "创作方案标签")
    private List<String> tags;

    /**
     * 创作方式
     */
    @Schema(description = "创作方式")
    private List<String> mode;

}
