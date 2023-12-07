package com.starcloud.ops.business.mission.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants.XHS_URL_REGEX;

@Data
@Schema(description = "认领任务")
public class ClaimReqVO {

    @Schema(description = "任务uid")
    @NotBlank(message = "任务uid不能为空")
    private String uid;

    @Schema(description = "认领人")
    @NotBlank(message = "认领人不能为空")
    private String claimUsername;

    @Schema(description = "发布链接", example ="https://www.xiaohongshu.com/explore/24位数字和字母")
    @Pattern(regexp = XHS_URL_REGEX, message = "发布链接为浏览器访问地址，如： https://www.xiaohongshu.com/explore/24位数字和字母")
    @NotBlank(message = "发布链接不能为空")
    private String publishUrl;


}
