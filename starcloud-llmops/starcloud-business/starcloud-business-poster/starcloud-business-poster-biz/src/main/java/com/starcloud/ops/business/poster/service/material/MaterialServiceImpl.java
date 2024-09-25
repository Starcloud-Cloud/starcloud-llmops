package com.starcloud.ops.business.poster.service.material;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialSaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.MaterialDO;
import com.starcloud.ops.business.poster.dal.mysql.material.MaterialMapper;
import com.starcloud.ops.business.user.util.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.mp.enums.ErrorCodeConstants.MATERIAL_NOT_EXISTS;

/**
 * 海报素材 Service 实现类
 *
 * @author starcloudadmin
 */
@Service
@Validated
public class MaterialServiceImpl implements MaterialService {

    @Resource
    private MaterialMapper materialMapper;

    @Override
    public Long createMaterial(MaterialSaveReqVO createReqVO) {
        // 插入
        MaterialDO material = BeanUtils.toBean(createReqVO, MaterialDO.class);
        material.setUid(IdUtil.fastSimpleUUID());
        material.setUserType(UserUtils.isAdmin() ? UserTypeEnum.ADMIN.getValue() : UserTypeEnum.MEMBER.getValue());
        materialMapper.insert(material);
        // 返回
        return material.getId();
    }


    @Override
    public Boolean batchCreateMaterial(List<MaterialSaveReqVO> createReqVOS) {
        if (CollUtil.isEmpty(createReqVOS))
            return true;

        // 插入
        List<MaterialDO> materialDOS = BeanUtils.toBean(createReqVOS, MaterialDO.class);
        List<MaterialDO> newMaterialDOS = materialDOS.stream().peek(t -> {
            t.setUid(IdUtil.fastSimpleUUID());
            t.setUserType(UserUtils.isAdmin() ? UserTypeEnum.ADMIN.getValue() : UserTypeEnum.MEMBER.getValue());
        }).collect(Collectors.toList());

        materialMapper.insertBatch(newMaterialDOS);
        // 返回
        return Boolean.TRUE;
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
            throw exception(MATERIAL_NOT_EXISTS);
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
     * 根据分组删除海报素材数据
     *
     * @param groupId 分组编号
     */
    @Override
    public void deleteMaterialByGroup(Long groupId) {
        materialMapper.delete(MaterialDO::getGroupId, groupId);
    }

    /**
     * 根据分组编号获取数据
     *
     * @param groupId 分组编号
     * @return 海报素材数据
     */
    @Override
    public List<MaterialDO> getMaterialByGroup(Long groupId) {
        return materialMapper.selectList(MaterialDO::getGroupId, groupId);
    }

    /**
     * 更新海报素材数据
     *
     * @param materialReqVOS 海报素材数据
     */
    @Override
    public void updateMaterialByGroup(Long groupId, List<MaterialSaveReqVO> materialReqVOS) {
        List<MaterialDO> newList = BeanUtils.toBean(materialReqVOS, MaterialDO.class);
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<MaterialDO> oldList = this.getMaterialByGroup(groupId);

        List<List<MaterialDO>> diffList =
                diffList(oldList, newList, // id 不同，就认为是不同的记录
                        (oldVal, newVal) -> ObjectUtil.equal(oldVal.getId(), newVal.getId()));

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(t -> {
                t.setUid(IdUtil.fastSimpleUUID());
                t.setUserType(UserUtils.isAdmin() ? UserTypeEnum.ADMIN.getValue() : UserTypeEnum.MEMBER.getValue());
            });
            materialMapper.insertBatch(diffList.get(0));
        }
        // 更新数据
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            materialMapper.updateBatch(diffList.get(1));
        }
        // 删除
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            materialMapper.deleteBatchIds(convertList(diffList.get(2), MaterialDO::getId));
        }
    }

}