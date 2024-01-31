package com.starcloud.ops.business.user.controller.admin.notify.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "筛选用户")
public class FilterUserReqVO extends PageParam {

    @Schema(description = "模板编码")
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;
}
