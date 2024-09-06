package com.starcloud.ops.business.job.api;


import com.starcloud.ops.business.job.dto.JobDetailDTO;

import java.util.List;

public interface BusinessJobApi {

    /**
     * 删除定时任务
     */
    void deleteByForeignKey(String foreignKey);

    /**
     * 复制素材库定时任务
     */
    void copyJob(String sourceForeignKey, String targetForeignKey, String libraryUid);

    /**
     * 查询定时任务
     */
    List<JobDetailDTO> queryJob(List<String> foreignKeyList);
}
