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


    public SubTaskResult() {
        super();
    }

}
