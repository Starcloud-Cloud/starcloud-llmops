package com.starcloud.ops.business.open.controller.admin;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.starcloud.ops.business.chat.context.RobotContextHolder;
import com.starcloud.ops.business.open.controller.admin.vo.request.GroupCallbackReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.request.QaCallbackReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.response.QaCallbackRespVO;
import com.starcloud.ops.business.open.service.WecomChatService;
import com.starcloud.ops.business.open.service.WecomGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;

@RequestMapping("/llm/worktool/callback")
@RestController
@Tag(name = "星河云海 - worktool 企业微信回调")
@Slf4j
public class WorkToolOpenController {

    @Resource
    private WecomChatService wecomChatService;

    @Resource
    private WecomGroupService wecomGroupService;

    @PostMapping("/qa/{robotId}")
    @Operation(summary = "qa回调")
    @PermitAll
    @OperateLog(enable = false)
    public QaCallbackRespVO reseiveMsg(@PathVariable("robotId") String robotId, @RequestBody QaCallbackReqVO qaMsgRequest) {
        log.info("机器人 {} 收到群消息，{}", robotId, qaMsgRequest);
        RobotContextHolder.setRobotId(robotId);
        if ("WorkTool".equalsIgnoreCase(qaMsgRequest.getGroupName())
                && "小明参与的WorkTool".equalsIgnoreCase(qaMsgRequest.getGroupRemark())) {
            return QaCallbackRespVO.success(StringUtils.EMPTY);
        }

        String spoken = qaMsgRequest.getSpoken();
        if (StringUtils.isNotBlank(spoken) && spoken.startsWith("BD")) {
            wecomGroupService.bindPublishChannel(qaMsgRequest);
            return QaCallbackRespVO.success(StringUtils.EMPTY);
        }

        if (StringUtils.isBlank(qaMsgRequest.getGroupRemark())) {
            log.error("groupRemark 不能为空");
            return QaCallbackRespVO.success(StringUtils.EMPTY);
        }
        wecomChatService.asynReplyMsg(qaMsgRequest);
        return QaCallbackRespVO.success(StringUtils.EMPTY);
    }

    @PostMapping("/instruction/{robotId}")
    @Operation(summary = "指令回调")
    @PermitAll
    @OperateLog(enable = false)
    public void instruction(@PathVariable("robotId") String robotId,
                            @RequestBody GroupCallbackReqVO request) {

        log.info("收到指令回调 {} | {}", robotId, request);

    }


    @PostMapping("/qr/{robotId}")
    @Operation(summary = "群二维码回调")
    @PermitAll
    @OperateLog(enable = false)
    public void groupCallback(@PathVariable("robotId") String robotId,
                              @RequestBody GroupCallbackReqVO request) {
        log.info("收到群二维码回调 {} | {}", robotId, request);

        // todo 修改 channel中的 qrcode
    }


}
