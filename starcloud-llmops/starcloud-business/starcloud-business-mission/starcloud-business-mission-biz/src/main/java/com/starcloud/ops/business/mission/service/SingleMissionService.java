package com.starcloud.ops.business.mission.service;

import com.starcloud.ops.business.mission.controller.admin.vo.request.*;
import com.starcloud.ops.business.mission.controller.admin.vo.response.PageResult;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionExportVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDO;

import javax.validation.Valid;
import java.util.List;

public interface SingleMissionService {

    /**
     * 增加单条任务
     */
    void addSingleMission(String notificationUid, List<String> creativeUids);

    /**
     * 分页查询
     */
    PageResult<SingleMissionRespVO> page(SinglePageQueryReqVO reqVO);

    /**
     * 修改任务
     */
    void modifySelective(@Valid SingleMissionModifyReqVO reqVO);

    /**
     * 更新任务
     */
    SingleMissionRespVO update(SingleMissionModifyReqVO reqVO);

    /**
     * 删除
     */
    void delete(String uid);

    /**
     * 领取任务
     */
    void pick(String uid);

    /**
     * 发布任务
     */
    void publish(String notificationUid, Boolean publish);

    /**
     * 查询
     */
    SingleMissionRespVO getById(Long id);

    /**
     * 查询预结算 Id
     */
    List<Long> selectIds(@Valid SingleMissionQueryReqVO reqVO);

    /**
     * 校验预算金额
     */
    void validBudget(NotificationCenterDO notificationCenterDO);

    /**
     * 导出结算信息
     */
    List<SingleMissionExportVO> exportSettlement(SinglePageQueryReqVO reqVO);

    /**
     * 刷新小红书笔记
     */
    void refreshNote(String uid);

    /**
     * 结算更新
     */
    void settlement(SingleMissionRespVO singleMissionRespVO);
}
