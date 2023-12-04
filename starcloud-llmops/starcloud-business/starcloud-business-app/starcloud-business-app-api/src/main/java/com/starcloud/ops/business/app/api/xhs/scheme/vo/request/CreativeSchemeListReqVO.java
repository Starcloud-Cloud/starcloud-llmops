package com.starcloud.ops.business.app.api.xhs.scheme.vo.request;

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
@Schema(name = "CreativeSchemeListReqVO", description = "创作方案列表请求")
public class CreativeSchemeListReqVO implements java.io.Serializable {

    private static final long serialVersionUID = 2885674454654361146L;

    /**
     * UID 列表
     */
    @Schema(description = "UID")
    private List<String> uidList;

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
     * 登录用户ID
     */
    @Schema(description = "登录用户ID")
    private String loginUserId;

    /**
     * 是否管理员
     */
    @Schema(description = "是否管理员")
    private Boolean isAdmin;
}
