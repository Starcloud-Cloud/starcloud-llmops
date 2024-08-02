package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice;

import cn.iocoder.yudao.framework.common.pojo.SortablePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 素材知识库数据分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaterialLibrarySlicePageReqVO extends SortablePageParam {

    public static final String SORT_FIELD_ID = "id";
    public static final String SORT_FIELD_USED_COUNT = "used_count";
    public static final String SORT_FIELD_CREATE_TIME = "create_time";
    public static final String SORT_FIELD_UPDATE_TIME = "update_time";


    @Schema(description = "素材库ID", example = "30132")
    private Long libraryId;

    @Schema(description = "素材库ID", example = "30132")
    private String libraryUid;

    @Schema(description = "字符数", example = "21593")
    private Long charCount;

    @Schema(description = "总使用次数", example = "17558")
    private Long usedCount;

    @Schema(description = "描述")
    private String content;

    @Schema(description = "序列")
    private Long sequence;

    @Schema(description = "状态", example = "2")
    private Boolean status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}