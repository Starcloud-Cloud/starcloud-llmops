package com.starcloud.ops.business.order.controller.admin.merchant;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.channel.PayChannelCreateReqVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.channel.PayChannelPageReqVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.channel.PayChannelRespVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.channel.PayChannelUpdateReqVO;
import com.starcloud.ops.business.order.convert.channel.PayChannelConvert;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayChannelDO;
import com.starcloud.ops.business.order.service.merchant.PayChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 支付渠道")
@RestController
@RequestMapping("/pay/channel")
@Validated
public class PayChannelController {

    @Resource
    private PayChannelService channelService;

    @PostMapping("/create")
    @Operation(summary = "创建支付渠道 ")
    @PreAuthorize("@ss.hasPermission('pay:channel:create')")
    public CommonResult<Long> createChannel(@Valid @RequestBody PayChannelCreateReqVO createReqVO) {
        return success(channelService.createChannel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新支付渠道 ")
    @PreAuthorize("@ss.hasPermission('pay:channel:update')")
    public CommonResult<Boolean> updateChannel(@Valid @RequestBody PayChannelUpdateReqVO updateReqVO) {
        channelService.updateChannel(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除支付渠道 ")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pay:channel:delete')")
    public CommonResult<Boolean> deleteChannel(@RequestParam("id") Long id) {
        channelService.deleteChannel(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得支付渠道 ")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pay:channel:query')")
    public CommonResult<PayChannelRespVO> getChannel(@RequestParam("id") Long id) {
        PayChannelDO channel = channelService.getChannel(id);
        return success(PayChannelConvert.INSTANCE.convert(channel));
    }

    @GetMapping("/list")
    @Operation(summary = "获得支付渠道列表")
    @Parameter(name = "ids", description = "编号列表",
            required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('pay:channel:query')")
    public CommonResult<List<PayChannelRespVO>> getChannelList(@RequestParam("ids") Collection<Long> ids) {
        List<PayChannelDO> list = channelService.getChannelList(ids);
        return success(PayChannelConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得支付渠道分页")
    @PreAuthorize("@ss.hasPermission('pay:channel:query')")
    public CommonResult<PageResult<PayChannelRespVO>> getChannelPage(@Valid PayChannelPageReqVO pageVO) {
        PageResult<PayChannelDO> pageResult = channelService.getChannelPage(pageVO);
        return success(PayChannelConvert.INSTANCE.convertPage(pageResult));
    }



    @GetMapping("/get-channel")
    @Operation(summary = "根据条件查询微信支付渠道")
    @Parameters({
            @Parameter(name = "merchantId", description = "商户编号",
                    required = true, example = "1"),
            @Parameter(name = "appId", description = "应用编号",
                    required = true, example = "1"),
            @Parameter(name = "code", description = "支付渠道编码",
                    required = true, example = "wx_pub")
    })
    @PreAuthorize("@ss.hasPermission('pay:channel:query')")
    public CommonResult<PayChannelRespVO> getChannel(
            @RequestParam Long merchantId, @RequestParam Long appId, @RequestParam String code) {
        // 獲取渠道
        PayChannelDO channel = channelService.getChannelByConditions(merchantId, appId, code);
        if (channel == null) {
            return success(new PayChannelRespVO());
        }
        // 拼凑数据
        PayChannelRespVO respVo = PayChannelConvert.INSTANCE.convert(channel);
        return success(respVo);
    }

}
