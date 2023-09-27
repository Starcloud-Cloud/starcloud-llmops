package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 数据集源数据 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class DatasetSourceDataBasicInfoVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @Schema(description = "编号",  requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "编号不能为空")
    private String uid;

    @Schema(description = "名称",  requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "名称不能为空")
    private String name;

    @Schema(description = " 描述",  requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "描述不能为空")
    private String description;

    @Schema(description = "总结内容")
    private String summary;

    @Schema(description = " 数据类型",  requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据类型不能为空")
    private String dataType;

    @Schema(description = " 数据地址",  requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    @Schema(description = " 清洗地址ID",  requiredMode = Schema.RequiredMode.REQUIRED)
    private Long cleanId;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "启用状态")
    private Boolean enabled;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime updateTime;


}