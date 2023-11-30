package com.starcloud.ops.business.product.api.sku;

import com.starcloud.ops.business.product.api.sku.dto.ProductSkuRespDTO;
import com.starcloud.ops.business.product.api.sku.dto.ProductSkuUpdateStockReqDTO;
import com.starcloud.ops.business.product.convert.sku.ProductSkuConvert;
import com.starcloud.ops.business.product.dal.dataobject.sku.ProductSkuDO;
import com.starcloud.ops.business.product.service.sku.ProductSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 商品 SKU API 实现类
 *
 * @author LeeYan9
 * @since 2022-09-06
 */
@Slf4j
@Service
@Validated
public class ProductSkuApiImpl implements ProductSkuApi {

    @PostConstruct
    public void ss() {
        System.out.println("sssss");
    }

    @Resource
    private ProductSkuService productSkuService;

    @Override
    public ProductSkuRespDTO getSku(Long id) {
        ProductSkuDO sku = productSkuService.getSku(id);
        return ProductSkuConvert.INSTANCE.convert02(sku);
    }

    @Override
    public List<ProductSkuRespDTO> getSkuList(Collection<Long> ids) {
        List<ProductSkuDO> skus = productSkuService.getSkuList(ids);
        return ProductSkuConvert.INSTANCE.convertList04(skus);
    }

    @Override
    public List<ProductSkuRespDTO> getSkuListBySpuId(Collection<Long> spuIds) {
        List<ProductSkuDO> skus = productSkuService.getSkuListBySpuId(spuIds);
        return ProductSkuConvert.INSTANCE.convertList04(skus);
    }

    @Override
    public void updateSkuStock(ProductSkuUpdateStockReqDTO updateStockReqDTO) {
        productSkuService.updateSkuStock(updateStockReqDTO);
    }

}
