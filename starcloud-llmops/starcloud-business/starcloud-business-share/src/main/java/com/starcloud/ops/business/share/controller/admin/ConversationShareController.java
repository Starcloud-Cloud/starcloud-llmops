package com.starcloud.ops.business.share.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageRespVO;
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
public class ConversationShareController {

    @Resource
    private ConversationShareService shareService;


    @PostMapping("/create")
    @Operation(summary = "创建会话分享")
    @PermitAll
    public CommonResult<String> create(@RequestBody @Valid ConversationShareReq req) {
        return CommonResult.success(shareService.createShareLink(req));
    }


    @PutMapping("/modify")
    @Operation(summary = "修改分享记录")
    public CommonResult<Boolean> modify(@RequestBody @Valid ConversationShareReq req) {
        shareService.modifyRecord(req);
        return CommonResult.success(true);
    }

    @GetMapping("/app/{shareKey}")
    @Operation(summary = "应用详情")
    @PermitAll
    public CommonResult<AppDetailRespVO> appDetail(@PathVariable("shareKey") String shareKey) {
        return CommonResult.success(shareService.appDetail(shareKey));
    }

    @GetMapping("/history/{shareKey}")
    @Operation(summary = "会话详情")
    @PermitAll
    public CommonResult<List<LogAppMessageRespVO>> conversationDetail(@PathVariable("shareKey") String shareKey) {
        return CommonResult.success(shareService.conversationDetail(shareKey));
    }

    @GetMapping("/list/{conversationUid}")
    @Operation(summary = "分享记录")
    @PermitAll
    public CommonResult<List<ConversationShareResp>> shareRecord(@PathVariable("conversationUid") String conversationUid) {
        return CommonResult.success(shareService.shareRecord(conversationUid));
    }


    @GetMapping("/detail/{shareKey}")
    @Operation(summary = "分享详情")
    @PermitAll
    public CommonResult<ConversationShareResp> recordDetail(@PathVariable("shareKey") String shareKey) {
        return CommonResult.success(shareService.recordDetail(shareKey));
    }
}
