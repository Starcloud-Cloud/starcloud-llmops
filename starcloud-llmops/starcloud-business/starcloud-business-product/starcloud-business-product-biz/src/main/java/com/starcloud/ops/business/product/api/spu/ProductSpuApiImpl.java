package com.starcloud.ops.business.product.api.spu;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.product.api.spu.dto.ProductSpuRespDTO;
import com.starcloud.ops.business.product.controller.app.spu.vo.AppProductSpuPageReqVO;
import com.starcloud.ops.business.product.convert.spu.ProductSpuConvert;
import com.starcloud.ops.business.product.dal.dataobject.spu.ProductSpuDO;
import com.starcloud.ops.business.product.service.spu.ProductSpuService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 商品 SPU API 接口实现类
 *
 * @author LeeYan9
 * @since 2022-09-06
 */
@Service
@Validated
public class ProductSpuApiImpl implements ProductSpuApi {

    @Resource
    private ProductSpuService spuService;


    @Override
    public List<ProductSpuRespDTO> getSpuList(Collection<Long> ids) {
        return ProductSpuConvert.INSTANCE.convertList2(spuService.getSpuList(ids));
    }

    @Override
    public List<ProductSpuRespDTO> validateSpuList(Collection<Long> ids) {
        return ProductSpuConvert.INSTANCE.convertList2(spuService.validateSpuList(ids));
    }

    @Override
    public ProductSpuRespDTO getSpu(Long id) {
        return ProductSpuConvert.INSTANCE.convert02(spuService.getSpu(id));
    }

    /**
     * 验证商品是否必须含有优惠券下单
     *
     * @param spuId    skuId
     * @param couponId 优惠券 ID
     */
    @Override
    public void validateSpuAndCoupon(Long spuId, Long couponId) {
        spuService.validateSpuAndCoupon(spuId,couponId);
    }

    @Override
    public void validateSpuRegisterLimit(Long userId,Long spuId) {
        spuService.validateSpuRegisterLimit(userId,spuId);
    }

    @Override
    public List<ProductSpuRespDTO> getSpuListByKeywordOrCategoryId(Long userId,String keyword,Long categoryId) {
        //
        AppProductSpuPageReqVO appProductSpuPageReqVO = new AppProductSpuPageReqVO().setKeyword(keyword).setCategoryId(categoryId);
        PageResult<ProductSpuDO> pageResult = spuService.getSpuPage(appProductSpuPageReqVO,userId);
        return ProductSpuConvert.INSTANCE.convertList2(pageResult.getList());
    }


}
