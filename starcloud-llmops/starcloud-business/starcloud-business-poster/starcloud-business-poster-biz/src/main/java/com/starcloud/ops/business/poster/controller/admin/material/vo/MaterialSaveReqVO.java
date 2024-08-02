package com.starcloud.ops.business.poster.controller.admin.material.vo;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import com.starcloud.ops.business.poster.enums.material.MaterialTypeEnum;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 海报素材新增/修改 Request VO")
@Data
public class MaterialSaveReqVO {

    @Schema(description = "主键id", requiredMode = Schema.RequiredMode.REQUIRED, example = "18992")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "标题不能为空")
    private String title;

    @Schema(description = "缩略图", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "缩略图不能为空")
    @URL(message = "缩略图必须是 URL 格式")
    private String thumbnail;

    @Schema(description = "描述")
    private String introduction;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "类型不能为空")
    @InEnum(value = MaterialTypeEnum.class,message = "素材类型不存在")
    private Integer type;

    @Schema(description = "标签")
    private String materialTags;

    @Schema(description = "素材数据", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "素材数据不能为空")
    private String materialData;

    @Schema(description = "请求数据")
    private String requestParams;

    @Schema(description = "素材分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "881")
    @NotNull(message = "素材分类编号不能为空")
    private Long categoryId;

    @Schema(description = "开启状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "开启状态不能为空")
    @InEnum(value = CommonStatusEnum.class,message = "请求状态必须在指定范围 {value}")
    private Integer status;

    @Schema(description = "分类排序")
    private Integer sort;

}