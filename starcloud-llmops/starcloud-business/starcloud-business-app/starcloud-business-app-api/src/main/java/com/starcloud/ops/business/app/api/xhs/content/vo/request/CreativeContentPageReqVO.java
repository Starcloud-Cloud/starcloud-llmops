package com.starcloud.ops.business.app.api.xhs.content.vo.request;

import com.starcloud.ops.framework.common.api.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "分页查询创作内容")
public class CreativeContentPageReqVO extends PageQuery {

    private static final long serialVersionUID = 7207651024270440957L;

    /**
     * 执行批次UID
     */
    @Schema(description = "计划执行批次")
    private String batchUid;

    /**
     * 创作计划UID
     */
    @Schema(description = "创作计划Uid")
    private String planUid;

    /**
     * 创作内容类型
     */
    @Schema(description = "创作内容类型")
    private String type;

    /**
     * 任务状态
     */
    @Schema(description = "任务状态")
    private String status;

    /**
     * 是否绑定
     */
    @Schema(description = "是否绑定")
    private Boolean claim;

    /**
     * 是否喜欢
     */
    @Schema(description = "是否喜欢")
    private Boolean liked;

    @Schema(description = "是否倒序")
    private Boolean desc;

}
