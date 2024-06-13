package com.starcloud.ops.business.poster.service.materialcategory;

import java.util.*;
import javax.validation.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.MaterialCategoryListReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.MaterialCategoryPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.MaterialCategorySaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.materialcategory.MaterialCategoryDO;

/**
 * 素材分类 Service 接口
 *
 * @author starcloudadmin
 */
public interface MaterialCategoryService {

    /**
     * 创建素材分类
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMaterialCategory(@Valid MaterialCategorySaveReqVO createReqVO);

    /**
     * 更新素材分类
     *
     * @param updateReqVO 更新信息
     */
    void updateMaterialCategory(@Valid MaterialCategorySaveReqVO updateReqVO);

    /**
     * 删除素材分类
     *
     * @param id 编号
     */
    void deleteMaterialCategory(Long id);

    /**
     * 获得素材分类
     *
     * @param id 编号
     * @return 素材分类
     */
    MaterialCategoryDO getMaterialCategory(Long id);

    /**
     * 校验分类
     *
     * @param id 分类编号
     */
    void validateCategory(Long id);

    /**
     * 获得分类的层级
     *
     * @param id 编号
     * @return 商品分类的层级
     */
    Integer getCategoryLevel(Long id);

    /**
     * 获得商品分类列表
     *
     * @param listReqVO 查询条件
     * @return 商品分类列表
     */
    List<MaterialCategoryDO> getEnableCategoryList(MaterialCategoryListReqVO listReqVO);

    /**
     * 获得开启状态的商品分类列表
     *
     * @return 商品分类列表
     */
    List<MaterialCategoryDO> getEnableCategoryList();

    /**
     * 校验商品分类是否有效。如下情况，视为无效：
     * 1. 商品分类编号不存在
     * 2. 商品分类被禁用
     *
     * @param ids 商品分类编号数组
     */
    void validateCategoryList(Collection<Long> ids);

    /**
     * 获得素材分类分页
     *
     * @param pageReqVO 分页查询
     * @return 素材分类分页
     */
    PageResult<MaterialCategoryDO> getMaterialCategoryPage(MaterialCategoryPageReqVO pageReqVO);

}