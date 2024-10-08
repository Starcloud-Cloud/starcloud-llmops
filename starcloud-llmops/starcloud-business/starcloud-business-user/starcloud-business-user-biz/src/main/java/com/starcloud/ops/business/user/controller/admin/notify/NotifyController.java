package com.starcloud.ops.business.user.controller.admin.notify;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.notify.vo.template.NotifyTemplateCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.notify.vo.template.NotifyTemplateUpdateReqVO;
import com.starcloud.ops.business.user.controller.admin.notify.vo.CreateNotifyReqVO;
import com.starcloud.ops.business.user.controller.admin.notify.vo.FilterUserReqVO;
import com.starcloud.ops.business.user.controller.admin.notify.vo.NotifyContentRespVO;
import com.starcloud.ops.business.user.service.notify.NotifyService;
import com.starcloud.ops.framework.common.api.dto.Option;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户通知")
@RestController
@RequestMapping("/llm/notify")
public class NotifyController {

    @Resource
    private NotifyService notifyService;

    @GetMapping("/metadata")
    @Operation(summary = "查询元数据", description = "查询元数据")
    public CommonResult<Map<String,List<Option>>> metadata() {
        return CommonResult.success(notifyService.metaData());
    }

    @GetMapping("/filterUser")
    @Operation(summary = "查询收件人", description = "查询收件人")
    public CommonResult<PageResult<NotifyContentRespVO>> filterUser(@Valid FilterUserReqVO reqVO) {
        PageResult<NotifyContentRespVO> result = notifyService.pageFilterUser(reqVO);
        return CommonResult.success(result);
    }

    @PostMapping("/trigger")
    @Operation(summary = "触发用户通知")
    @PreAuthorize("@ss.hasPermission('system:notify-template:send-notify')")
    public CommonResult<Boolean> triggerMsgTask(@Valid @RequestBody CreateNotifyReqVO reqVO) {
        notifyService.triggerNotify(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/create/temp")
    @Operation(summary = "创建站内信模版-new")
    @PreAuthorize("@ss.hasPermission('system:notify-template:create')")
    public CommonResult<Long> createNotifyTemplate(@Valid @RequestBody NotifyTemplateCreateReqVO createReqVO) {
        return success(notifyService.createNotifyTemplate(createReqVO));
    }

    @PutMapping("/update/temp")
    @Operation(summary = "更新站内信模版")
    @PreAuthorize("@ss.hasPermission('system:notify-template:update')")
    public CommonResult<Boolean> updateNotifyTemplate(@Valid @RequestBody NotifyTemplateUpdateReqVO updateReqVO) {
        notifyService.updateNotifyTemplate(updateReqVO);
        return success(true);
    }

}
