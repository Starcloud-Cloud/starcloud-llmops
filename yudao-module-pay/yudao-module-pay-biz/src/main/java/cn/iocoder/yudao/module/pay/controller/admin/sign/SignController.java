package cn.iocoder.yudao.module.pay.controller.admin.sign;


import cn.iocoder.yudao.module.pay.api.sign.dto.PaySignCreateReqDTO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;

import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.pay.controller.admin.sign.vo.*;
import cn.iocoder.yudao.module.pay.dal.dataobject.sign.PaySignDO;
import cn.iocoder.yudao.module.pay.service.sign.PaySignService;

@Tag(name = "管理后台 - 支付签约 ")
@RestController
@RequestMapping("/pay/sign")
@Validated
public class SignController {

    @Resource
    private PaySignService paySignService;

    @PostMapping("/create")
    @Operation(summary = "创建支付签约 ")
    @PreAuthorize("@ss.hasPermission('pay:sign:create')")
    public CommonResult<Long> createSign(@Valid @RequestBody PaySignCreateReqDTO createReqVO) {
        return success(paySignService.createSign(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新支付签约 ")
    @PreAuthorize("@ss.hasPermission('pay:sign:update')")
    public CommonResult<String> updateSign(@Valid @RequestBody PaySignSubmitReqVO updateReqVO) {
        PaySignSubmitRespVO paySignSubmitRespVO = paySignService.submitSign(updateReqVO, getClientIP());
        return success("alipays://platformapi/startApp?appId=60000157&orderStr="+paySignSubmitRespVO.getDisplayContent());
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除支付签约 ")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('pay:sign:delete')")
    public CommonResult<Boolean> deleteSign(@RequestParam("id") Long id) {
        paySignService.deleteSign(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得支付签约 ")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pay:sign:query')")
    public CommonResult<SignRespVO> getSign(@RequestParam("id") Long id) {
        PaySignDO sign = paySignService.getSign(id);
        return success(BeanUtils.toBean(sign, SignRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得支付签约分页")
    @PreAuthorize("@ss.hasPermission('pay:sign:query')")
    public CommonResult<PageResult<SignRespVO>> getSignPage(@Valid SignPageReqVO pageReqVO) {
        PageResult<PaySignDO> pageResult = paySignService.getSignPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SignRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出支付签约Excel")
    @PreAuthorize("@ss.hasPermission('pay:sign:export')")
    @OperateLog(type = EXPORT)
    public void exportSignExcel(@Valid SignPageReqVO pageReqVO,
                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PaySignDO> list = paySignService.getSignPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "支付签约.xls", "数据", SignRespVO.class,
                BeanUtils.toBean(list, SignRespVO.class));
    }

}