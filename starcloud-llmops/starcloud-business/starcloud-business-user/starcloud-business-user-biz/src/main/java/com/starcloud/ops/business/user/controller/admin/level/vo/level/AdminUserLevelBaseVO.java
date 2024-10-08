package com.starcloud.ops.business.user.controller.admin.level.vo.level;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 会员等级记录 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class AdminUserLevelBaseVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "25923")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "等级编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "25985")
    @NotNull(message = "等级编号不能为空")
    private Long levelId;


    @Schema(description = "等级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "25985")
    @NotNull(message = "等级名称不能为空")
    private String levelName;


    /**
     * 业务编码
     */
    @Schema(description = "会员等级业务编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "会员等级业务编码不能为空")
    private String bizId;
    /**
     * 业务类型
     * <p>
     * 枚举 {@link AdminUserRightsBizTypeEnum}
     */
    @Schema(description = "会员等级业务类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "会员等级业务类型不能为空")
    @InEnum(value = AdminUserRightsBizTypeEnum.class, message = "业务类型[{value}]必须是: {values}")
    private Integer bizType;


    @Schema(description = "备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "推广需要")
    private String remark;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "升级为金牌会员")
    @NotNull(message = "描述不能为空")
    private String description;

}
