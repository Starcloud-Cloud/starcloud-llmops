// package com.starcloud.ops.business.promotion.service.promocode;
//
// import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
// import cn.iocoder.yudao.framework.common.pojo.PageResult;
// import com.starcloud.ops.business.product.api.category.ProductCategoryApi;
// import com.starcloud.ops.business.product.api.spu.ProductSpuApi;
//
// import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template.PromoCodeTemplateCreateReqVO;
// import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template.PromoCodeTemplatePageReqVO;
// import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template.PromoCodeTemplateUpdateReqVO;
// import com.starcloud.ops.business.promotion.convert.promocode.PromoCodeTemplateConvert;
// import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeTemplateDO;
// import com.starcloud.ops.business.promotion.enums.common.PromotionProductScopeEnum;
// import com.starcloud.ops.business.promotion.enums.promocode.PromoCodeTypeEnum;
// import com.starcloud.ops.business.promotion.dal.mysql.promocode.PromoCodeTemplateMapper;
// import com.starcloud.ops.business.promotion.service.coupon.CouponTemplateService;
// import org.springframework.stereotype.Service;
// import org.springframework.validation.annotation.Validated;
//
// import javax.annotation.Resource;
// import java.util.Collection;
// import java.util.List;
// import java.util.Objects;
//
// import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
//
//
// /**
//  * 兑换码模板 Service 实现类
//  *
//  * @author 芋道源码
//  */
// @Service
// @Validated
// public class PromoCodeTemplateServiceImpl implements PromoCodeTemplateService {
//
//     @Resource
//     private PromoCodeTemplateMapper PromoCodeTemplateMapper;
//
//     @Resource
//     private ProductCategoryApi productCategoryApi;
//
//     @Resource
//     private  CouponTemplateService couponTemplateService;
//     @Resource
//     private ProductSpuApi productSpuApi;
//
//     @Override
//     public Long createPromoCodeTemplate(PromoCodeTemplateCreateReqVO createReqVO) {
//         // 校验商品范围
//         validateCoupon(createReqVO.getCodeType(), createReqVO.getCouponValues(),createReqVO.getTotalCount(),createReqVO.getTakeLimitCount());
//         // 插入
//         PromoCodeTemplateDO PromoCodeTemplate = PromoCodeTemplateConvert.INSTANCE.convert(createReqVO)
//                 .setStatus(CommonStatusEnum.ENABLE.getStatus());
//         PromoCodeTemplateMapper.insert(PromoCodeTemplate);
//         // 返回
//         return PromoCodeTemplate.getId();
//     }
//
//     @Override
//     public void updatePromoCodeTemplate(PromoCodeTemplateUpdateReqVO updateReqVO) {
//         // 校验存在
//         PromoCodeTemplateDO PromoCodeTemplate = validatePromoCodeTemplateExists(updateReqVO.getId());
//         // 校验发放数量不能过小
//         if (updateReqVO.getTotalCount() < PromoCodeTemplate.getTakeCount()) {
//             // throw exception(PromoCode_TEMPLATE_TOTAL_COUNT_TOO_SMALL, PromoCodeTemplate.getTakeCount());
//         }
//         // 校验商品范围
//         validateCoupon(updateReqVO.getProductScope(), updateReqVO.getProductScopeValues());
//
//         // 更新
//         PromoCodeTemplateDO updateObj = PromoCodeTemplateConvert.INSTANCE.convert(updateReqVO);
//         PromoCodeTemplateMapper.updateById(updateObj);
//     }
//
//     @Override
//     public void updateStatus(Long id, Integer status) {
//         // 校验存在
//         validatePromoCodeTemplateExists(id);
//         // 更新
//         PromoCodeTemplateMapper.updateById(new PromoCodeTemplateDO().setId(id).setStatus(status));
//     }
//
//     @Override
//     public void deleteTemplate(Long id) {
//         // 校验存在
//         validatePromoCodeTemplateExists(id);
//         // 删除
//         PromoCodeTemplateMapper.deleteById(id);
//     }
//
//     private PromoCodeTemplateDO validatePromoCodeTemplateExists(Long id) {
//         PromoCodeTemplateDO PromoCodeTemplate = PromoCodeTemplateMapper.selectById(id);
//         if (PromoCodeTemplate == null) {
//             // throw exception(PromoCode_TEMPLATE_NOT_EXISTS);
//         }
//         return PromoCodeTemplate;
//     }
//
//     private void validateCoupon(Integer codeType, List<Long> productScopeValues) {
//         if (PromoCodeTypeEnum.ADMIN)
//         if (Objects.equals(PromotionProductScopeEnum.SPU.getScope(), productScope)) {
//             productSpuApi.validateSpuList(productScopeValues);
//         } else if (Objects.equals(PromotionProductScopeEnum.CATEGORY.getScope(), productScope)) {
//             productCategoryApi.validateCategoryList(productScopeValues);
//         }
//     }
//
//     @Override
//     public PromoCodeTemplateDO getTemplate(Long id) {
//         return PromoCodeTemplateMapper.selectById(id);
//     }
//
//     /**
//      * 获得兑换码模板
//      *
//      * @param code 兑换码编号
//      * @return 兑换码模板
//      */
//     @Override
//     public PromoCodeTemplateDO getTemplate(String code) {
//         return PromoCodeTemplateMapper.selectTemplateByCode(code);
//     }
//
//     @Override
//     public PageResult<PromoCodeTemplateDO> getTemplatePage(PromoCodeTemplatePageReqVO pageReqVO) {
//         return PromoCodeTemplateMapper.selectPage(pageReqVO);
//     }
//
//     @Override
//     public void updateTemplateTakeCount(Long id, int incrCount) {
//         PromoCodeTemplateMapper.updateTakeCount(id, incrCount);
//     }
//
//     @Override
//     public List<PromoCodeTemplateDO> getTemplateListByCodeType(PromoCodeTypeEnum takeType) {
//         return PromoCodeTemplateMapper.selectListByCodeType(takeType.getValue());
//     }
//
//     @Override
//     public List<PromoCodeTemplateDO> getTemplateList(List<Integer> canTakeTypes, Integer productScope,
//                                                   Long productScopeValue, Integer count) {
//         return PromoCodeTemplateMapper.selectList(canTakeTypes, productScope, productScopeValue, count);
//     }
//
//     @Override
//     public List<PromoCodeTemplateDO> getTemplateList(Collection<Long> ids) {
//         return PromoCodeTemplateMapper.selectBatchIds(ids);
//     }
//
// }
