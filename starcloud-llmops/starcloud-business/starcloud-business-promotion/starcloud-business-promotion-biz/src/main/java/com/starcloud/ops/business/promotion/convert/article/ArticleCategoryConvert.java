package com.starcloud.ops.business.promotion.convert.article;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.promotion.controller.admin.article.vo.category.ArticleCategoryCreateReqVO;
import com.starcloud.ops.business.promotion.controller.admin.article.vo.category.ArticleCategoryRespVO;
import com.starcloud.ops.business.promotion.controller.admin.article.vo.category.ArticleCategorySimpleRespVO;
import com.starcloud.ops.business.promotion.controller.admin.article.vo.category.ArticleCategoryUpdateReqVO;
import com.starcloud.ops.business.promotion.controller.app.article.vo.category.AppArticleCategoryRespVO;
import com.starcloud.ops.business.promotion.dal.dataobject.article.ArticleCategoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 文章分类 Convert
 *
 * @author HUIHUI
 */
@Mapper
public interface ArticleCategoryConvert {

    ArticleCategoryConvert INSTANCE = Mappers.getMapper(ArticleCategoryConvert.class);

    ArticleCategoryDO convert(ArticleCategoryCreateReqVO bean);

    ArticleCategoryDO convert(ArticleCategoryUpdateReqVO bean);

    ArticleCategoryRespVO convert(ArticleCategoryDO bean);

    List<ArticleCategoryRespVO> convertList(List<ArticleCategoryDO> list);

    PageResult<ArticleCategoryRespVO> convertPage(PageResult<ArticleCategoryDO> page);

    List<ArticleCategorySimpleRespVO> convertList03(List<ArticleCategoryDO> list);

    List<AppArticleCategoryRespVO> convertList04(List<ArticleCategoryDO> categoryList);

}
