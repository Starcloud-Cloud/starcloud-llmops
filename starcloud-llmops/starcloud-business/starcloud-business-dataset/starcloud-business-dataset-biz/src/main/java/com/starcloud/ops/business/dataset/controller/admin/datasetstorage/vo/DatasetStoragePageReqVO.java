package com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 数据集源数据存储分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetStoragePageReqVO extends PageParam {

    @Schema(description = "编号", example = "25468")
    private String uid;

    @Schema(description = "名称", example = "芋艿")
    private String name;

    @Schema(description = "数据类型", example = "2")
    private String type;

    @Schema(description = "键")
    private String storageKey;

    @Schema(description = "存储类型", example = "2")
    private String storageType;

    @Schema(description = "大小")
    private Integer size;

    @Schema(description = "MIME类型", example = "2")
    private String mimeType;

    @Schema(description = "是否已使用")
    private Boolean used;

    @Schema(description = "使用者ID")
    private String usedBy;

    @Schema(description = "使用时间")
    private LocalDateTime usedAt;

    @Schema(description = "哈希值")
    private String hash;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}