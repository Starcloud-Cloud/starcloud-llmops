package com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.TIME_ZONE_DEFAULT;

/**
* 兑换码模板 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class PromoCodeTemplateBaseVO {


    @Schema(description = "兑换码名", requiredMode = Schema.RequiredMode.REQUIRED, example = "春节送送送")
    @NotNull(message = "兑换码名不能为空")
    private String name;

    @Schema(description = "兑换码编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "春节送送送")
    @NotNull(message = "兑换码编号不能为空")
    private String code;

    @Schema(description = "发行总量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024") // -1 - 则表示不限制发放数量
    @NotNull(message = "发行总量不能为空")
    private Integer totalCount;

    @Schema(description = "每人限领个数", requiredMode = Schema.RequiredMode.REQUIRED, example = "66") // -1 - 则表示不限制
    @NotNull(message = "每人限领个数不能为空")
    private Integer takeLimitCount;

    @Schema(description = "兑换码类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "兑换码类型不能为空")
    private Integer codeType;

    @Schema(description = "固定日期 - 生效开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, timezone = TIME_ZONE_DEFAULT)
    private LocalDateTime validStartTime;

    @Schema(description = "固定日期 - 生效结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, timezone = TIME_ZONE_DEFAULT)
    private LocalDateTime validEndTime;


    @Schema(description = "优惠券编号", example = "1")
    private Long couponTemplateId;

    @Schema(description = "权益参数")
    private AdminUserRightsAndLevelCommonDTO giveRights;




}
