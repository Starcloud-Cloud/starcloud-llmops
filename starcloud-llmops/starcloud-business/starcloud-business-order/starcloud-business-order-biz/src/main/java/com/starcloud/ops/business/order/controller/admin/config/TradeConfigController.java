package com.starcloud.ops.business.order.controller.admin.config;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.order.controller.admin.config.vo.AppTradeConfigRespVO;
import com.starcloud.ops.business.order.controller.admin.config.vo.TradeConfigRespVO;
import com.starcloud.ops.business.order.controller.admin.config.vo.TradeConfigSaveReqVO;
import com.starcloud.ops.business.order.convert.config.TradeConfigConvert;
import com.starcloud.ops.business.order.dal.dataobject.config.TradeConfigDO;
import com.starcloud.ops.business.order.service.config.TradeConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 交易中心配置")
@RestController
@RequestMapping("/trade/config")
@Validated
@Deprecated
public class TradeConfigController {

    @Resource
    private TradeConfigService tradeConfigService;

    @Value("${yudao.tencent-lbs-key}")
    private String tencentLbsKey;

    @PutMapping("/save")
    @Operation(summary = "更新交易中心配置")
    @PreAuthorize("@ss.hasPermission('trade:config:save')")
    public CommonResult<Boolean> updateConfig(@Valid @RequestBody TradeConfigSaveReqVO updateReqVO) {
        tradeConfigService.saveTradeConfig(updateReqVO);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得交易中心配置")
    @PreAuthorize("@ss.hasPermission('trade:config:query')")
    public CommonResult<TradeConfigRespVO> getConfig() {
        TradeConfigDO config = tradeConfigService.getTradeConfig();
        TradeConfigRespVO configVO = TradeConfigConvert.INSTANCE.convert(config);
        if (configVO != null) {
            configVO.setTencentLbsKey(tencentLbsKey);
        }
        return success(configVO);
    }
//==========================APP================================

    @GetMapping("/get")
    @Operation(summary = "获得交易配置")
    public CommonResult<AppTradeConfigRespVO> getTradeConfig() {
        TradeConfigDO config = ObjUtil.defaultIfNull(tradeConfigService.getTradeConfig(), new TradeConfigDO());
        return success(TradeConfigConvert.INSTANCE.convert02(config).setTencentLbsKey(tencentLbsKey));
    }
}
