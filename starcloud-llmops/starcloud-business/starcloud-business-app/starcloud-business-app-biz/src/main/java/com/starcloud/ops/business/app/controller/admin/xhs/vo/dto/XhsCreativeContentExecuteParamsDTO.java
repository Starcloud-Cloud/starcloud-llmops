package com.starcloud.ops.business.app.controller.admin.xhs.vo.dto;

import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsBathImageExecuteRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author nacoyer
 */
@Data
@Schema(description = "执行参数")
public class XhsCreativeContentExecuteParamsDTO implements java.io.Serializable {

    private static final long serialVersionUID = -3410146595842083253L;

    /**
     * 应用请求
     */
    private XhsAppExecuteRequest appExecuteRequest;

    /**
     * 批量图片请求
     */
    private XhsBathImageExecuteRequest bathImageExecuteRequest;

    /**
     * 根据app执行请求生成XhsCreativeContentExecuteParamsDTO对象
     *
     * @param appExecuteRequest app执行请求对象
     * @return 生成的XhsCreativeContentExecuteParamsDTO对象
     */
    public static XhsCreativeContentExecuteParamsDTO ofApp(XhsAppExecuteRequest appExecuteRequest) {
        XhsCreativeContentExecuteParamsDTO executeParamsDTO = new XhsCreativeContentExecuteParamsDTO();
        executeParamsDTO.setAppExecuteRequest(appExecuteRequest);
        return executeParamsDTO;
    }

    /**
     * 从XhsBathImageExecuteRequest对象中生成XhsCreativeContentExecuteParamsDTO对象
     *
     * @param bathImageExecuteRequest XhsBathImageExecuteRequest对象，用于填充XhsCreativeContentExecuteParamsDTO对象
     * @return XhsCreativeContentExecuteParamsDTO对象
     */
    public static XhsCreativeContentExecuteParamsDTO ofBathImage(XhsBathImageExecuteRequest bathImageExecuteRequest) {
        XhsCreativeContentExecuteParamsDTO executeParamsDTO = new XhsCreativeContentExecuteParamsDTO();
        executeParamsDTO.setBathImageExecuteRequest(bathImageExecuteRequest);
        return executeParamsDTO;
    }
}
