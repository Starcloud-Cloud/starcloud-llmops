package com.starcloud.ops.business.promotion.service.article;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.promotion.controller.admin.article.vo.category.ArticleCategoryCreateReqVO;
import com.starcloud.ops.business.promotion.controller.admin.article.vo.category.ArticleCategoryPageReqVO;
import com.starcloud.ops.business.promotion.controller.admin.article.vo.category.ArticleCategoryUpdateReqVO;
import com.starcloud.ops.business.promotion.convert.article.ArticleCategoryConvert;
import com.starcloud.ops.business.promotion.dal.dataobject.article.ArticleCategoryDO;
import com.starcloud.ops.business.promotion.dal.mysql.article.ArticleCategoryMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.promotion.enums.ErrorCodeConstants.ARTICLE_CATEGORY_DELETE_FAIL_HAVE_ARTICLES;
import static com.starcloud.ops.business.promotion.enums.ErrorCodeConstants.ARTICLE_CATEGORY_NOT_EXISTS;

/**
 * 文章分类 Service 实现类
 *
 * @author HUIHUI
 */
@Service
@Validated
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    @Resource
    private ArticleCategoryMapper articleCategoryMapper;

    @Resource
    @Lazy // 延迟加载，解决循环依赖问题
    private ArticleService articleService;

    @Override
    public Long createArticleCategory(ArticleCategoryCreateReqVO createReqVO) {
        // 插入
        ArticleCategoryDO category = ArticleCategoryConvert.INSTANCE.convert(createReqVO);
        articleCategoryMapper.insert(category);
        // 返回
        return category.getId();
    }

    @Override
    public void updateArticleCategory(ArticleCategoryUpdateReqVO updateReqVO) {
        // 校验存在
        validateArticleCategoryExists(updateReqVO.getId());
        // 更新
        ArticleCategoryDO updateObj = ArticleCategoryConvert.INSTANCE.convert(updateReqVO);
        articleCategoryMapper.updateById(updateObj);
    }

    @Override
    public void deleteArticleCategory(Long id) {
        // 校验存在
        validateArticleCategoryExists(id);
        // 校验是不是存在关联文章
        Long count = articleService.getArticleCountByCategoryId(id);
        if (count > 0) {
            throw exception(ARTICLE_CATEGORY_DELETE_FAIL_HAVE_ARTICLES);
        }

        // 删除
        articleCategoryMapper.deleteById(id);
    }

    private void validateArticleCategoryExists(Long id) {
        if (articleCategoryMapper.selectById(id) == null) {
            throw exception(ARTICLE_CATEGORY_NOT_EXISTS);
        }
    }

    @Override
    public ArticleCategoryDO getArticleCategory(Long id) {
        return articleCategoryMapper.selectById(id);
    }

    @Override
    public PageResult<ArticleCategoryDO> getArticleCategoryPage(ArticleCategoryPageReqVO pageReqVO) {
        return articleCategoryMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ArticleCategoryDO> getArticleCategoryListByStatus(Integer status) {
        return articleCategoryMapper.selectListByStatus(status);
    }

}
