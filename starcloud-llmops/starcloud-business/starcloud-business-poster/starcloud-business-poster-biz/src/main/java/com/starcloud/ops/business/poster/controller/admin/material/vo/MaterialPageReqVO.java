package com.starcloud.ops.business.poster.controller.admin.material.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 海报素材分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaterialPageReqVO extends PageParam {

    @Schema(description = "编号", example = "10885")
    private String uid;

    @Schema(description = "名称", example = "王五")
    private String name;

    @Schema(description = "分组编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private Long groupId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "缩略图")
    private String thumbnail;

    @Schema(description = "描述")
    private String introduction;

    @Schema(description = "类型", example = "2")
    private String type;

    @Schema(description = "标签")
    private String materialTags;

    @Schema(description = "素材数据")
    private String materialData;

    @Schema(description = "请求数据")
    private String requestParams;

    @Schema(description = "素材分类编号", example = "881")
    private Long categoryId;

    @Schema(description = "开启状态", example = "2")
    private Integer status;

    @Schema(description = "分类排序")
    private Integer sort;

    @Schema(description = "用户类型", example = "1")
    private String userType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}