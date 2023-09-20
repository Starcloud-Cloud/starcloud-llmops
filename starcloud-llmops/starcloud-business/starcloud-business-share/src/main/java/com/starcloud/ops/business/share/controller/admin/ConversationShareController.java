package com.starcloud.ops.business.share.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageRespVO;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.starcloud.ops.business.share.controller.admin.vo.AppDetailRespVO;
import com.starcloud.ops.business.share.controller.admin.vo.ConversationShareReq;
import com.starcloud.ops.business.share.controller.admin.vo.ConversationShareResp;
import com.starcloud.ops.business.share.service.ConversationShareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/share/conversation")
@Tag(name = "魔法AI-会话分享")
@DataPermission(enable = false)
public class ConversationShareController {

    @Resource
    private ConversationShareService shareService;


    @PostMapping("/create")
    @Operation(summary = "创建会话分享")
    @PermitAll
    public CommonResult<ConversationShareResp> create(@RequestBody @Valid ConversationShareReq req) {
        return CommonResult.success(shareService.createShareLink(req));
    }

    @GetMapping("/app/{shareKey}")
    @Operation(summary = "应用详情")
    @PermitAll
    @TenantIgnore
    public CommonResult<AppDetailRespVO> appDetail(@PathVariable("shareKey") String shareKey) {
        return CommonResult.success(shareService.appDetail(shareKey));
    }

    @GetMapping("/history/{shareKey}")
    @Operation(summary = "会话详情")
    @PermitAll
    @TenantIgnore
    public CommonResult<List<LogAppMessageRespVO>> conversationDetail(@PathVariable("shareKey") String shareKey) {
        return CommonResult.success(shareService.conversationDetail(shareKey));
    }

    @GetMapping("/list/{conversationUid}")
    @Operation(summary = "分享记录")
    @PermitAll
    @TenantIgnore
    public CommonResult<List<ConversationShareResp>> shareRecord(@PathVariable("conversationUid") String conversationUid) {
        return CommonResult.success(shareService.shareRecord(conversationUid));
    }


    @GetMapping("/detail/{shareKey}")
    @Operation(summary = "分享详情")
    @PermitAll
    @TenantIgnore
    public CommonResult<ConversationShareResp> recordDetail(@PathVariable("shareKey") String shareKey) {
        return CommonResult.success(shareService.recordDetail(shareKey));
    }
}
