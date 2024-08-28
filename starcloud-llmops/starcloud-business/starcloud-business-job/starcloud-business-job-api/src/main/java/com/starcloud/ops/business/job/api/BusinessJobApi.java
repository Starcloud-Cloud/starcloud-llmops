package com.starcloud.ops.business.job.api;


public interface BusinessJobApi {

    /**
     * 删除定时任务
     */
    void deleteByForeignKey(String foreignKey);

    /**
     * 复制素材库定时任务
     */
    void copyJob(String sourceForeignKey, String targetForeignKey, String libraryUid);
}
