package com.starcloud.ops.business.app.controller.admin.xhs.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Pattern;

import static com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants.XHS_URL_REGEX;

@Data
@Schema(description = "查询小红书笔记内容")
public class NoteDetailQueryReqVO {

    @Schema(description = "笔记url")
    @Pattern(regexp = XHS_URL_REGEX, message = "发布链接为浏览器访问地址，如： https://www.xiaohongshu.com/explore/24位数字和字母")
    private String noteUrl;
}