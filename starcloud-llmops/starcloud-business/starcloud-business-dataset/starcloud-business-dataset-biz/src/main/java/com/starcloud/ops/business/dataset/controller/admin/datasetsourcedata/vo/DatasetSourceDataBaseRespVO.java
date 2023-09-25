package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import com.starcloud.ops.business.dataset.enums.DataSetSourceDataStatusEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataModelEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.service.dto.DataSourceInfoDTO;
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
public class DatasetSourceDataBaseRespVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "编号")
    private String uid;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    /**
     * 数据模型
     * {@link DataSourceDataModelEnum}
     */
    @Schema(description = "数据模型")
    private String dataModel;

    /**
     * 数据类型
     * {@link DataSourceDataTypeEnum}
     */
    @Schema(description = "数据类型")
    private String dataType;

    @Schema(description = "批次")
    private String batch;

    @Schema(description = "预处理规则ID")
    private Long ruleId;

    @Schema(description = "存储ID")
    private Long storageId;

    @Schema(description = "清洗内容存储 ID")
    private Long cleanStorageId;

    @Schema(description = "总结内容")
    private String summary;

    @Schema(description = "总结状态")
    private Long summaryStatus;

    @Schema(description = "字符数")
    private Long wordCount;

    @Schema(description = "数据扩展信息")
    private DataSourceInfoDTO dataSourceInfo;

    /**
     * 状态
     * {@link DataSetSourceDataStatusEnum}
     */
    @Schema(description = " 状态")
    private Long status;

    @Schema(description = "令牌数")
    private Long tokens;

    @Schema(description = "执行信息")
    private String errorMessage;

    @Schema(description = " 启用状态")
    private Boolean enabled;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime updateTime;

}