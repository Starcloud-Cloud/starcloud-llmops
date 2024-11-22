package com.starcloud.ops.business.product.controller.admin.spu;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.starcloud.ops.business.product.controller.admin.spu.vo.*;
import com.starcloud.ops.business.product.controller.app.property.vo.value.AppProductPropertyValueDetailRespVO;
import com.starcloud.ops.business.product.controller.app.spu.vo.AppProductSpuDetailRespVO;
import com.starcloud.ops.business.product.controller.app.spu.vo.AppProductSpuPageReqVO;
import com.starcloud.ops.business.product.controller.app.spu.vo.AppProductSpuPageRespVO;
import com.starcloud.ops.business.product.convert.spu.ProductSpuConvert;
import com.starcloud.ops.business.product.dal.dataobject.sku.ProductSkuDO;
import com.starcloud.ops.business.product.dal.dataobject.spu.ProductSpuDO;
import com.starcloud.ops.business.product.enums.ErrorCodeConstants;
import com.starcloud.ops.business.product.enums.spu.ProductSpuStatusEnum;
import com.starcloud.ops.business.product.service.sku.ProductSkuService;
import com.starcloud.ops.business.product.service.spu.ProductSpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.product.enums.ErrorCodeConstants.SKU_NOT_EXISTS;

@Tag(name = "星河云海 -管理后台 - 商品 SPU")
@RestController
@RequestMapping("/llm/product/spu")
public class ProductSpuController {

    @Resource
    private ProductSpuService productSpuService;
    @Resource
    private ProductSkuService productSkuService;

