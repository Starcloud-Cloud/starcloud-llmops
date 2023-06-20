package com.starcloud.ops.business.order.controller.admin.refund;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.pay.core.enums.PayChannelEnum;
import com.starcloud.ops.business.order.controller.admin.refund.vo.PayRefundDetailsRespVO;
import com.starcloud.ops.business.order.controller.admin.refund.vo.PayRefundPageItemRespVO;
import com.starcloud.ops.business.order.controller.admin.refund.vo.PayRefundPageReqVO;
import com.starcloud.ops.business.order.convert.refund.PayRefundConvert;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayAppDO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayMerchantDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderDO;
import com.starcloud.ops.business.order.dal.dataobject.refund.PayRefundDO;
import com.starcloud.ops.business.order.service.merchant.PayAppService;
import com.starcloud.ops.business.order.service.merchant.PayMerchantService;
import com.starcloud.ops.business.order.service.order.PayOrderService;
import com.starcloud.ops.business.order.service.refund.PayRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 退款订单")
@RestController
@RequestMapping("/pay/refund")
@Validated
public class PayRefundController {

    @Resource
    private PayRefundService refundService;
    @Resource
    private PayMerchantService merchantService;
    @Resource
    private PayAppService appService;
    @Resource
    private PayOrderService orderService;

    @GetMapping("/get")
    @Operation(summary = "获得退款订单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pay:refund:query')")
    public CommonResult<PayRefundDetailsRespVO> getRefund(@RequestParam("id") Long id) {
        PayRefundDO refund = refundService.getRefund(id);
        if (ObjectUtil.isNull(refund)) {
            return success(new PayRefundDetailsRespVO());
        }

        PayMerchantDO merchantDO = merchantService.getMerchant(refund.getMerchantId());
        PayAppDO appDO = appService.getApp(refund.getAppId());
        PayChannelEnum channelEnum = PayChannelEnum.getByCode(refund.getChannelCode());
        PayOrderDO orderDO = orderService.getOrder(refund.getOrderId());

        PayRefundDetailsRespVO refundDetail = PayRefundConvert.INSTANCE.refundDetailConvert(refund);
        refundDetail.setMerchantName(ObjectUtil.isNotNull(merchantDO) ? merchantDO.getName() : "未知商户");
        refundDetail.setAppName(ObjectUtil.isNotNull(appDO) ? appDO.getName() : "未知应用");
        refundDetail.setChannelCodeName(ObjectUtil.isNotNull(channelEnum) ? channelEnum.getName() : "未知渠道");
        refundDetail.setSubject(orderDO.getSubject());

        return success(refundDetail);
    }

    @GetMapping("/page")
    @Operation(summary = "获得退款订单分页")
    @PreAuthorize("@ss.hasPermission('pay:refund:query')")
    public CommonResult<PageResult<PayRefundPageItemRespVO>> getRefundPage(@Valid PayRefundPageReqVO pageVO) {
        PageResult<PayRefundDO> pageResult = refundService.getRefundPage(pageVO);
        if (CollectionUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }

        // 处理商户ID数据
        Map<Long, PayMerchantDO> merchantMap = merchantService.getMerchantMap(
                CollectionUtils.convertList(pageResult.getList(), PayRefundDO::getMerchantId));
        // 处理应用ID数据
        Map<Long, PayAppDO> appMap = appService.getAppMap(
                CollectionUtils.convertList(pageResult.getList(), PayRefundDO::getAppId));
        List<PayRefundPageItemRespVO> list = new ArrayList<>(pageResult.getList().size());
        pageResult.getList().forEach(c -> {
            PayMerchantDO merchantDO = merchantMap.get(c.getMerchantId());
            PayAppDO appDO = appMap.get(c.getAppId());
            PayChannelEnum channelEnum = PayChannelEnum.getByCode(c.getChannelCode());

            PayRefundPageItemRespVO item = PayRefundConvert.INSTANCE.pageItemConvert(c);

            item.setMerchantName(ObjectUtil.isNotNull(merchantDO) ? merchantDO.getName() : "未知商户");
            item.setAppName(ObjectUtil.isNotNull(appDO) ? appDO.getName() : "未知应用");
            item.setChannelCodeName(ObjectUtil.isNotNull(channelEnum) ? channelEnum.getName() : "未知渠道");
            list.add(item);
        });

        return success(new PageResult<>(list, pageResult.getTotal()));
    }


}
