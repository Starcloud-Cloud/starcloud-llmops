package com.starcloud.ops.business.job.biz.controller.admin.vo;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginConfigVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.PluginDetailVO;
import com.starcloud.ops.business.job.biz.enums.BusinessJobTypeEnum;
import com.starcloud.ops.business.job.biz.enums.TriggerTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "定时任务配置")
public class BusinessJobBaseVO {

    @Schema(description = "名称")
    @NotBlank(message = "定时任务不能为空")
    @Length(min = 4, max = 50, message = "账号长度为 4-50 位")
    private String name;

    @Schema(description = "描述")
    private String descption;

    @Schema(description = "业务key 素材库定时任务就填libraryUid")
    @NotBlank(message = "业务key 必填")
    private String foreignKey;

    @Schema(description = "配置 执行参数和字段映射等")
    @NotNull(message = "配置不能为空")
    private JobConfigBaseVO config;

    @Schema(description = "定时类型")
    @NotNull(message = "定时类型不能为空")
    @InEnum(value = TriggerTypeEnum.class, field = InEnum.EnumField.CODE, message = "定时类型[{value}]必须是: {values}")
    private Integer timeExpressionType;

    @Schema(description = "定时表达式")
    @NotBlank(message = "定时表达式不能为空")
    private String timeExpression;

    @Schema(description = "是否启用 默认启用")
    private Boolean enable;

    /**
     * 任务的业务类型 {@link BusinessJobTypeEnum}
     */
    @Schema(description = "业务类型")
    @NotBlank(message = "业务类型不能为空")
    @InEnum(value = BusinessJobTypeEnum.class, field = InEnum.EnumField.CODE, message = "定时类型[{value}]必须是: {values}")
    private String businessJobType;

    @Schema(description = "生命周期开始时间")
    private Long lifecycleStart;

    @Schema(description = "生命周期结束时间")
    private Long lifecycleEnd;

}
