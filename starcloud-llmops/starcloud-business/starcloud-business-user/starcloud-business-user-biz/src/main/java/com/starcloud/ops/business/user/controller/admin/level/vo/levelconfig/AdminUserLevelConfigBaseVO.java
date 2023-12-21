package com.starcloud.ops.business.user.controller.admin.level.vo.levelconfig;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 会员等级 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class AdminUserLevelConfigBaseVO {

    @Schema(description = "等级", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "等级不能为空")
    @Positive(message = "等级必须大于 0")
    private Integer level;

    @Schema(description = "等级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotBlank(message = "等级名称不能为空")
    private String name;

    @Schema(description = "所关联的角色", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "所关联的角色不能为空")
    private Long roleId;

    /**
     * 等级配置
     */
    @Schema(description = "等级配置", example = "xxx")
    private String levelConfig;

    @Schema(description = "等级图标", example = "https://www.iocoder.cn/yudao.jpg")
    @URL(message = "等级图标必须是 URL 格式")
    private String icon;

    @Schema(description = "等级背景图", example = "https://www.iocoder.cn/yudao.jpg")
    @URL(message = "等级背景图必须是 URL 格式")
    private String backgroundUrl;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

}
