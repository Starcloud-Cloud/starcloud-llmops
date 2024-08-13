package cn.iocoder.yudao.module.system.controller.admin.socail.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Schema(description = "管理后台 - 社交绑定 Request VO，使用 code 授权码")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialUserBindReqVO {

    @Schema(description = "社交平台的类型，参见 UserSocialTypeEnum 枚举值", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @InEnum(SocialTypeEnum.class)
    @NotNull(message = "社交平台的类型不能为空")
    private Integer type;

    @Schema(description = "授权码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "授权码不能为空")
    private String code;

    @Schema(description = "state", requiredMode = Schema.RequiredMode.REQUIRED, example = "9b2ffbc1-7425-4155-9894-9d5c08541d62")
    // @NotEmpty(message = "state 不能为空")
    private String state;

    /**
     * 绑定方式 自动/手动
     */
    @Schema(description = "备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "我是一条备注")
    @Size(max = 200, message = "备注仅限 200 个字符")
    private String remark;

    /**
     * 绑定方式 自动/手动
     */
    @Schema(description = "绑定方式 自动/手动", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    // @NotNull(message = "绑定方式 自动/手动 不能为空")
    private Boolean auto = true;


}
