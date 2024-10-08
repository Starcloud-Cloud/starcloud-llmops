package com.starcloud.ops.business.mission.service;

import com.starcloud.ops.business.mission.controller.admin.vo.request.*;
import com.starcloud.ops.business.mission.controller.admin.vo.response.PageResult;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionExportVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import com.starcloud.ops.business.mission.dal.dataobject.MissionNotificationDTO;
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
     * 任务详情
     */
    MissionNotificationDTO missionDetail(String uid);


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
     * 批量删除
     */
    void batchDelete(List<String> uids);

    /**
     * 领取任务
     */
    void pick(String uid);

    /**
     * 发布任务
     */
    void publish(NotificationCenterDO notificationCenterDO, Boolean publish);

    /**
     * 查询
     */
    SingleMissionRespVO getById(Long id);

    /**
     * 查询预结算 Id
     */
    List<Long> selectIds(@Valid SingleMissionQueryReqVO reqVO);

    /**
     * 查询结算 Id
     */
    List<Long> selectSettlementIds(@Valid SingleMissionQueryReqVO reqVO);

    /**
     * 重试 id
     * @param reqVO
     * @return
     */
    List<Long> retryIds(@Valid SingleMissionQueryReqVO reqVO);

    /**
     * 执行 id
     * @param reqVO
     * @return
     */
    List<Long> executeIds(@Valid SingleMissionQueryReqVO reqVO);

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
    SingleMissionRespVO refreshNote(RefreshNoteDetailReqVO reqVO);

    /**
     * 预结算更新
     */
    void preSettlement(SingleMissionRespVO singleMissionRespVO);

    /**
     * 结算
     */
    void settlement(SingleMissionRespVO singleMissionRespVO);

    /**
     * 删除通告
     */
    void deleteNotification(String notificationUid);

    /**
     * 导入
     */
    void importSettlement(List<SingleMissionImportVO> importVOList);

    /**
     * 重试
     * @param singleMissionId
     */
    void retry(Long singleMissionId);
}
