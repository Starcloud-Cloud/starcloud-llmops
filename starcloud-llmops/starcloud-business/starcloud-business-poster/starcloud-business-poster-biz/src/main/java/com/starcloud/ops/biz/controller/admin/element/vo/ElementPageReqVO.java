package com.starcloud.ops.biz.controller.admin.element.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 海报元素分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ElementPageReqVO extends PageParam {

    @Schema(description = "uid", example = "2992")
    private String uid;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "标签")
    private String label;

    @Schema(description = "详情")
    private String json;

    @Schema(description = "次序")
    private Integer order;

    @Schema(description = "类型uid", example = "8858")
    private String elementTypeUid;

    @Schema(description = "url", example = "https://www.iocoder.cn")
    private String elementUrl;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}