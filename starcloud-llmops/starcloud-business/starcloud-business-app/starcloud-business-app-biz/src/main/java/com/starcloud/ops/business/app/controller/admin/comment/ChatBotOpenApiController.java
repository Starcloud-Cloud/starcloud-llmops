package com.starcloud.ops.business.app.controller.admin.comment;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.xhs.bath.vo.response.CreativePlanBatchRespVO;
import com.starcloud.ops.business.app.controller.admin.comment.vo.wechat.ChatBotOpenApiCreateReqVO;
import com.starcloud.ops.business.app.controller.admin.comment.vo.wechat.ChatBotOpenApiUpdateReqVO;
import com.starcloud.ops.business.app.service.comment.wechat.ChatBotOpenApiDOService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

@RestController
@RequestMapping("/llm/xhs/comment/ChatBotOpenApi")
@Tag(name = " 微信开放平台", description = "微信开放平台")
public class ChatBotOpenApiController {

    @Resource
    private ChatBotOpenApiDOService chatBotOpenApiDOService;

    @PostMapping("/create")
    @Operation(summary = "添加微信开放平台 API", description = "添加微信开放平台 API")
    public CommonResult<Long> create(ChatBotOpenApiCreateReqVO createReqVO) {
        return CommonResult.success(chatBotOpenApiDOService.add(getLoginUserId(), createReqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "修改微信开放平台 API", description = "修改微信开放平台 API")
    public CommonResult<Boolean> update(ChatBotOpenApiUpdateReqVO updateReqVO) {
        chatBotOpenApiDOService.update(getLoginUserId(), updateReqVO);
        return CommonResult.success(true);
    }

    @GetMapping("/info")
    @Operation(summary = "查询微信开放平台 API", description = "查询微信开放平台 API")
    public CommonResult<PageResult<CreativePlanBatchRespVO>> info(Long id) {
        chatBotOpenApiDOService.get(getLoginUserId(), id);
        return CommonResult.success(null);
    }
}
