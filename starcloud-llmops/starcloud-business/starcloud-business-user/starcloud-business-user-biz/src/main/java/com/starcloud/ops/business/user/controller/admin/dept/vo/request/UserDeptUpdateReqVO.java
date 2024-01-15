package com.starcloud.ops.business.user.controller.admin.dept.vo.request;

import cn.iocoder.yudao.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Schema(description = "部门更新 Request VO")
@Data
public class UserDeptUpdateReqVO {

    @Schema(description = "部门id")
    @NotNull(message = "部门id不能为空")
    private Long id;

    @Schema(description = "部门名称")
    @Size(max = 30, message = "部门名称长度不能超过30个字符")
    private String name;

    @Schema(description = "联系电话")
    @Mobile(message = "联系电话格式不正确")
    private String phone;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    @Schema(description = "部门头像")
    private String avatar;

    @Schema(description = "父菜单 ID")
    private Long parentId;

}
