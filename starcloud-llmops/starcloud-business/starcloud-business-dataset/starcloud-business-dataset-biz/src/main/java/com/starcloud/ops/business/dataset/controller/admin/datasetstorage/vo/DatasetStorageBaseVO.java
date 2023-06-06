package com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
* 数据集源数据存储 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class DatasetStorageBaseVO {

    @Schema(description = "编号", required = true, example = "25468")
    @NotNull(message = "编号不能为空")
    private String uid;

    @Schema(description = "名称", required = true, example = "芋艿")
    @NotNull(message = "名称不能为空")
    private String name;

    @Schema(description = "数据类型", required = true, example = "2")
    @NotNull(message = "数据类型不能为空")
    private String type;

    @Schema(description = "键", required = true)
    @NotNull(message = "键不能为空")
    private String storageKey;

    @Schema(description = "存储类型", required = true, example = "2")
    @NotNull(message = "存储类型不能为空")
    private String storageType;

    @Schema(description = "大小", required = true)
    @NotNull(message = "大小不能为空")
    private Long size;

    @Schema(description = "MIME类型", example = "2")
    private String mimeType;

    @Schema(description = "是否已使用", required = true)
    @NotNull(message = "是否已使用不能为空")
    private Boolean used;

    @Schema(description = "使用者ID")
    private String usedBy;

    @Schema(description = "使用时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime usedAt;

    @Schema(description = "哈希值")
    private String hash;

}