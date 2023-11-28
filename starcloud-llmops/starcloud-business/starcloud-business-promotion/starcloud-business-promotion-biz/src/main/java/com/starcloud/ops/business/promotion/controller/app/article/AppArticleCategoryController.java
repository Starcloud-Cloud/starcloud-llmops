package com.starcloud.ops.business.promotion.controller.app.article;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.promotion.controller.app.article.vo.category.AppArticleCategoryRespVO;
import com.starcloud.ops.business.promotion.convert.article.ArticleCategoryConvert;
import com.starcloud.ops.business.promotion.dal.dataobject.article.ArticleCategoryDO;
import com.starcloud.ops.business.promotion.service.article.ArticleCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 文章分类")
@RestController
@RequestMapping("/promotion/article-category")
@Validated
public class AppArticleCategoryController {

    @Resource
    private ArticleCategoryService articleCategoryService;

    @RequestMapping("/list")
    @Operation(summary = "获得文章分类列表")
    public CommonResult<List<AppArticleCategoryRespVO>> getArticleCategoryList() {
        List<ArticleCategoryDO> categoryList = articleCategoryService.getArticleCategoryListByStatus(
                CommonStatusEnum.ENABLE.getStatus());
        categoryList.sort(Comparator.comparing(ArticleCategoryDO::getSort)); // 按 sort 降序排列
        return success(ArticleCategoryConvert.INSTANCE.convertList04(categoryList));
    }

}
