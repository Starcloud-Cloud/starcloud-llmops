package com.starcloud.ops.business.app.controller.admin.xhs.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author nacoyer
 */
@Data
@ToString
public class XhsAppCreativeExecuteRequest extends XhsAppExecuteRequest {

    private static final long serialVersionUID = -2675544310134972689L;

    /**
     * 任务uid
     */
    private String creativeContentUid;

    /**
     * 用户id
     */
    private String userId;
}
