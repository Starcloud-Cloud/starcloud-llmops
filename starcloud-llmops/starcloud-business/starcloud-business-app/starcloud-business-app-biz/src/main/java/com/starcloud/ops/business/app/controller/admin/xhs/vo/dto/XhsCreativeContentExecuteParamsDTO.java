package com.starcloud.ops.business.app.controller.admin.xhs.vo.dto;

import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsBathImageExecuteRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "执行参数")
public class XhsCreativeContentExecuteParamsDTO {

    private XhsAppExecuteRequest appExecuteRequest;

    private XhsBathImageExecuteRequest bathImageExecuteRequest;

}
