package com.starcloud.ops.business.poster.service.material;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;

import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialSaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.MaterialDO;
import com.starcloud.ops.business.poster.dal.mysql.material.MaterialMapper;
import com.starcloud.ops.business.poster.service.materialcategory.MaterialCategoryService;
import com.starcloud.ops.business.user.util.UserUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;


import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.poster.enums.ErrorCodeConstants.SPU_MATERIAL_FAIL_CATEGORY_LEVEL_ERROR;
import static com.starcloud.ops.business.poster.dal.dataobject.materialcategory.MaterialCategoryDO.CATEGORY_LEVEL;

/**
 * 海报素材 Service 实现类
 *
 * @author starcloudadmin
 */
@Service
@Validated
public class MaterialServiceImpl implements MaterialService {

    @Resource
    private MaterialCategoryService materialCategoryService;

    @Resource
    private MaterialMapper materialMapper;

    @Override
    public Long createMaterial(MaterialSaveReqVO createReqVO) {

        // 校验分类
        validateCategory(createReqVO.getCategoryId());
        // 设置信息

        // 插入
        MaterialDO material = BeanUtils.toBean(createReqVO, MaterialDO.class);
        material.setUid(IdUtil.fastSimpleUUID());
        material.setUserType(UserUtils.isAdmin()? UserTypeEnum.ADMIN.getValue():UserTypeEnum.MEMBER.getValue());
        materialMapper.insert(material);
        // 返回
        return material.getId();
    }

    @Override
    public void updateMaterial(MaterialSaveReqVO updateReqVO) {
        // 校验存在
        validateMaterialExists(updateReqVO.getId());
        // 更新
        MaterialDO updateObj = BeanUtils.toBean(updateReqVO, MaterialDO.class);
        materialMapper.updateById(updateObj);
    }

    @Override
    public void deleteMaterial(Long id) {
        // 校验存在
        validateMaterialExists(id);
        // 删除
        materialMapper.deleteById(id);
    }

    private void validateMaterialExists(Long id) {
        if (materialMapper.selectById(id) == null) {
            // throw exception(MATERIAL_NOT_EXISTS);
        }
    }

    @Override
    public MaterialDO getMaterial(Long id) {
        return materialMapper.selectById(id);
    }

    @Override
    public PageResult<MaterialDO> getMaterialPage(MaterialPageReqVO pageReqVO) {
        return materialMapper.selectPage(pageReqVO);
    }

    /**
     * 获取当前分类下素材数量
     *
     * @param categoryId 素材分类 ID
     * @return
     */
    @Override
    public Long getMaterialCountByCategoryId(Long categoryId) {
        return materialMapper.selectCount(MaterialDO::getCategoryId, categoryId);
    }


    /**
     * 校验商品分类是否合法
     *
     * @param id 商品分类编号
     */
    private void validateCategory(Long id) {
        materialCategoryService.validateCategory(id);
        // 校验层级
        if (materialCategoryService.getCategoryLevel(id) < CATEGORY_LEVEL) {
            throw exception(SPU_MATERIAL_FAIL_CATEGORY_LEVEL_ERROR);
        }
    }
}