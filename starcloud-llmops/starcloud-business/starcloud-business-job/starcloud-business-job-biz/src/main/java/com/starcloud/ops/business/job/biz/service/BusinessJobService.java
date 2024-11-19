package com.starcloud.ops.business.job.biz.service;

import com.starcloud.ops.business.job.biz.controller.admin.vo.BusinessJobBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.BusinessJobModifyReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.BusinessJobRespVO;
import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobDO;

import java.util.List;
import java.util.Map;

public interface BusinessJobService {

    /**
     * 新建定时任务
     */
    BusinessJobRespVO createJob(BusinessJobBaseVO businessJobBaseVO);

    /**
     * 修改定时任务
     */
    void modify(BusinessJobModifyReqVO reqVO);

    /**
     * 删除任务并停止调度实例
     */
    void delete(String uid);

    /**
     * 停止任务
     */
    void stop(String uid);

    /**
     * 启动任务
     */
    void start(String uid);

    /**
     * 查询定时任务配置
     *
     * @param jobId powerjob任务id
     */
    BusinessJobDO getByJobId(Long jobId);


    /**
     * 可执行次数减一
     */
    void decreaseNum(String uid);

    /**
     * 执行任务
     */
    void runJob(String uid);

    /**
     * 查询定时任务配置
     */
    BusinessJobRespVO getByForeignKey(String foreignKey);

    List<BusinessJobRespVO> getByForeignKey(List<String> foreignKeys);

    /**
     * 枚举值
     */
    Map<String, Object> metadata();

}
