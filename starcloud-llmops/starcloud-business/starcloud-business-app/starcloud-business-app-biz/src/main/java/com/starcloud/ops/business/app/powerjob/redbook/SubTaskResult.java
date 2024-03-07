package com.starcloud.ops.business.app.powerjob.redbook;

import com.starcloud.ops.business.app.powerjob.base.BaseTaskResult;
import lombok.*;

import java.util.List;

@Data
@ToString
public class SubTaskResult extends BaseTaskResult {

    private String planUid;

    private Long batch;

    private List<Long> allTaskUids;

    private List<Long> errorTaskUids;

    public SubTaskResult(boolean success, String msg, String planUid, Long batch, List<Long> allTaskUids, List<Long> errorTaskUids) {
        super(success, msg);
        this.planUid = planUid;
        this.allTaskUids = allTaskUids;
        this.errorTaskUids = errorTaskUids;
        this.batch = batch;
    }

    public SubTaskResult(boolean success, String msg) {
        super(success, msg);
    }
}
