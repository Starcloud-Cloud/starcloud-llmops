package com.starcloud.ops.business.mission.api;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.mission.api.vo.request.*;
import com.starcloud.ops.business.mission.api.vo.response.AppNotificationRespVO;
import com.starcloud.ops.business.mission.api.vo.response.AppSingleMissionRespVO;
import com.starcloud.ops.business.mission.api.vo.response.PreSettlementRecordRespVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.NotificationRespVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;

public interface WechatAppApi {
    /**
     * 小程序分页查询通告
     */
    PageResult<AppNotificationRespVO> notifyPage(AppNotificationQueryReqVO reqVO);

    /**
     * 小程序认领任务
     */
    AppSingleMissionRespVO claimMission(AppClaimReqVO reqVO);

    /**
     * 小程序提交发布链接
     */
    void publishMission(AppMissionPublishReqVO reqVO);

    /**
     * 放弃任务
     */
    void abandonMission(AppAbandonMissionReqVO reqVO);

    /**
     * 已认领任务
     */
    PageResult<AppSingleMissionRespVO> claimedMission(ClaimedMissionQueryReqVO reqVO);

    /**
     * 任务详情
     */
    AppSingleMissionRespVO missionDetail(String missionUid);

    /**
     * 通告详情
     */
    AppNotificationRespVO notifyDetail(String notificationUid,String userId);

    /**
     * 预结算记录
     */
    PageResult<PreSettlementRecordRespVO> preSettlementRecord(PreSettlementRecordReqVO reqVO);
}
