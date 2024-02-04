package com.starcloud.ops.business.user.controller.admin.invite.vo.rule;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.starcloud.ops.business.user.dal.dataobject.invite.AdminUserInviteRuleDO;
import com.starcloud.ops.business.user.enums.invite.InviteRuleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 邀请记录 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class AdminUserInviteRuleBaseVO {

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @NotBlank(message = "规则名称不能为空")
    @Size(min = 4, max = 30, message = "规则名称长度为 4-30 个字符")
    private String name;

    @Schema(description = "邀请规则类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "邀请规则类型不可以为空")
    @InEnum(value = InviteRuleTypeEnum.class, message = "邀请规则类型必须是 {value}")
    private Integer type;


    @Schema(description = "时间范围", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "时间范围不可以为空")
    private Integer timeNums;

    @Schema(description = "时间单位", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    @NotNull(message = "时间单位不可以为空")
    @InEnum(value = TimeRangeTypeEnum.class, message = "时间单位必须是 {value}")
    private Integer timeRange;


    @Schema(description = "规则", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    List<AdminUserInviteRuleDO.Rule> inviteRule;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    @InEnum(value = CommonStatusEnum.class, message = "状态必须是 {value}")
    private Integer status;




}
