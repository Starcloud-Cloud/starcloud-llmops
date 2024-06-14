package com.starcloud.ops.business.poster.service.materialcategory;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.PosterMaterialCategoryListReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.PosterMaterialCategoryPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.PosterMaterialCategorySaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.materialcategory.PosterMaterialCategoryDO;
import com.starcloud.ops.business.poster.dal.mysql.materialcategory.PosterMaterialCategoryMapper;
import com.starcloud.ops.business.poster.service.material.PosterMaterialService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;


import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.poster.dal.dataobject.materialcategory.PosterMaterialCategoryDO.PARENT_ID_NULL;
import static com.starcloud.ops.business.poster.enums.ErrorCodeConstants.*;

/**
 * 素材分类 Service 实现类
 *
 * @author starcloudadmin
 */
@Service
@Validated
public class PosterMaterialCategoryServiceImpl implements PosterMaterialCategoryService {

    @Resource
    @Lazy
    private PosterMaterialService posterMaterialService;


    @Resource
    private PosterMaterialCategoryMapper posterMaterialCategoryMapper;

    @Override
    public Long createMaterialCategory(PosterMaterialCategorySaveReqVO createReqVO) {
        // 校验父分类存在
        validateParentMaterialCategory(createReqVO.getParentId());
        // 插入
        PosterMaterialCategoryDO materialCategory = BeanUtils.toBean(createReqVO, PosterMaterialCategoryDO.class);
        posterMaterialCategoryMapper.insert(materialCategory);
        // 返回
        return materialCategory.getId();
    }


    @Override
    public void updateMaterialCategory(PosterMaterialCategorySaveReqVO updateReqVO) {
        // 校验分类是否存在
        validateMaterialCategoryExists(updateReqVO.getId());
        // 校验父分类存在
        validateParentMaterialCategory(updateReqVO.getParentId());


        // 校验存在
        validateMaterialCategoryExists(updateReqVO.getId());
        // 更新
        PosterMaterialCategoryDO updateObj = BeanUtils.toBean(updateReqVO, PosterMaterialCategoryDO.class);
        posterMaterialCategoryMapper.updateById(updateObj);
    }

    @Override
    public void deleteMaterialCategory(Long id) {
        // 校验分类是否存在
        validateMaterialCategoryExists(id);
        // 校验是否还有子分类
        if (posterMaterialCategoryMapper.selectCountByParentId(id) > 0) {
            throw exception(CATEGORY_EXISTS_CHILDREN);
        }
        // 校验分类是否绑定了素材
        Long spuCount = posterMaterialService.getMaterialCountByCategoryId(id);
        if (spuCount > 0) {
            throw exception(CATEGORY_HAVE_BIND_SPU);
        }
        // 删除
        posterMaterialCategoryMapper.deleteById(id);
    }

    private void validateMaterialCategoryExists(Long id) {
        if (posterMaterialCategoryMapper.selectById(id) == null) {
            throw exception(MATERIAL_CATEGORY_NOT_EXISTS);
        }
    }

    @Override
    public PosterMaterialCategoryDO getMaterialCategory(Long id) {
        return posterMaterialCategoryMapper.selectById(id);
    }

    /**
     * 校验分类
     *
     * @param id 分类编号
     */
    @Override
    public void validateCategory(Long id) {
        PosterMaterialCategoryDO category = posterMaterialCategoryMapper.selectById(id);
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
            PosterMaterialCategoryDO category = posterMaterialCategoryMapper.selectById(id);
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
    public List<PosterMaterialCategoryDO> getEnableCategoryList(PosterMaterialCategoryListReqVO listReqVO) {
        return posterMaterialCategoryMapper.selectList(listReqVO);
    }

    /**
     * 获得开启状态的商品分类列表
     *
     * @return 商品分类列表
     */
    @Override
    public List<PosterMaterialCategoryDO> getEnableCategoryList() {
        return posterMaterialCategoryMapper.selectListByStatus(CommonStatusEnum.ENABLE.getStatus());
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
        List<PosterMaterialCategoryDO> list = posterMaterialCategoryMapper.selectBatchIds(ids);
        Map<Long, PosterMaterialCategoryDO> categoryMap = CollectionUtils.convertMap(list, PosterMaterialCategoryDO::getId);
        // 校验
        ids.forEach(id -> {
            PosterMaterialCategoryDO category = categoryMap.get(id);
            if (category == null) {
                throw exception(MATERIAL_CATEGORY_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(category.getStatus())) {
                throw exception(CATEGORY_DISABLED, category.getName());
            }
        });

    }

    @Override
    public PageResult<PosterMaterialCategoryDO> getMaterialCategoryPage(PosterMaterialCategoryPageReqVO pageReqVO) {
        return posterMaterialCategoryMapper.selectPage(pageReqVO);
    }


    private void validateParentMaterialCategory(Long parentId) {

        // 如果是根分类，无需验证
        if (Objects.equals(parentId, PARENT_ID_NULL)) {
            return;
        }
        // 父分类不存在
        PosterMaterialCategoryDO category = posterMaterialCategoryMapper.selectById(parentId);
        if (category == null) {
            throw exception(CATEGORY_PARENT_NOT_EXISTS);
        }
    }


}