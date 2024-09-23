package com.starcloud.ops.business.poster.service.materialcategory;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.MaterialCategoryListReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.MaterialCategoryPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.MaterialCategorySaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.materialcategory.MaterialCategoryDO;
import com.starcloud.ops.business.poster.dal.mysql.materialcategory.MaterialCategoryMapper;
import com.starcloud.ops.business.poster.service.material.MaterialService;
import com.starcloud.ops.business.poster.service.materialgroup.MaterialGroupService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;


import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.poster.dal.dataobject.materialcategory.MaterialCategoryDO.PARENT_ID_NULL;
import static com.starcloud.ops.business.poster.enums.ErrorCodeConstants.*;

/**
 * 素材分类 Service 实现类
 *
 * @author starcloudadmin
 */
@Service
@Validated
public class MaterialCategoryServiceImpl implements MaterialCategoryService {

    @Resource
    @Lazy
    private MaterialGroupService materialGroupService;


    @Resource
    private MaterialCategoryMapper materialCategoryMapper;

    @Override
    public Long createMaterialCategory(MaterialCategorySaveReqVO createReqVO) {
        // 校验父分类存在
        validateParentMaterialCategory(createReqVO.getParentId());
        // 插入
        MaterialCategoryDO materialCategory = BeanUtils.toBean(createReqVO, MaterialCategoryDO.class);
        materialCategoryMapper.insert(materialCategory);
        // 返回
        return materialCategory.getId();
    }


    @Override
    public void updateMaterialCategory(MaterialCategorySaveReqVO updateReqVO) {
        // 校验分类是否存在
        validateMaterialCategoryExists(updateReqVO.getId());
        // 校验父分类存在
        validateParentMaterialCategory(updateReqVO.getParentId());


        // 校验存在
        validateMaterialCategoryExists(updateReqVO.getId());
        // 更新
        MaterialCategoryDO updateObj = BeanUtils.toBean(updateReqVO, MaterialCategoryDO.class);
        materialCategoryMapper.updateById(updateObj);
    }

    @Override
    public void deleteMaterialCategory(Long id) {
        // 校验分类是否存在
        validateMaterialCategoryExists(id);
        // 校验是否还有子分类
        if (materialCategoryMapper.selectCountByParentId(id) > 0) {
            throw exception(CATEGORY_EXISTS_CHILDREN);
        }
        // 校验分类是否绑定了素材
        Long spuCount = materialGroupService.getCountByCategoryId(id);
        if (spuCount > 0) {
            throw exception(CATEGORY_HAVE_BIND_SPU);
        }
        // 删除
        materialCategoryMapper.deleteById(id);
    }

    private void validateMaterialCategoryExists(Long id) {
        if (materialCategoryMapper.selectById(id) == null) {
            throw exception(MATERIAL_CATEGORY_NOT_EXISTS);
        }
    }

    @Override
    public MaterialCategoryDO getMaterialCategory(Long id) {
        return materialCategoryMapper.selectById(id);
    }

    /**
     * 校验分类
     *
     * @param id 分类编号
     */
    @Override
    public void validateCategory(Long id) {
        MaterialCategoryDO category = materialCategoryMapper.selectById(id);
        if (category == null) {
            throw exception(MATERIAL_CATEGORY_NOT_EXISTS);
        }
        if (Objects.equals(category.getStatus(), CommonStatusEnum.DISABLE.getStatus())) {
            throw exception(CATEGORY_DISABLED, category.getName());
        }
    }

    /**
     * 获得分类的层级
     *
     * @param id 编号
     * @return 商品分类的层级
     */
    @Override
    public Integer getCategoryLevel(Long id) {
        if (Objects.equals(id, PARENT_ID_NULL)) {
            return 0;
        }
        int level = 1;
        for (int i = 0; i < Byte.MAX_VALUE; i++) {
            // 如果没有父节点，break 结束
            MaterialCategoryDO category = materialCategoryMapper.selectById(id);
            if (category == null
                    || Objects.equals(category.getParentId(), PARENT_ID_NULL)) {
                break;
            }
            // 继续递归父节点
            level++;
            id = category.getParentId();
        }
        return level;
    }

    /**
     * 获得商品分类列表
     *
     * @param listReqVO 查询条件
     * @return 商品分类列表
     */
    @Override
    public List<MaterialCategoryDO> getEnableCategoryList(MaterialCategoryListReqVO listReqVO) {
        return materialCategoryMapper.selectList(listReqVO);
    }

    /**
     * 获得开启状态的商品分类列表
     *
     * @return 商品分类列表
     */
    @Override
    public List<MaterialCategoryDO> getEnableCategoryList() {
        return materialCategoryMapper.selectListByStatus(CommonStatusEnum.ENABLE.getStatus());
    }

    /**
     * 校验商品分类是否有效。如下情况，视为无效：
     * 1. 商品分类编号不存在
     * 2. 商品分类被禁用
     *
     * @param ids 商品分类编号数组
     */
    @Override
    public void validateCategoryList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得商品分类信息
        List<MaterialCategoryDO> list = materialCategoryMapper.selectBatchIds(ids);
        Map<Long, MaterialCategoryDO> categoryMap = CollectionUtils.convertMap(list, MaterialCategoryDO::getId);
        // 校验
        ids.forEach(id -> {
            MaterialCategoryDO category = categoryMap.get(id);
            if (category == null) {
                throw exception(MATERIAL_CATEGORY_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(category.getStatus())) {
                throw exception(CATEGORY_DISABLED, category.getName());
            }
        });

    }

    @Override
    public PageResult<MaterialCategoryDO> getMaterialCategoryPage(MaterialCategoryPageReqVO pageReqVO) {
        return materialCategoryMapper.selectPage(pageReqVO);
    }


    private void validateParentMaterialCategory(Long parentId) {

        // 如果是根分类，无需验证
        if (Objects.equals(parentId, PARENT_ID_NULL)) {
            return;
        }
        // 父分类不存在
        MaterialCategoryDO category = materialCategoryMapper.selectById(parentId);
        if (category == null) {
            throw exception(CATEGORY_PARENT_NOT_EXISTS);
        }
    }


}