package com.starcloud.ops.business.limits.controller.admin.userbenefits.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import com.starcloud.ops.business.limits.enums.UserLevelEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户权益信息 VO")
@Data
@ToString(callSuper = true)
public class UserBenefitsInfoResultVO {


    @Schema(description = "用户等级")
    private String userLevel;

    @Schema(description = "用户等级")
    private String userLevelName;

    @Schema(description = "权益数据")
    private List<UserBenefitsBaseResultVO> benefits;

    @Schema(description = "查询时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime queryTime;

}
