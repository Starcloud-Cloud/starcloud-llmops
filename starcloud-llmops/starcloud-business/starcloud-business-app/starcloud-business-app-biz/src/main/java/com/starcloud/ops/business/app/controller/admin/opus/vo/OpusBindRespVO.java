package com.starcloud.ops.business.app.controller.admin.opus.vo;

import com.starcloud.ops.business.app.model.content.CreativeContentExecuteResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "创作内容绑定")
public class OpusBindRespVO {

    @Schema(description = "bindUid")
    private String bindUid;

    @Schema(description = "作品集uid")
    private String opusUid;

    @Schema(description = "目录uid")
    private String dirUid;

    @Schema(description = "创作内容uid")
    private String creativeContentUid;

    @Schema(description = "创作内容结果")
    private CreativeContentExecuteResult executeResult;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String createUser;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人")
    private String updaterUser;

}
