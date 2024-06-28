package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 素材知识库分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaterialLibraryPageReqVO extends PageParam {

    @Schema(description = "名称", example = "芋艿")
    private String name;

    @Schema(description = "图标链接", example = "https://www.iocoder.cn")
    private String iconUrl;

    @Schema(description = "描述", example = "你猜")
    private String description;

    @Schema(description = "素材类型", example = "2")
    private Integer formatType;

    @Schema(description = "素材库大小")
    private Long allFileSize;

    @Schema(description = "分享范围")
    private String shareRange;

    @Schema(description = "总使用次数", example = "6606")
    private Long totalUsedCount;

    @Schema(description = "插件配置")
    private String pluginConfig;

    @Schema(description = "状态", example = "2")
    private Boolean status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}