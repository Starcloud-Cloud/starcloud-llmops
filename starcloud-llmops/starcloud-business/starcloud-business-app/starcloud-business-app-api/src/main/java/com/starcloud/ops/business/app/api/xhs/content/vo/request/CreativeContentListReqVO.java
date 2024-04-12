package com.starcloud.ops.business.app.api.xhs.content.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "查询列表创作内容")
public class CreativeContentListReqVO implements java.io.Serializable {

    private static final long serialVersionUID = -4965589480403300084L;

    /**
     * 创作计划UID集合
     */
    @Schema(description = "创作计划UID列表")
    private List<String> uidList;

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

}
