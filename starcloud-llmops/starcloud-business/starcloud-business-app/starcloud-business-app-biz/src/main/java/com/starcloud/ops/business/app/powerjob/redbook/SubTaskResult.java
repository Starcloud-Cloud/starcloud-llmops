package com.starcloud.ops.business.app.powerjob.redbook;

import com.starcloud.ops.business.app.powerjob.base.BaseTaskResult;
import lombok.*;

@Data
@ToString
public class SubTaskResult extends BaseTaskResult {

    private String planUid;

    public SubTaskResult(boolean success, String msg, String planUid) {
        super(success, msg);
        this.planUid = planUid;
    }

    public SubTaskResult(boolean success, String msg) {
        super(success, msg);
    }
}
