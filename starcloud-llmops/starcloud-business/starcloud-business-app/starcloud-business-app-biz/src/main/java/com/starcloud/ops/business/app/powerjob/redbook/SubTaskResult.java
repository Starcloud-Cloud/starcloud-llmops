package com.starcloud.ops.business.app.powerjob.redbook;

import com.starcloud.ops.business.app.powerjob.base.BaseTaskResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 任务执行结果
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-22
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubTaskResult extends BaseTaskResult {

    /**
     * 计划UID
     */
    private String planUid;

    /**
     * 所有任务UID
     */
    private List<Long> allTaskUids;

    /**
     * 错误任务UID
     */
    private List<Long> errorTaskUids;

    public SubTaskResult(boolean success, String msg, String planUid, List<Long> allTaskUids, List<Long> errorTaskUids) {
        super(success, msg);
        this.planUid = planUid;
        this.allTaskUids = allTaskUids;
        this.errorTaskUids = errorTaskUids;
    }

    public SubTaskResult(boolean success, String msg) {
        super(success, msg);
    }
}
