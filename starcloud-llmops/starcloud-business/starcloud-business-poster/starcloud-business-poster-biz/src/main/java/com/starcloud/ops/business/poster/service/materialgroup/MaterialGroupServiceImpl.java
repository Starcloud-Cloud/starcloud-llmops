package com.starcloud.ops.business.poster.service.materialgroup;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialSaveReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupSaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.MaterialDO;
import com.starcloud.ops.business.poster.dal.dataobject.materialgroup.MaterialGroupDO;
import com.starcloud.ops.business.poster.dal.mysql.materialgroup.MaterialGroupMapper;
import com.starcloud.ops.business.poster.service.material.MaterialService;
import com.starcloud.ops.business.poster.service.materialcategory.MaterialCategoryService;
import com.starcloud.ops.business.user.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.poster.dal.dataobject.materialcategory.MaterialCategoryDO.CATEGORY_LEVEL;
import static com.starcloud.ops.business.poster.enums.ErrorCodeConstants.*;

/**
 * 海报素材分组 Service 实现类
 *
 * @author starcloudadmin
 */
@Slf4j
@Service
@Validated
public class MaterialGroupServiceImpl implements MaterialGroupService {

    @Resource
    private MaterialGroupMapper materialGroupMapper;

    @Resource
    private MaterialCategoryService materialCategoryService;

    @Resource
    private MaterialService materialService;

    @Override
    public Long createMaterialGroup(MaterialGroupSaveReqVO createReqVO) {
        // // 校验分类
        // validateCategory(createReqVO.getCategoryId());
        // 插入
        MaterialGroupDO materialGroup = BeanUtils.toBean(createReqVO, MaterialGroupDO.class);
        List<MaterialSaveReqVO> materialReqVO = createReqVO.getMaterialSaveReqVOS();
        // 设置缩略图为素材的第一张
        materialGroup
                .setUid(IdUtil.fastSimpleUUID())
                .setUserType(UserUtils.isAdmin() ? UserTypeEnum.ADMIN.getValue() : UserTypeEnum.MEMBER.getValue());
        materialGroupMapper.insert(materialGroup);
        // 设置分组编号
        List<MaterialSaveReqVO> newMaterialReqVO = materialReqVO.stream().map(t -> t.setGroupId(materialGroup.getId())).collect(Collectors.toList());
        // 添加素材
        materialService.batchCreateMaterial(newMaterialReqVO);


        return materialGroup.getId();
    }

    @Override
    public void updateMaterialGroup(MaterialGroupSaveReqVO updateReqVO) {
        // 校验存在
        validateMaterialGroupExists(updateReqVO.getId());
        // 更新
        MaterialGroupDO updateObj = BeanUtils.toBean(updateReqVO, MaterialGroupDO.class);

        // 设置缩略图为素材的第一张
        updateObj.setThumbnail(updateReqVO.getMaterialSaveReqVOS().get(0)
                .getThumbnail());
        materialGroupMapper.updateById(updateObj);
        List<MaterialSaveReqVO> newMaterialReqVO = updateReqVO.getMaterialSaveReqVOS().stream().map(t -> t.setGroupId(updateObj.getId())).collect(Collectors.toList());

        // 更新素材
        materialService.updateMaterialByGroup(updateObj.getId(), newMaterialReqVO);
    }

    @Override
    public void deleteMaterialGroup(Long id) {
        // 校验存在
        validateMaterialGroupExists(id);
        // 删除分组下的素材
        materialService.deleteMaterialByGroup(id);
        // 删除分组
        materialGroupMapper.deleteById(id);
    }

    private void validateMaterialGroupExists(Long id) {
        if (materialGroupMapper.selectById(id) == null) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }
    }

    @Override
    public MaterialGroupDO getMaterialGroup(Long id) {
        return materialGroupMapper.selectById(id);
    }

    @Override
    public PageResult<MaterialGroupDO> getMaterialGroupPage(MaterialGroupPageReqVO pageReqVO) {
        return materialGroupMapper.selectPage(pageReqVO);
    }

    /**
     * 发布数据
     *
     * @param groupId 分组编号
     */
    @Override
    public void publish(Long groupId) {

        MaterialGroupDO publishGroup = validatePublish(groupId);
        List<MaterialDO> materialDOS = materialService.getMaterialByGroup(groupId);

        // 检查素材列表是否为空
        if (materialDOS == null || materialDOS.isEmpty()) {
            throw exception(MATERIAL_PUBLISH_FAIL_EMPTY);
        }

        // 检查 publishGroup 是否为空
        if (publishGroup != null) {
            // 设置缩略图为素材的第一张
            publishGroup.setThumbnail(materialDOS.get(0).getThumbnail())
                    .setName(materialDOS.get(0).getName());
            materialGroupMapper.updateById(publishGroup);
            materialService.deleteMaterialByGroup(publishGroup.getId());

            // 设置分组编号
            List<MaterialDO> newMaterialReqVO = materialDOS.stream()
                    .peek(t -> t.setGroupId(publishGroup.getId()))
                    .collect(Collectors.toList());
            // 添加素材
            materialService.batchCreateMaterial(BeanUtils.toBean(newMaterialReqVO, MaterialSaveReqVO.class));

        } else {
            MaterialGroupDO group = materialGroupMapper.selectById(groupId);
            if (group == null) {
                throw exception(MATERIAL_GROUP_NOT_EXISTS);
            }

            // 设置其他信息
            group.setId(null)
                    .setUid(IdUtil.fastSimpleUUID())
                    .setOvertStatus(Boolean.TRUE)
                    .setAssociatedId(groupId)
                    .setUserType(UserUtils.isAdmin() ? UserTypeEnum.ADMIN.getValue() : UserTypeEnum.MEMBER.getValue());
            materialGroupMapper.insert(group);

            // 设置分组编号
            List<MaterialDO> newMaterialReqVO = materialDOS.stream()
                    .peek(t -> t.setGroupId(group.getId()))
                    .collect(Collectors.toList());
            // 添加素材
            materialService.batchCreateMaterial(BeanUtils.toBean(newMaterialReqVO, MaterialSaveReqVO.class));
        }

    }

    /**
     * 获取当前分类下分组数量
     *
     * @param categoryId 素材分类 ID
     * @return 当前分类下分组数量
     */
    @Override
    public Long getCountByCategoryId(Long categoryId) {
        return materialGroupMapper.selectCount(MaterialGroupDO::getCategoryId, categoryId);
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

    /**
     * 校验商品分类是否合法
     *
     * @param groupId 分组编号
     */
    private MaterialGroupDO validatePublish(Long groupId) {
        // 校验分组是否存在
        validateMaterialGroupExists(groupId);

        // 校验当前分组是否已经存在公开的数据
        MaterialGroupDO publishGroup = materialGroupMapper.selectOne(MaterialGroupDO::getAssociatedId, groupId, MaterialGroupDO::getOvertStatus, Boolean.TRUE);

        return publishGroup;
    }


}