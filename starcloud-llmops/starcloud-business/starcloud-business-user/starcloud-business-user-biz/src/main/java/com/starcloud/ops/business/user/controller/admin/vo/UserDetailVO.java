package com.starcloud.ops.business.user.controller.admin.vo;

import cn.iocoder.yudao.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "用户明细")
public class UserDetailVO {

    @Schema(description = "用户编号", required = true, example = "1")
    private Long id;

    @Schema(description = "状态,参见 CommonStatusEnum 枚举类", required = true, example = "1")
    private Integer status;

    @Schema(description = "用户账号", required = true, example = "yudao")
    private String username;

    @Schema(description = "用户昵称", required = true, example = "芋艿")
    private String nickname;

    @Schema(description = "备注", example = "我是一个用户")
    private String remark;

    @Schema(description = "用户邮箱", example = "yudao@iocoder.cn")
    private String email;

    @Schema(description = "手机号码", example = "15601691300")
    @Mobile
    private String mobile;

    @Schema(description = "用户性别,参见 SexEnum 枚举类", example = "1")
    private Integer sex;

    @Schema(description = "用户头像", example = "https://www.iocoder.cn/xxx.png")
    private String avatar;

    @Schema(description = "邀请码")
    private String inviteCode;

    @Schema(description = "邀请链接")
    private String inviteUrl;


}
