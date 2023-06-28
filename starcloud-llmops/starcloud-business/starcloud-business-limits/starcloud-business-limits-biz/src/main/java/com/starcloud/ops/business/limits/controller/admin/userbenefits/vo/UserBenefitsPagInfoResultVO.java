package com.starcloud.ops.business.limits.controller.admin.userbenefits.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户权益余量信息 VO")
@Data
@ToString(callSuper = true)
public class UserBenefitsPagInfoResultVO {

    @Schema(description = "权益名称")
    private String benefitsName;

    @Schema(description = "权益列表")
    private List<UserBenefitsListResultVO> benefitsList;
    /**
     * 生效时间
     */
    @Schema(description = "生效时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime effectiveTime;
    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime expirationTime;
    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "有效期数")
    private Long validity;

    @Schema(description = "有效单位")
    private String validityUnit;

}
