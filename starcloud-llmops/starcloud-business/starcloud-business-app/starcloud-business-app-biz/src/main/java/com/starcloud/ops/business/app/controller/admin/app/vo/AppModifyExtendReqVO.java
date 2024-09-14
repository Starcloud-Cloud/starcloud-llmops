package com.starcloud.ops.business.app.controller.admin.app.vo;

import com.starcloud.ops.business.app.api.app.vo.request.AppUpdateReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanModifyReqVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppModifyExtendReqVO extends AppUpdateReqVO {

    private static final long serialVersionUID = 6092067732803063559L;

    /**
     * 计划请求, 媒体矩阵应用专属
     */
    private CreativePlanModifyReqVO planRequest;

}
