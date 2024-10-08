package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import cn.iocoder.yudao.framework.common.pojo.SortablePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 素材知识库分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaterialLibraryPageReqVO extends SortablePageParam {

    public static final String SORT_FIELD_FILE_COUNT = "file_count";
    public static final String SORT_FIELD_CREATE_TIME = "create_time";
    public static final String SORT_FIELD_UPDATE_TIME = "update_time";

    @Schema(description = "名称", example = "芋艿")
    private String name;

    @Schema(description = "图标链接", example = "https://www.iocoder.cn")
    private String iconUrl;

    @Schema(description = "描述", example = "你猜")
    private String description;

    @Schema(description = "素材类型", example = "2")
    private Integer formatType;

    @Schema(description = "素材库类型", example = "2")
    @NotNull(message = "素材库类型不能为空")
    private Integer libraryType;

    @Schema(description = "状态", example = "2")
    private Boolean status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "创建人", example = "李广")
    private String createName;

    @Schema(description = "创建编号", example = "123")
    private Long creator;

}