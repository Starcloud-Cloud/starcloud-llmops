package com.starcloud.ops.business.product.service.sku;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.starcloud.ops.business.product.api.sku.dto.ProductSkuUpdateStockReqDTO;
import com.starcloud.ops.business.product.controller.admin.sku.vo.ProductSkuBaseVO;
import com.starcloud.ops.business.product.controller.admin.sku.vo.ProductSkuCreateOrUpdateReqVO;
import com.starcloud.ops.business.product.convert.sku.ProductSkuConvert;
import com.starcloud.ops.business.product.dal.dataobject.property.ProductPropertyDO;
import com.starcloud.ops.business.product.dal.dataobject.property.ProductPropertyValueDO;
import com.starcloud.ops.business.product.dal.dataobject.sku.ProductSkuDO;
import com.starcloud.ops.business.product.dal.mysql.sku.ProductSkuMapper;
import com.starcloud.ops.business.product.service.property.ProductPropertyService;
import com.starcloud.ops.business.product.service.property.ProductPropertyValueService;
import com.starcloud.ops.business.product.service.spu.ProductSpuService;
import com.starcloud.ops.business.promotion.api.coupon.CouponApi;
import com.starcloud.ops.business.user.api.user.AdminUsersApi;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static com.starcloud.ops.business.product.enums.ErrorCodeConstants.*;

