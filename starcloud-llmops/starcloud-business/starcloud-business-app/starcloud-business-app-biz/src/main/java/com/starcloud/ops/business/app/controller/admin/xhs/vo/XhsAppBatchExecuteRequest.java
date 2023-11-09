package com.starcloud.ops.business.app.controller.admin.xhs.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class XhsAppBatchExecuteRequest extends XhsAppExecuteRequest{

    /**
     * 任务uid
     */
    private String creativeContentUid;
}
