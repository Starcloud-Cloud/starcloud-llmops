package com.starcloud.ops.business.app.convert.category;

import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.category.dto.CategoryRemarkDTO;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Objects;

/**
 * 类目转换类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
@Mapper
public interface CategoryConvert {

    CategoryConvert INSTANCE = Mappers.getMapper(CategoryConvert.class);

    /**
     * 转换为 TemplateOperateDO
     *
     * @param dictData 模版操作请求
     * @return TemplateOperateDO
     */
    default AppCategoryVO convert(DictDataDO dictData) {
        AppCategoryVO category = new AppCategoryVO();
        category.setCode(dictData.getValue());
        // 如果 remark 未配置 label，则使用 label 作为 name
        category.setName(dictData.getLabel());
        category.setSort(dictData.getSort());

        String remark = dictData.getRemark();
        if (StringUtils.isBlank(remark)) {
            return null;
        }
        CategoryRemarkDTO categoryRemark = JSON.parseObject(remark, CategoryRemarkDTO.class);

        if (Objects.isNull(categoryRemark)) {
            return null;
        }
        category.setImage(categoryRemark.getImage());
        category.setIcon(categoryRemark.getIcon());

        // 国际化 name 和 description
        String locale = LocaleContextHolder.getLocale().toString();
        if (Objects.nonNull(categoryRemark.getLabel()) && categoryRemark.getLabel().containsKey(locale)) {
            category.setName(categoryRemark.getLabel().get(locale));
        }

        if (Objects.nonNull(categoryRemark.getDescription()) && categoryRemark.getDescription().containsKey(locale)) {
            category.setDescription(categoryRemark.getDescription().get(locale));
        }

        return category;
    }
}
