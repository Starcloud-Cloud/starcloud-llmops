package com.starcloud.ops.business.log.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.response.LogAppConversationRespVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationUpdateReqVO;
import com.starcloud.ops.business.log.convert.LogAppConversationConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 应用执行日志会话")
@RestController
@RequestMapping("/log/app-conversation")
@Validated
public class LogAppConversationController {

    @Resource
    private LogAppConversationService appConversationService;

    @PostMapping("/create")
    @Operation(summary = "创建应用执行日志会话")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:create')")
    public CommonResult<Long> createAppConversation(@Valid @RequestBody LogAppConversationCreateReqVO createReqVO) {
        return success(appConversationService.createAppLogConversation(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应用执行日志会话")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:update')")
    public CommonResult<Boolean> updateAppConversation(@Valid @RequestBody LogAppConversationUpdateReqVO updateReqVO) {
        appConversationService.updateAppLogConversation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用执行日志会话")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('log:app-conversation:delete')")
    public CommonResult<Boolean> deleteAppConversation(@RequestParam("id") Long id) {
        appConversationService.deleteAppLogConversation(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应用执行日志会话")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<LogAppConversationRespVO> getAppConversation(@RequestParam("id") Long id) {
        LogAppConversationDO appConversation = appConversationService.getAppLogConversation(id);
        return success(LogAppConversationConvert.INSTANCE.convert(appConversation));
    }

    @GetMapping("/list")
    @Operation(summary = "获得应用执行日志会话列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<List<LogAppConversationRespVO>> getAppConversationList(@RequestParam("ids") Collection<Long> ids) {
        List<LogAppConversationDO> list = appConversationService.listAppLogConversation(ids);
        return success(LogAppConversationConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应用执行日志会话分页")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<PageResult<LogAppConversationRespVO>> getAppConversationPage(@Valid LogAppConversationPageReqVO pageVO) {
        PageResult<LogAppConversationDO> pageResult = appConversationService.pageAppLogConversation(pageVO);
        return success(LogAppConversationConvert.INSTANCE.convertPage(pageResult));
    }


}
