package cn.iocoder.yudao.module.pay.controller.admin.sign;


import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.iocoder.yudao.module.pay.api.sign.dto.PaySignCreateReqDTO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.imageio.ImageIO;
import javax.validation.*;
import javax.servlet.http.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import static cn.hutool.core.img.ImgUtil.IMAGE_TYPE_JPG;
import static cn.hutool.extra.qrcode.QrCodeUtil.QR_TYPE_SVG;
import static cn.hutool.extra.qrcode.QrCodeUtil.QR_TYPE_TXT;
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

    @PostMapping("/create_sign")
    @Operation(summary = "创建支付签约")
    public CommonResult<Long> createSign(@Valid @RequestBody PaySignCreateReqDTO createReqVO) {
        return success(paySignService.createSign(createReqVO));
    }

    @PutMapping("/submit_sign")
    @Operation(summary = "提交支付签约")
    public CommonResult<String> updateSign(@Valid @RequestBody PaySignSubmitReqVO updateReqVO) throws IOException {
        PaySignSubmitRespVO paySignSubmitRespVO = paySignService.submitSign(updateReqVO, getClientIP());

        //如果二维码要在前端显示需要转成Base64
        // QrConfig qrConfig = new QrConfig()
        //         .setWidth(240)
        //         .setHeight(240)
        //         .setMargin(1)
        //         .setImg("./static/alipay_logo.png");
        // return success(QrCodeUtil.generateAsBase64(paySignSubmitRespVO.getDisplayContent(), qrConfig, IMAGE_TYPE_JPG));

        return success(paySignSubmitRespVO.getDisplayContent());


    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除支付签约")
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


    @GetMapping("/syncSignPay")
    @Operation(summary = "签约支付")
    // @PreAuthorize("@ss.hasPermission('pay:sign:export')")
    public CommonResult<Integer> syncSignPay() throws IOException {
        return success(  paySignService.syncSignPay());
    }


}