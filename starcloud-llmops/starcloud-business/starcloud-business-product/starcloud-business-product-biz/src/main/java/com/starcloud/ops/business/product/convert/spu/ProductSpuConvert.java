package com.starcloud.ops.business.product.convert.spu;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.dict.core.util.DictFrameworkUtils;
import com.starcloud.ops.business.product.api.spu.dto.ProductSpuRespDTO;
import com.starcloud.ops.business.product.api.spu.dto.SubscribeConfigDTO;
import com.starcloud.ops.business.product.controller.admin.spu.vo.*;
import com.starcloud.ops.business.product.controller.app.spu.vo.AppProductSpuDetailRespVO;
import com.starcloud.ops.business.product.controller.app.spu.vo.AppProductSpuPageReqVO;
import com.starcloud.ops.business.product.controller.app.spu.vo.AppProductSpuPageRespVO;
import com.starcloud.ops.business.product.convert.sku.ProductSkuConvert;
import com.starcloud.ops.business.product.dal.dataobject.sku.ProductSkuDO;
import com.starcloud.ops.business.product.dal.dataobject.spu.ProductSpuDO;
import com.starcloud.ops.business.product.enums.DictTypeConstants;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.util.ObjectUtil.defaultIfNull;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMultiMap;

/**
 * 商品 SPU Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface ProductSpuConvert {

    ProductSpuConvert INSTANCE = Mappers.getMapper(ProductSpuConvert.class);

//    @Mapping(target = "giveRights", expression = "java(spu.getPrice() / 100)")
//    @Mapping(target = "subscribeConfig", expression = "java(spu.getPrice() / 100)")
    @Mapping(source = "bean.giveRights", target = "giveRights")
    @Mapping(source = "bean.subscribeConfig", target = "subscribeConfig")
    ProductSpuDO convert(ProductSpuCreateReqVO bean);

    @Mapping(source = "bean.giveRights", target = "giveRights")
    @Mapping(source = "bean.subscribeConfig", target = "subscribeConfig")
    ProductSpuDO convert(ProductSpuUpdateReqVO bean);

    List<ProductSpuDO> convertList(List<ProductSpuDO> list);

    PageResult<ProductSpuRespVO> convertPage(PageResult<ProductSpuDO> page);

    ProductSpuPageReqVO convert(AppProductSpuPageReqVO bean);

    List<ProductSpuRespDTO> convertList2(List<ProductSpuDO> list);

    List<ProductSpuSimpleRespVO> convertList02(List<ProductSpuDO> list);


    @Mapping(target = "price", expression = "java(spu.getPrice() / 100)")
    @Mapping(target = "marketPrice", expression = "java(spu.getMarketPrice() / 100)")
    @Mapping(target = "costPrice", expression = "java(spu.getCostPrice() / 100)")
    ProductSpuExcelVO convert(ProductSpuDO spu);

    default List<ProductSpuExcelVO> convertList03(List<ProductSpuDO> list) {
        List<ProductSpuExcelVO> spuExcelVOs = new ArrayList<>();
        list.forEach(spu -> {
            ProductSpuExcelVO spuExcelVO = convert(spu);
            spuExcelVOs.add(spuExcelVO);
        });
        return spuExcelVOs;
    }

    // @Mapping(target = "giveRights", expression = "java(com.starcloud.ops.business.product.convert.spu.ProductSpuConvert.mapToGiveRightsVO(spu.getGiveRights()))")
    // @Mapping(target = "subscribeConfig", expression = "java(com.starcloud.ops.business.product.convert.spu.ProductSpuConvert.mapToSubscribeConfigVO(spu.getSubscribeConfig()))")
    ProductSpuDetailRespVO convert03(ProductSpuDO spu);

    public static SubscribeConfigVO mapToSubscribeConfigVO(ProductSpuDO.SubscribeConfig subscribeConfig) {
        return BeanUtil.toBean(subscribeConfig,SubscribeConfigVO.class);
    }
    public static GiveRightsVO mapToGiveRightsVO(AdminUserRightsAndLevelCommonDTO giveRights) {
        return BeanUtil.toBean(giveRights,GiveRightsVO.class);
    }

    // @Mapping(target = "giveRights", expression = "java(com.starcloud.ops.business.product.convert.spu.ProductSpuConvert.mapToGiveRightsDTO(bean.getGiveRights()))")
    // @Mapping(target = "subscribeConfig", expression = "java(com.starcloud.ops.business.product.convert.spu.ProductSpuConvert.mapToSubscribeConfigDTO(bean.getSubscribeConfig()))")
    ProductSpuRespDTO convert02(ProductSpuDO bean);

    public static SubscribeConfigDTO mapToSubscribeConfigDTO(ProductSpuDO.SubscribeConfig subscribeConfig) {
        return BeanUtil.toBean(subscribeConfig,SubscribeConfigDTO.class);
    }
    public static AdminUserRightsAndLevelCommonDTO mapToGiveRightsDTO(AdminUserRightsAndLevelCommonDTO giveRights) {
        return BeanUtil.toBean(giveRights, AdminUserRightsAndLevelCommonDTO.class);
    }
    // ========== 用户 App 相关 ==========


    PageResult<AppProductSpuPageRespVO> convertPageForGetSpuPage(PageResult<ProductSpuDO> page);

    default List<AppProductSpuPageRespVO> convertListForGetSpuList(List<ProductSpuDO> list) {
        // 处理虚拟销量
        list.forEach(spu -> spu.setSalesCount(spu.getSalesCount() + spu.getVirtualSalesCount()));
        // 处理 VO 字段
        List<AppProductSpuPageRespVO> voList = convertListForGetSpuList0(list);
        for (int i = 0; i < list.size(); i++) {
            ProductSpuDO spu = list.get(i);
            AppProductSpuPageRespVO spuVO = voList.get(i);
            spuVO.setUnitName(DictFrameworkUtils.getDictDataLabel(DictTypeConstants.PRODUCT_UNIT, spu.getUnit()));
        }
        return voList;
    }

    @Named("convertListForGetSpuList0")
    List<AppProductSpuPageRespVO> convertListForGetSpuList0(List<ProductSpuDO> list);

    @Mapping(target = "giveRights", expression = "java(com.starcloud.ops.business.product.convert.spu.ProductSpuConvert.mapToGiveRightsDTO(productSpuDO.getGiveRights()))")
    @Mapping(target = "subscribeConfig", expression = "java(com.starcloud.ops.business.product.convert.spu.ProductSpuConvert.mapToSubscribeConfigDTO(productSpuDO.getSubscribeConfig()))")
    AppProductSpuPageRespVO productSpuDOToAppProductSpuPageRespVO(ProductSpuDO productSpuDO);

    default AppProductSpuDetailRespVO convertForGetSpuDetail(ProductSpuDO spu, List<ProductSkuDO> skus) {
        // 处理 SPU
        AppProductSpuDetailRespVO spuVO = convertForGetSpuDetail(spu)
                .setSalesCount(spu.getSalesCount() + defaultIfNull(spu.getVirtualSalesCount(), 0))
                .setUnitName(DictFrameworkUtils.getDictDataLabel(DictTypeConstants.PRODUCT_UNIT, spu.getUnit()));
        // 处理 SKU
        spuVO.setSkus(convertListForGetSpuDetail(skus));
        return spuVO;
    }

    AppProductSpuDetailRespVO convertForGetSpuDetail(ProductSpuDO spu);

    List<AppProductSpuDetailRespVO.Sku> convertListForGetSpuDetail(List<ProductSkuDO> skus);

    List<AppProductSpuPageRespVO.Sku> convertListForGetSKUDetail(List<ProductSkuDO> skus);

    default ProductSpuDetailRespVO convertForSpuDetailRespVO(ProductSpuDO spu, List<ProductSkuDO> skus) {
        return convert03(spu).setSkus(ProductSkuConvert.INSTANCE.convertList(skus));
    }

    default List<ProductSpuDetailRespVO> convertForSpuDetailRespListVO(List<ProductSpuDO> spus, List<ProductSkuDO> skus) {
        Map<Long, List<ProductSkuDO>> skuMultiMap = convertMultiMap(skus, ProductSkuDO::getSpuId);
        return CollectionUtils.convertList(spus, spu -> convert03(spu)
                .setSkus(ProductSkuConvert.INSTANCE.convertList(skuMultiMap.get(spu.getId()))));
    }

}
