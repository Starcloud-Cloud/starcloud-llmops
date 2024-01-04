package com.starcloud.ops.business.promotion.service.promocode;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template.PromoCodeTemplateCreateReqVO;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template.PromoCodeTemplatePageReqVO;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template.PromoCodeTemplateUpdateReqVO;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeTemplateDO;
import com.starcloud.ops.business.promotion.enums.common.PromotionCodeTypeEnum;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * 兑换码模板 Service 接口
 *
 * @author Cusack Alan
 */
public interface PromoCodeTemplateService {

    /**
     * 创建兑换码模板
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPromoCodeTemplate(@Valid PromoCodeTemplateCreateReqVO createReqVO);

    /**
     * 更新兑换码模板
     *
     * @param updateReqVO 更新信息
     */
    void updatePromoCodeTemplate(@Valid PromoCodeTemplateUpdateReqVO updateReqVO);

    /**
     * 更新兑换码模板的状态
     *
     * @param id     编号
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 删除兑换码模板
     *
     * @param id 编号
     */
    void deleteTemplate(Long id);

    /**
     * 获得兑换码模板
     *
     * @param id 编号
     * @return 兑换码模板
     */
    PromoCodeTemplateDO getTemplate(Long id);


    /**
     * 获得兑换码模板
     *
     * @param code 兑换码编号
     * @return 兑换码模板
     */
    PromoCodeTemplateDO getTemplate(String code, Integer codeType);

    /**
     * 获得兑换码模板分页
     *
     * @param pageReqVO 分页查询
     * @return 兑换码模板分页
     */
    PageResult<PromoCodeTemplateDO> getTemplatePage(PromoCodeTemplatePageReqVO pageReqVO);

    /**
     * 更新兑换码模板的领取数量
     *
     * @param id        兑换码模板编号
     * @param incrCount 增加数量
     */
    void updateTemplateTakeCount(Long id, int incrCount);

    /**
     * 获得指定领取方式的兑换码模板
     *
     * @param codeType 领取方式
     * @return 兑换码模板列表
     */
    List<PromoCodeTemplateDO> getTemplateListByCodeType(PromotionCodeTypeEnum codeType);

    /**
     * 获得兑换码模板列表
     *
     * @param canTakeTypes      可领取的类型列表
     * @param productScope      商品使用范围类型
     * @param productScopeValue 商品使用范围编号
     * @param count             查询数量
     * @return 兑换码模板列表
     */
    List<PromoCodeTemplateDO> getTemplateList(List<Integer> canTakeTypes, Integer productScope,
                                           Long productScopeValue, Integer count);

    /**
     * 获得兑换码模版列表
     *
     * @param ids 兑换码模版编号
     * @return 兑换码模版列表
     */
    List<PromoCodeTemplateDO> getTemplateList(Collection<Long> ids);

}
