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

    public static XhsCreativeContentExecuteParamsDTO ofApp(XhsAppExecuteRequest appExecuteRequest) {
        XhsCreativeContentExecuteParamsDTO executeParamsDTO = new XhsCreativeContentExecuteParamsDTO();
        executeParamsDTO.setAppExecuteRequest(appExecuteRequest);
        return executeParamsDTO;
    }

    public static XhsCreativeContentExecuteParamsDTO ofBathImage(XhsBathImageExecuteRequest bathImageExecuteRequest) {
        XhsCreativeContentExecuteParamsDTO executeParamsDTO = new XhsCreativeContentExecuteParamsDTO();
        executeParamsDTO.setBathImageExecuteRequest(bathImageExecuteRequest);
        return executeParamsDTO;
    }
}
