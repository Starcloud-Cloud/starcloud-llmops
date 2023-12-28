package com.starcloud.ops.business.mission.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.security.core.annotations.PreAuthenticated;
import com.starcloud.ops.business.mission.api.vo.request.*;
import com.starcloud.ops.business.mission.api.WechatAppApi;
import com.starcloud.ops.business.mission.api.vo.response.AppNotificationRespVO;
import com.starcloud.ops.business.mission.api.vo.response.AppSingleMissionRespVO;
import com.starcloud.ops.business.mission.api.vo.response.PreSettlementRecordRespVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.NotificationRespVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import com.starcloud.ops.business.mission.service.NotificationCenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/llm/wechat/app")
@Tag(name = "星河云海-微信小程序", description = "微信小程序")
public class WechatAppController {

    @Resource
    private WechatAppApi wechatAppApi;

    @Resource
    private NotificationCenterService centerService;

    @GetMapping("/metadata")
    @Operation(summary = "获取通告中心元数据", description = "获取通告中心元数据")
    @PreAuthenticated
    public CommonResult<Map<String, Object>> metadata() {
        return CommonResult.success(centerService.metadata());
    }

    @PostMapping("/notify/page")
    @Operation(summary = "小程序通告列表")
    @PreAuthenticated
    @OperateLog(enable = false)
    public CommonResult<PageResult<AppNotificationRespVO>> notifyPage(@Valid @RequestBody AppNotificationQueryReqVO reqVO) {
        PageResult<AppNotificationRespVO> result = wechatAppApi.notifyPage(reqVO);
        return CommonResult.success(result);
    }

    @GetMapping("/notify/detail/{notificationUid}")
    @Operation(summary = "小程序通告详情")
    @PermitAll
    @OperateLog(enable = false)
    public CommonResult<AppNotificationRespVO> notifyDetail(@PathVariable("notificationUid") String notificationUid) {
        AppNotificationRespVO result = wechatAppApi.notifyDetail(notificationUid);
        return CommonResult.success(result);
    }

    @PostMapping("/mission/claimed")
    @Operation(summary = "小程序已认领任务")
    @PreAuthenticated
    @OperateLog(enable = false)
    public CommonResult<PageResult<AppSingleMissionRespVO>> claimedMission(@Valid @RequestBody ClaimedMissionQueryReqVO reqVO) {
        PageResult<AppSingleMissionRespVO> result = wechatAppApi.claimedMission(reqVO);
        return CommonResult.success(result);
    }

    @GetMapping("/mission/detail/{missionUid}")
    @Operation(summary = "小程序任务详情")
    @PreAuthenticated
    @OperateLog(enable = false)
    public CommonResult<AppSingleMissionRespVO> detail(@PathVariable("missionUid") String missionUid) {
        AppSingleMissionRespVO result = wechatAppApi.missionDetail(missionUid);
        return CommonResult.success(result);
    }

    @PutMapping("/mission/claim")
    @Operation(summary = "小程序认领")
    @PreAuthenticated
    @OperateLog(enable = false)
    public CommonResult<AppSingleMissionRespVO> claimMission(@Valid @RequestBody AppClaimReqVO reqVO) {
        return CommonResult.success(wechatAppApi.claimMission(reqVO));
    }

    @PutMapping("/mission/publish")
    @Operation(summary = "小程序提交发布链接")
    @PreAuthenticated
    @OperateLog(enable = false)
    public CommonResult<Boolean> publishMission(@Valid @RequestBody AppMissionPublishReqVO reqVO) {
        wechatAppApi.publishMission(reqVO);
        return CommonResult.success(true);
    }

    @PutMapping("/mission/abandon")
    @Operation(summary = "小程序取消认领")
    @PreAuthenticated
    @OperateLog(enable = false)
    public CommonResult<Boolean> abandonMission(@Valid @RequestBody AppAbandonMissionReqVO reqVO) {
        wechatAppApi.abandonMission(reqVO);
        return CommonResult.success(true);
    }

    @GetMapping("/preSettlement/record")
    @Operation(summary = "预结算记录")
    @PreAuthenticated
    @OperateLog(enable = false)
    public CommonResult<PageResult<PreSettlementRecordRespVO>> preSettlementRecord(@Valid PreSettlementRecordReqVO reqVO) {
        PageResult<PreSettlementRecordRespVO> result = wechatAppApi.preSettlementRecord(reqVO);
        return CommonResult.success(result);
    }

}