/**
 * 商品 SKU Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ProductSkuServiceImpl implements ProductSkuService {

    @Resource
    private ProductSkuMapper productSkuMapper;

    @Resource
    @Lazy // 循环依赖，避免报错
    private ProductSpuService productSpuService;
    @Resource
    @Lazy // 循环依赖，避免报错
    private ProductPropertyService productPropertyService;
    @Resource
    private ProductPropertyValueService productPropertyValueService;

    @Resource
    private AdminUsersApi adminUsersApi;
    @Resource
    private CouponApi couponApi;

    @Override
    public void deleteSku(Long id) {
        // 校验存在
        validateSkuExists(id);
        // 删除
        productSkuMapper.deleteById(id);
    }

    private void validateSkuExists(Long id) {
        if (productSkuMapper.selectById(id) == null) {
            throw exception(SKU_NOT_EXISTS);
        }
    }

    @Override
    public ProductSkuDO getSku(Long id) {
        return productSkuMapper.selectById(id);
    }

    @Override
    public List<ProductSkuDO> getSkuList() {
        return productSkuMapper.selectList();
    }

    @Override
    public List<ProductSkuDO> getSkuList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return ListUtil.empty();
        }
        return productSkuMapper.selectBatchIds(ids);
    }

    @Override
    public void validateSkuList(List<ProductSkuCreateOrUpdateReqVO> skus, Boolean specType) {
        // 0、校验skus是否为空
        if (CollUtil.isEmpty(skus)) {
            throw exception(SKU_NOT_EXISTS);
        }
        // 单规格，赋予单规格默认属性
        if (ObjectUtil.equal(specType, false)) {
            ProductSkuCreateOrUpdateReqVO skuVO = skus.get(0);
            List<ProductSkuBaseVO.Property> properties = new ArrayList<>();
            ProductSkuBaseVO.Property property = new ProductSkuBaseVO.Property();
            property.setPropertyId(ProductPropertyDO.ID_DEFAULT);
            property.setPropertyName(ProductPropertyDO.NAME_DEFAULT);
            property.setValueId(ProductPropertyValueDO.ID_DEFAULT);
            property.setValueName(ProductPropertyValueDO.NAME_DEFAULT);
            properties.add(property);
            skuVO.setProperties(properties);
            return; // 单规格不需要后续的校验
        }

        // 1、校验属性项存在
        Set<Long> propertyIds = skus.stream().filter(p -> p.getProperties() != null)
                // 遍历多个 Property 属性
                .flatMap(p -> p.getProperties().stream())
                // 将每个 Property 转换成对应的 propertyId，最后形成集合
                .map(ProductSkuCreateOrUpdateReqVO.Property::getPropertyId)
                .collect(Collectors.toSet());
        List<ProductPropertyDO> propertyList = productPropertyService.getPropertyList(propertyIds);
        if (propertyList.size() != propertyIds.size()) {
            throw exception(PROPERTY_NOT_EXISTS);
        }

        // 2. 校验，一个 SKU 下，没有重复的属性。校验方式是，遍历每个 SKU ，看看是否有重复的属性 propertyId
        Map<Long, ProductPropertyValueDO> propertyValueMap = convertMap(productPropertyValueService.getPropertyValueListByPropertyId(propertyIds), ProductPropertyValueDO::getId);
        skus.forEach(sku -> {
            Set<Long> skuPropertyIds = convertSet(sku.getProperties(), propertyItem -> propertyValueMap.get(propertyItem.getValueId()).getPropertyId());
            if (skuPropertyIds.size() != sku.getProperties().size()) {
                throw exception(SKU_PROPERTIES_DUPLICATED);
            }
        });

        // 3. 再校验，每个 Sku 的属性值的数量，是一致的。
        int attrValueIdsSize = skus.get(0).getProperties().size();
        for (int i = 1; i < skus.size(); i++) {
            if (attrValueIdsSize != skus.get(i).getProperties().size()) {
                throw exception(SPU_ATTR_NUMBERS_MUST_BE_EQUALS);
            }
        }

        // 4. 最后校验，每个 Sku 之间不是重复的
        // 每个元素，都是一个 Sku 的 attrValueId 集合。这样，通过最外层的 Set ，判断是否有重复的.
        Set<Set<Long>> skuAttrValues = new HashSet<>();
        for (ProductSkuCreateOrUpdateReqVO sku : skus) {
            // 添加失败，说明重复
            if (!skuAttrValues.add(convertSet(sku.getProperties(), ProductSkuCreateOrUpdateReqVO.Property::getValueId))) {
                throw exception(SPU_SKU_NOT_DUPLICATE);
            }
        }
    }

    @Override
    public void createSkuList(Long spuId, List<ProductSkuCreateOrUpdateReqVO> skuCreateReqList) {
        productSkuMapper.insertBatch(ProductSkuConvert.INSTANCE.convertList06(skuCreateReqList, spuId));
    }

    @Override
    public List<ProductSkuDO> getSkuListBySpuId(Long spuId) {
        return productSkuMapper.selectListBySpuId(spuId);
    }

    /**
     * 获得商品 SKU 集合
     *
     * @param spuId  spu 编号
     * @param filter 是否开启过滤
     * @return 商品sku 集合
     */
    @Override
    public List<ProductSkuDO> getSkuListBySpuId(Long spuId, Boolean filter, Long userId, Long categoryId) {
        List<ProductSkuDO> productSkuDOS = productSkuMapper.selectListBySpuId(spuId);
        if (Objects.isNull(userId)) {
            return productSkuDOS;
        }
        if (filter) {
            return productSkuDOS.stream().filter(sku -> {
                if (Objects.nonNull(sku.getOrderLimitConfig())) {
                    if (sku.getOrderLimitConfig().getIsNewUser()) {
                        if (!adminUsersApi.isNewUser(userId)) {
                            return false;
                        }
                    }
                    if (CollUtil.isNotEmpty(sku.getOrderLimitConfig().getLimitCouponTemplateId())) {
                        return couponApi.getMatchCouponCount(userId, sku.getPrice(), Collections.singletonList(sku.getSpuId()), Collections.singletonList(sku.getId()), Collections.singletonList(categoryId)) != 0;
                    }
                    return true;

                }
                return true;
            }).collect(Collectors.toList());
        }
        return productSkuDOS;
    }

    @Override
    public List<ProductSkuDO> getSkuListBySpuId(Collection<Long> spuIds) {
        if (CollUtil.isEmpty(spuIds)) {
            return Collections.emptyList();
        }
        return productSkuMapper.selectListBySpuId(spuIds);
    }

    @Override
    public void deleteSkuBySpuId(Long spuId) {
        productSkuMapper.deleteBySpuId(spuId);
    }

    @Override
    public List<ProductSkuDO> getSkuListByAlarmStock() {
        return productSkuMapper.selectListByAlarmStock();
    }

    @Override
    public int updateSkuProperty(Long propertyId, String propertyName) {
        // 获取所有的 sku
        List<ProductSkuDO> skuDOList = productSkuMapper.selectList();
        // 处理后需要更新的 sku
        List<ProductSkuDO> updateSkus = new ArrayList<>();
        if (CollUtil.isEmpty(skuDOList)) {
            return 0;
        }
        skuDOList.stream().filter(sku -> sku.getProperties() != null)
                .forEach(sku -> sku.getProperties().forEach(property -> {
                    if (property.getPropertyId().equals(propertyId)) {
                        property.setPropertyName(propertyName);
                        updateSkus.add(sku);
                    }
                }));
        if (CollUtil.isEmpty(updateSkus)) {
            return 0;
        }

        productSkuMapper.updateBatch(updateSkus);
        return updateSkus.size();
    }

    @Override
    public int updateSkuPropertyValue(Long propertyValueId, String propertyValueName, String propertyValueRemark) {
        // 获取所有的 sku
        List<ProductSkuDO> skuDOList = productSkuMapper.selectList();
        // 处理后需要更新的 sku
        List<ProductSkuDO> updateSkus = new ArrayList<>();
        if (CollUtil.isEmpty(skuDOList)) {
            return 0;
        }
        skuDOList.stream()
                .filter(sku -> sku.getProperties() != null)
                .forEach(sku -> sku.getProperties().forEach(property -> {
                    if (property.getValueId().equals(propertyValueId)) {
                        property.setValueName(propertyValueName);
                        property.setRemark(propertyValueRemark);
                        updateSkus.add(sku);
                    }
                }));
        if (CollUtil.isEmpty(updateSkus)) {
            return 0;
        }

        productSkuMapper.updateBatch(updateSkus);
        return updateSkus.size();
    }

    /**
     * @param userId 用户编号
     * @param skuId  SKU 编号
     */
    @Override
    public void canPlaceOrder(Long userId, Long skuId) {
        ProductSkuDO sku = getSku(skuId);

        if (sku == null) {
            throw exception(SKU_NOT_EXISTS);
        }

        if (Objects.isNull(sku.getOrderLimitConfig())) {
            return;
        }

        if (Objects.nonNull(sku.getOrderLimitConfig().getIsNewUser()) && sku.getOrderLimitConfig().getIsNewUser()) {
            if (!adminUsersApi.isNewUser(userId)) {
                throw exception(SKU_FAIL_NEW_USER_LIMIT);
            }
        }
        if (CollUtil.isNotEmpty(sku.getOrderLimitConfig().getLimitCouponTemplateId())) {
            if (couponApi.validateUserExitTemplateId(userId, sku.getOrderLimitConfig().getLimitCouponTemplateId())) {
                throw exception(SKU_FAIL_COUPON_LIMIT);
            }
        }
    }

    /**
     * 验证商品是否支持签约
     *
     * @param skuId SKU 编号
     */
    @Override
    public void isValidSubscriptionSupported(Long skuId) {
        ProductSkuDO sku = getSku(skuId);

        if (sku == null) {
            throw exception(SKU_NOT_EXISTS);
        }

        if (Objects.isNull(sku.getSubscribeConfig()) || !sku.getSubscribeConfig().getIsSubscribe()) {
            //  TODO 发送预警

            throw exception(SKU_NO_SUPPORT_SUBSCRIPTION);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSkuList(Long spuId, List<ProductSkuCreateOrUpdateReqVO> skus) {
        // 构建属性与 SKU 的映射关系;
        Map<String, Long> existsSkuMap = convertMap(productSkuMapper.selectListBySpuId(spuId),
                ProductSkuConvert.INSTANCE::buildPropertyKey, ProductSkuDO::getId);

        // 拆分三个集合，新插入的、需要更新的、需要删除的
        List<ProductSkuDO> insertSkus = new ArrayList<>();
        List<ProductSkuDO> updateSkus = new ArrayList<>();
        List<ProductSkuDO> allUpdateSkus = ProductSkuConvert.INSTANCE.convertList06(skus, spuId);
        allUpdateSkus.forEach(sku -> {
            String propertiesKey = ProductSkuConvert.INSTANCE.buildPropertyKey(sku);
            // 1、找得到的，进行更新
            Long existsSkuId = existsSkuMap.remove(propertiesKey);
            if (existsSkuId != null) {
                sku.setId(existsSkuId);
                updateSkus.add(sku);
                return;
            }
            // 2、找不到，进行插入
            sku.setSpuId(spuId);
            insertSkus.add(sku);
        });

        // 执行最终的批量操作
        if (CollUtil.isNotEmpty(insertSkus)) {
            productSkuMapper.insertBatch(insertSkus);
        }
        if (CollUtil.isNotEmpty(updateSkus)) {
            updateSkus.forEach(sku -> productSkuMapper.updateById(sku));
        }
        if (CollUtil.isNotEmpty(existsSkuMap)) {
            productSkuMapper.deleteBatchIds(existsSkuMap.values());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSkuStock(ProductSkuUpdateStockReqDTO updateStockReqDTO) {
        // 更新 SKU 库存
        updateStockReqDTO.getItems().forEach(item -> {
            if (item.getIncrCount() > 0) {
                productSkuMapper.updateStockIncr(item.getId(), item.getIncrCount());
            } else if (item.getIncrCount() < 0) {
                int updateStockIncr = productSkuMapper.updateStockDecr(item.getId(), item.getIncrCount());
                if (updateStockIncr == 0) {
                    throw exception(SKU_STOCK_NOT_ENOUGH);
                }
            }
        });

        // 更新 SPU 库存
        List<ProductSkuDO> skus = productSkuMapper.selectBatchIds(
                convertSet(updateStockReqDTO.getItems(), ProductSkuUpdateStockReqDTO.Item::getId));
        Map<Long, Integer> spuStockIncrCounts = ProductSkuConvert.INSTANCE.convertSpuStockMap(
                updateStockReqDTO.getItems(), skus);
        productSpuService.updateSpuStock(spuStockIncrCounts);
    }

}
