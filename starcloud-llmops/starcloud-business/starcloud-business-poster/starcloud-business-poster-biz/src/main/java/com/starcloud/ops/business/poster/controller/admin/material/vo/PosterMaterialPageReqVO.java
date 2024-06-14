package com.starcloud.ops.business.poster.controller.admin.material.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 2024/06/14   AlanCusack    1.0         1.0 Version
 */
@Data
public class PosterMaterialPageReqVO extends PageParam {

    @Schema(description = "分类编号", example = "1")
    private Long categoryId;
}
