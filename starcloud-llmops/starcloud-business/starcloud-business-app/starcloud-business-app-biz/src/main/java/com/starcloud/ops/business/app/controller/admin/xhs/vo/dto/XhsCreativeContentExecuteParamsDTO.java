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

}
