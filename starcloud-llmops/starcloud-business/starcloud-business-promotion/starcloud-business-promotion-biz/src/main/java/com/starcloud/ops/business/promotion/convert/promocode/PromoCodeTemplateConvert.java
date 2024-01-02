package com.starcloud.ops.business.promotion.convert.promocode;

import cn.hutool.core.map.MapUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template.*;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeTemplateDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * 兑换码模板 Convert
 *
 * @author
 */
@Mapper
public interface PromoCodeTemplateConvert {

    PromoCodeTemplateConvert INSTANCE = Mappers.getMapper(PromoCodeTemplateConvert.class);

    PromoCodeTemplateDO convert(PromoCodeTemplateCreateReqVO bean);

    PromoCodeTemplateDO convert(PromoCodeTemplateUpdateReqVO bean);

    PromoCodeTemplateRespVO convert(PromoCodeTemplateDO bean);

    PageResult<PromoCodeTemplateRespVO> convertPage(PageResult<PromoCodeTemplateDO> page);

    PromoCodeTemplatePageReqVO convert(AppPromoCodeTemplatePageReqVO pageReqVO, List<Integer> canTakeTypes, Integer productScope, Long productScopeValue);

    PageResult<AppPromoCodeTemplateRespVO> convertAppPage(PageResult<PromoCodeTemplateDO> pageResult);

    List<AppPromoCodeTemplateRespVO> convertAppList(List<PromoCodeTemplateDO> list);

    default PageResult<AppPromoCodeTemplateRespVO> convertAppPage(PageResult<PromoCodeTemplateDO> pageResult, Map<Long, Boolean> userCanTakeMap) {
        PageResult<AppPromoCodeTemplateRespVO> result = convertAppPage(pageResult);
        copyTo(result.getList(), userCanTakeMap);
        return result;
    }

    default List<AppPromoCodeTemplateRespVO> convertAppList(List<PromoCodeTemplateDO> list, Map<Long, Boolean> userCanTakeMap) {
        List<AppPromoCodeTemplateRespVO> result = convertAppList(list);
        copyTo(result, userCanTakeMap);
        return result;
    }

    default void copyTo(List<AppPromoCodeTemplateRespVO> list, Map<Long, Boolean> userCanTakeMap) {
        for (AppPromoCodeTemplateRespVO template : list) {
            // 检查已领取数量是否超过限领数量
            template.setCanTake(MapUtil.getBool(userCanTakeMap, template.getId(), false));
        }
    }

}
