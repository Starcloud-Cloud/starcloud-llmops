package com.starcloud.ops.business.app.api.xhs.scheme.vo.request;

import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.AppUpdateReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GenerateOptionReqVO {

    @NotBlank(message = "应用节点ID不能为空")
    @Schema(description = "应用节点ID")
    private String stepCode;


    /**
     * 逻辑上应该前端的所以改动都要请求下接口重新进行计算
     */
    private AppUpdateReqVO appReqVO;

    @Schema(description = "APP/MARKET")
    private String source;

    @Schema(description = "计划uid source为MARKET时必填")
    private String planUid;
}
