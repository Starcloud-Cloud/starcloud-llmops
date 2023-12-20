package com.starcloud.ops.business.mission.api.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.starcloud.ops.business.enums.NotificationPlatformEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "小程序查询通告列表")
public class AppNotificationQueryReqVO extends PageParam {

    @Schema(description = "用户id")
    @NotBlank(message = "用户id不能为空")
    private String claimUserId;

    @Schema(description = "通告名称 左匹配")
    private String notificationName;

    @Schema(description = "是否公开")
    private Boolean open;

    @Schema(description = "任务创建人")
    private List<String> creator;

    @Schema(description = "平台")
    @InEnum(value = NotificationPlatformEnum.class, field = InEnum.EnumField.CODE, message = "平台类型[{value}]必须是: {values}")
    private String platform;

    @Schema(description = "类目")
    private String field;

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "正序")
    private Boolean asc;

    @Schema(description = "最小粉丝数")
    private Integer minFansNum;

    @Schema(description = "单个任务预算")
    private BigDecimal singleBudget;

}
