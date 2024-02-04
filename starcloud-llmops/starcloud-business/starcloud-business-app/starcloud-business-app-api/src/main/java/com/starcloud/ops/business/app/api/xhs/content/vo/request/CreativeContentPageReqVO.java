package com.starcloud.ops.business.app.api.xhs.content.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页查询创作内容")
public class CreativeContentPageReqVO extends PageParam {

    /**
     * 创作方案UID
     */
    @Schema(description = "创作方案UID")
    private String schemeUid;

    @Schema(description = "创作计划Uid")
    private String planUid;

    @Schema(description = "任务状态", example = "execute_success")
    private String status;

    @Schema(description = "是否绑定")
    private Boolean claim;

    @Schema(description = "是否测试")
    private Boolean isTest;
}
