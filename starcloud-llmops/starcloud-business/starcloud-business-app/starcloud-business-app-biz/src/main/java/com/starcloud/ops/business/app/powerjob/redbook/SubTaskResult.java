package com.starcloud.ops.business.app.powerjob.redbook;

import com.starcloud.ops.business.app.powerjob.base.BaseTaskResult;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SubTaskResult extends BaseTaskResult {

    /**
     * 计划UID
     */
    private String planUid;

    /**
     * 批次UID
     */
    private String batchUid;

    /**
     * 任务UID列表
     */
    private List<String> taskUidList;

    /**
     * 失败UID列表
     */
    private List<String> errorTaskUidList;


    public SubTaskResult(boolean success, String msg, String planUid, String batchUid, List<String> taskUidList, List<String> errorTaskUidList) {
        super(success, msg);
        this.planUid = planUid;
        this.taskUidList = taskUidList;
        this.errorTaskUidList = errorTaskUidList;
        this.batchUid = batchUid;
    }

    public SubTaskResult(boolean success, String msg) {
        super(success, msg);
    }

    public static SubTaskResult success(String msg, String planUid, String batchUid, List<String> taskUidList, List<String> errorTaskUidList) {
        return new SubTaskResult(true, msg, planUid, batchUid, taskUidList, errorTaskUidList);
    }

    public static SubTaskResult failure(String msg) {
        return new SubTaskResult(false, msg);
    }

}