    @PostMapping("/create")
    @Operation(summary = "创建商品 SPU")
    public CommonResult<Long> createProductSpu(@RequestBody ProductSpuCreateReqVO createReqVO) {
        return success(productSpuService.createSpu(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新商品 SPU")
    public CommonResult<Boolean> updateSpu(@Valid @RequestBody ProductSpuUpdateReqVO updateReqVO) {
        productSpuService.updateSpu(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新商品 SPU Status")
    public CommonResult<Boolean> updateStatus(@Valid @RequestBody ProductSpuUpdateStatusReqVO updateReqVO) {
        productSpuService.updateSpuStatus(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除商品 SPU")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<Boolean> deleteSpu(@RequestParam("id") Long id) {
        productSpuService.deleteSpu(id);
        return success(true);
    }

    @GetMapping("/get-detail")
    @Operation(summary = "获得商品 SPU 明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('product:spu:query')")
    public CommonResult<ProductSpuDetailRespVO> getSpuDetail(@RequestParam("id") Long id) {
        // 获得商品 SPU
        ProductSpuDO spu = productSpuService.getSpu(id);
        if (spu == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.SPU_NOT_EXISTS);
        }
        // 查询商品 SKU
        List<ProductSkuDO> skus = productSkuService.getSkuListBySpuId(spu.getId());
        return success(ProductSpuConvert.INSTANCE.convertForSpuDetailRespVO(spu, skus));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获得商品 SPU 精简列表")
    public CommonResult<List<ProductSpuSimpleRespVO>> getSpuSimpleList() {
        List<ProductSpuDO> list = productSpuService.getSpuListByStatus(ProductSpuStatusEnum.ENABLE.getStatus());
        // 降序排序后，返回给前端
        list.sort(Comparator.comparing(ProductSpuDO::getSort).reversed());
        return success(ProductSpuConvert.INSTANCE.convertList02(list));
    }

    @GetMapping("/list")
    @Operation(summary = "获得商品 SPU 详情列表")
    @Parameter(name = "spuIds", description = "spu 编号列表", required = true, example = "[1,2,3]")
    public CommonResult<List<ProductSpuDetailRespVO>> getSpuList(@RequestParam("spuIds") Collection<Long> spuIds) {
        return success(ProductSpuConvert.INSTANCE.convertForSpuDetailRespListVO(
                productSpuService.getSpuList(spuIds), productSkuService.getSkuListBySpuId(spuIds)));
    }

    @GetMapping("/page")
    @Operation(summary = "获得商品 SPU 分页")
    public CommonResult<PageResult<ProductSpuRespVO>> getSpuPage(@Valid ProductSpuPageReqVO pageVO) {
        return success(ProductSpuConvert.INSTANCE.convertPage(productSpuService.getSpuPage(pageVO)));
    }

    @GetMapping("/get-count")
    @Operation(summary = "获得商品 SPU 分页 tab count")
    public CommonResult<Map<Integer, Long>> getSpuCount() {
        return success(productSpuService.getTabsCount());
    }

    @GetMapping("/export")
    @Operation(summary = "导出商品")
    @OperateLog(type = EXPORT)
    public void exportUserList(@Validated ProductSpuExportReqVO reqVO,
                               HttpServletResponse response) throws IOException {
        List<ProductSpuDO> spuList = productSpuService.getSpuList(reqVO);
        // 导出 Excel
        List<ProductSpuExcelVO> datas = ProductSpuConvert.INSTANCE.convertList03(spuList);
        ExcelUtils.write(response, "商品列表.xls", "数据", ProductSpuExcelVO.class, datas);
    }


    //=======================ADMIN====USER=======================
    @GetMapping("/u/list")
    @Operation(summary = "系统会员-获得商品 SPU 列表")
    @Parameters({
            @Parameter(name = "recommendType", description = "推荐类型", required = true), // 参见 AppProductSpuPageReqVO.RECOMMEND_TYPE_XXX 常量
            @Parameter(name = "count", description = "数量", required = true)
    })
    public CommonResult<List<AppProductSpuPageRespVO>> getSpuList(
            @RequestParam("recommendType") String recommendType,
            @RequestParam(value = "count", defaultValue = "10") Integer count) {
        List<ProductSpuDO> list = productSpuService.getSpuList(recommendType, count);
        if (CollUtil.isEmpty(list)) {
            return success(Collections.emptyList());
        }

        // 计算单价

        // 拼接返回
        List<AppProductSpuPageRespVO> voList = ProductSpuConvert.INSTANCE.convertListForGetSpuList(list);
        return success(voList);
    }

    @GetMapping("/u/page")
    @PermitAll
    @Operation(summary = "系统会员-获得商品 SPU 分页")
    public CommonResult<PageResult<AppProductSpuPageRespVO>> getSpuPage(@Valid AppProductSpuPageReqVO pageVO) {
        PageResult<ProductSpuDO> pageResult = productSpuService.getSpuPage(pageVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        // 拼接返回
        PageResult<AppProductSpuPageRespVO> voPageResult = ProductSpuConvert.INSTANCE.convertPageForGetSpuPage(pageResult);

        voPageResult.getList().stream().forEach(spu -> {
            List<ProductSkuDO> skus = productSkuService.getSkuListBySpuId(spu.getId(), true, getLoginUserId(), spu.getCategoryId());
            List<AppProductSpuPageRespVO.Sku> skuRespList = ProductSpuConvert.INSTANCE.convertListForGetSKUDetail(skus);
            skuRespList.stream().forEach(sku -> sku.setUnitPrice(calculateUnitPrice(sku)));
            spu.setSkus(skuRespList);

        });
        return success(voPageResult);
    }

    @GetMapping("/u/get-detail")
    @Operation(summary = "系统会员-获得商品 SPU 明细")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<AppProductSpuDetailRespVO> getUSpuDetail(@RequestParam("id") Long id) {
        // 获得商品 SPU
        ProductSpuDO spu = productSpuService.getSpu(id);
        if (spu == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.SPU_NOT_EXISTS);
        }
        if (!ProductSpuStatusEnum.isEnable(spu.getStatus())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.SPU_NOT_ENABLE);
        }

        // 拼接返回
        List<ProductSkuDO> skus = productSkuService.getSkuListBySpuId(spu.getId());
        AppProductSpuDetailRespVO detailVO = ProductSpuConvert.INSTANCE.convertForGetSpuDetail(spu, skus);
        return success(detailVO);
    }

    @GetMapping("/u/like_name")
    @Operation(summary = "根据商品名称获得同类型商品")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<List<Long>> getSpuListByLikeName(@RequestParam("id") Long skuId) {

        ProductSkuDO sku = productSkuService.getSku(skuId);
        if (sku == null) {
            throw exception(SKU_NOT_EXISTS);
        }
        ProductSpuDO spu = productSpuService.getSpu(sku.getSpuId());
        if (spu == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.SPU_NOT_EXISTS);
        }

        AppProductSpuPageReqVO pageVO = new AppProductSpuPageReqVO();
        String[] parts = spu.getName().split("-");
        ArrayList<Long> list = new ArrayList<>();

        if (parts.length > 1) {
            pageVO.setName(parts[0]);
        } else {
            return success(list);
        }

        PageResult<ProductSpuDO> pageResult = productSpuService.getSpuPage(pageVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(list);
        }
        pageResult.getList().stream().forEach(spuDO -> {
            List<ProductSkuDO> skus = productSkuService.getSkuListBySpuId(spuDO.getId());
            list.add(skus.get(0).getId());
        });

        return success(list);
    }

    @GetMapping("/u/special_offer")
    @Operation(summary = "系统会员-获得特价商品")
    public CommonResult<AppProductSpuPageRespVO> getSpecialOffer() {
        ProductSpuDO specialOfferSku = productSpuService.getSpecialOfferSku();

        List<AppProductSpuPageRespVO> appProductSpuPageRespVOS = ProductSpuConvert.INSTANCE.convertListForGetSpuList(Collections.singletonList(specialOfferSku));

        appProductSpuPageRespVOS.stream().forEach(spu -> {
            List<ProductSkuDO> skus = productSkuService.getSkuListBySpuId(spu.getId());
            spu.setSkus(ProductSpuConvert.INSTANCE.convertListForGetSKUDetail(skus));

        });
        return success(appProductSpuPageRespVOS.get(0));
    }


    private Integer calculateUnitPrice(AppProductSpuPageRespVO.Sku sku) {
        List<AppProductPropertyValueDetailRespVO> properties = sku.getProperties();

        for (AppProductPropertyValueDetailRespVO property : properties) {
            switch (property.getValueName()) {
                case "3月":
                    return sku.getPrice() / 3;
                case "6月":
                    return sku.getPrice() / 6;
                case "1年":
                    return sku.getPrice() / 12;
                case "2年":
                    return sku.getPrice() / 24;
                case "3年":
                    return sku.getPrice() / 36;
                case "1月":
                default:
                    return sku.getPrice();
            }
        }

        return sku.getPrice();
    }

}
