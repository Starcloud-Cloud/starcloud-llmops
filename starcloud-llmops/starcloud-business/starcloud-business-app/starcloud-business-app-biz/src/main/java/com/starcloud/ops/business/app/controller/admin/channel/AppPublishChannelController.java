package com.starcloud.ops.business.app.controller.admin.channel;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.base.vo.request.StatusRequest;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelModifyReqVO;
import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelReqVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 应用发布渠道
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@RestController
@RequestMapping("/llm/app/publish/channel")
@Tag(name = "星河云海-应用发布渠道", description = "星河云海应用发布渠道")
public class AppPublishChannelController {

    @Resource
    private AppPublishChannelService appPublishChannelService;

    @GetMapping("/get/{uid}")
    @Operation(summary = "根据UID获得发布渠道详情", description = "根据UID获得发布渠道详情")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<AppPublishChannelRespVO> get(@Parameter(name = "uid", description = "发布 UID") @PathVariable("uid") String uid) {
        return CommonResult.success(appPublishChannelService.get(uid));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用发布渠道", description = "创建应用发布渠道")
    @ApiOperationSupport(order = 25, author = "nacoyer")
    public CommonResult<String> create(@Validated @RequestBody AppPublishChannelReqVO request) {
        appPublishChannelService.create(request);
        return CommonResult.success("创建应用发布渠道成功");
    }

    @PostMapping("/modify")
    @Operation(summary = "修改应用发布渠道", description = "修改应用发布渠道")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<String> modify(@Validated @RequestBody AppPublishChannelModifyReqVO request) {
        appPublishChannelService.modify(request);
        return CommonResult.success("修改应用发布渠道成功");
    }

    @PostMapping("/resetShareSlug")
    @Operation(summary = "重置分享链接唯一标识", description = "重置分享链接唯一标识")
    @ApiOperationSupport(order = 35, author = "nacoyer")
    public CommonResult<String> modify(@Validated @RequestBody UidRequest request) {
        return CommonResult.success(appPublishChannelService.resetShareSlug(request.getUid()));
    }

    @PostMapping("/operate")
    @Operation(summary = "启用/禁用应用发布渠道", description = "启用/禁用应用发布渠道")
    @ApiOperationSupport(order = 40, author = "nacoyer")
    public CommonResult<String> operate(@Validated @RequestBody StatusRequest request) {
        appPublishChannelService.operate(request);
        return CommonResult.success("启用/禁用应用发布渠道成功");
    }

    @PostMapping("/delete")
    @Operation(summary = "删除应用发布渠道", description = "删除应用发布渠道")
    @ApiOperationSupport(order = 45, author = "nacoyer")
    public CommonResult<String> delete(@Validated @RequestBody UidRequest request) {
        appPublishChannelService.delete(request.getUid());
        return CommonResult.success("删除应用发布渠道成功");
    }

}
