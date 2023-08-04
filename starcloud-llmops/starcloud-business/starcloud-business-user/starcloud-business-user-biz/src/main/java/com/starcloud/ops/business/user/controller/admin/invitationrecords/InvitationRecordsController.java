package com.starcloud.ops.business.user.controller.admin.invitationrecords;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.user.service.InvitationRecordsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 邀请记录")
@RestController
@RequestMapping("/llm/invitation-records")
@Validated
public class InvitationRecordsController {

    @Resource
    private InvitationRecordsService invitationRecordsService;

    @PostMapping("/create/{inviterId}/{inviteeId}")
    @Operation(summary = "创建邀请记录")
    // @PreAuthorize("@ss.hasPermission('llm:invitation-records:create')")
    public CommonResult<Boolean> createInvitationRecords(            @PathVariable("inviterId") Long inviterId,
                                                                  @PathVariable("inviteeId") Long inviteeId) {
        invitationRecordsService.createInvitationRecords(inviterId,inviteeId);
        return success(true);
    }
    //
    // @GetMapping("/get")
    // @Operation(summary = "获得邀请记录")
    // @Parameter(name = "id", description = "编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('llm:invitation-records:query')")
    // public CommonResult<InvitationRecordsRespVO> getInvitationRecords(@RequestParam("id") Long id) {
    //     InvitationRecordsDO invitationRecords = invitationRecordsService.getInvitationRecords(id);
    //     return success(InvitationRecordsConvert.INSTANCE.convert(invitationRecords));
    // }
    //
    // @GetMapping("/list")
    // @Operation(summary = "获得邀请记录列表")
    // @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    // @PreAuthorize("@ss.hasPermission('llm:invitation-records:query')")
    // public CommonResult<List<InvitationRecordsRespVO>> getInvitationRecordsList(@RequestParam("ids") Collection<Long> ids) {
    //     List<InvitationRecordsDO> list = invitationRecordsService.getInvitationRecordsList(ids);
    //     return success(InvitationRecordsConvert.INSTANCE.convertList(list));
    // }
    //
    // @GetMapping("/page")
    // @Operation(summary = "获得邀请记录分页")
    // @PreAuthorize("@ss.hasPermission('llm:invitation-records:query')")
    // public CommonResult<PageResult<InvitationRecordsRespVO>> getInvitationRecordsPage(@Valid InvitationRecordsPageReqVO pageVO) {
    //     PageResult<InvitationRecordsDO> pageResult = invitationRecordsService.getInvitationRecordsPage(pageVO);
    //     return success(InvitationRecordsConvert.INSTANCE.convertPage(pageResult));
    // }
    //

}
