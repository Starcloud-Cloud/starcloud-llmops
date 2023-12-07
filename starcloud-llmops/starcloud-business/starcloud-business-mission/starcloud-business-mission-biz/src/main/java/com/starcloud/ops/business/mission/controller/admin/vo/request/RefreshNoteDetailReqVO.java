package com.starcloud.ops.business.mission.controller.admin.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Pattern;

import static com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants.XHS_URL_REGEX;

@Data
@Schema(description = "更新互动数据")
public class RefreshNoteDetailReqVO {

    @Schema(description = "任务uid")
    private String uid;

    @Schema(description = "小红书url")
    @Pattern(regexp = XHS_URL_REGEX, message = "发布链接为浏览器访问地址，如： https://www.xiaohongshu.com/explore/24位数字和字母")
    private String publishUrl;
}
