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

    @Schema(description = "分组编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    private Long groupId;

    @Schema(description = "缩略图", requiredMode = Schema.RequiredMode.REQUIRED)
    // @URL(message = "缩略图必须是 URL 格式")
    // @NotEmpty(message = "缩略图不能为空")
    private String thumbnail;

    @Schema(description = "素材数据", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "素材数据不能为空")
    private String materialData;

    @Schema(description = "请求数据")
    @NotEmpty(message = "请求数据不能为空")
    private String requestParams;

    @Schema(description = "开启状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private boolean status = Boolean.TRUE;

    @Schema(description = "分类排序")
    private Integer sort;

}