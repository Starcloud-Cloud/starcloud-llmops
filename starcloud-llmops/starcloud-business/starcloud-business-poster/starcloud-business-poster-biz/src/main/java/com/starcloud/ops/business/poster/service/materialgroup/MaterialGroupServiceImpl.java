package com.starcloud.ops.business.poster.service.materialgroup;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.util.MyBatisUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialSaveReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupPublishReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupRespVO;
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
import java.util.Objects;
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
    public String createMaterialGroup(MaterialGroupSaveReqVO createReqVO) {
        // // 校验分类
        // validateCategory(createReqVO.getCategoryId());
        // 插入
        MaterialGroupDO materialGroup = BeanUtils.toBean(createReqVO, MaterialGroupDO.class);
        List<MaterialSaveReqVO> materialReqVO = createReqVO.getMaterialSaveReqVOS();

        materialGroup
                .setThumbnail(materialReqVO.get(0).getThumbnail())
                .setCategoryId(createReqVO.getCategoryId())
                .setUid(IdUtil.fastSimpleUUID())
                .setUserType(UserUtils.isAdmin() ? UserTypeEnum.ADMIN.getValue() : UserTypeEnum.MEMBER.getValue());
        materialGroupMapper.insert(materialGroup);
        // 设置分组编号
        List<MaterialSaveReqVO> newMaterialReqVO = materialReqVO.stream().map(t -> t.setGroupId(materialGroup.getId())).collect(Collectors.toList());
        // 添加素材
        materialService.batchCreateMaterial(newMaterialReqVO);
        return materialGroup.getUid();
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
        List<MaterialSaveReqVO> newMaterialReqVO = updateReqVO.getMaterialSaveReqVOS().stream().peek(t -> t.setGroupId(updateObj.getId())).collect(Collectors.toList());

        // 更新素材
        materialService.updateMaterialByGroup(updateObj.getId(), newMaterialReqVO);

        if (updateReqVO.getOvertStatus()){
            this.publish(updateReqVO.getUid());
        }else {
            this.cancelPublish(updateReqVO.getUid());
        }
    }

    /**
     * 更新海报素材分组
     *
     * @param updateReqVO 更新信息
     */
    @Override
    public void updateMaterialGroupByUid(MaterialGroupSaveReqVO updateReqVO) {
        // 校验存在
        MaterialGroupDO materialGroupDO = getMaterialGroupByUid(updateReqVO.getUid());

        if (materialGroupDO == null) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }
        // 更新
        MaterialGroupDO updateObj = BeanUtils.toBean(updateReqVO, MaterialGroupDO.class);

        // 设置缩略图为素材的第一张
        updateObj.setId(materialGroupDO.getId()).setThumbnail(updateReqVO.getMaterialSaveReqVOS().get(0)
                .getThumbnail());
        materialGroupMapper.updateById(updateObj);
        List<MaterialSaveReqVO> newMaterialReqVO = updateReqVO.getMaterialSaveReqVOS().stream().map(t -> t.setGroupId(materialGroupDO.getId())).collect(Collectors.toList());

        // 更新素材
        materialService.updateMaterialByGroup(materialGroupDO.getId(), newMaterialReqVO);
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

    /**
     * 删除海报素材分组
     *
     * @param uid 编号
     */
    @Override
    public void deleteMaterialGroupByUid(String uid) {
        // 校验存在
        MaterialGroupDO materialGroupDO = getMaterialGroupByUid(uid);

        if (materialGroupDO == null) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }
        // 删除分组下的素材
        materialService.deleteMaterialByGroup(materialGroupDO.getId());
        // 删除分组
        materialGroupMapper.deleteById(materialGroupDO.getId());
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

    /**
     * 获得海报素材分组
     *
     * @param uid 编号
     * @return 海报素材分组
     */
    @Override
    public MaterialGroupDO getMaterialGroupByUid(String uid) {
        return materialGroupMapper.selectOne(MaterialGroupDO::getUid, uid);
    }

    @Override
    public PageResult<MaterialGroupRespVO> getMaterialGroupPage(MaterialGroupPageReqVO pageReqVO) {


        // 2. 分页查询
        IPage<MaterialGroupRespVO> pageResult = materialGroupMapper.selectPage(
                MyBatisUtils.buildPage(pageReqVO), pageReqVO);

        // 3. 拼接数据并返回
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());


        // List<MaterialGroupRespVO> groupRespVOS = materialGroupMapper.selectPage(pageReqVO,
        //         PageUtils.getStart(pageReqVO), pageReqVO.getPageSize());
        // return new PageResult<>(groupRespVOS, count);


        // return materialGroupMapper.selectPage(pageReqVO, PageUtils.getStart(pageReqVO), pageReqVO.getPageSize());
    }

    /**
     * 发布数据
     *
     * @param groupUid 分组编号
     */
    @Override
    public void publish(String groupUid) {

        // 校验存在
        MaterialGroupDO materialGroupDO = getMaterialGroupByUid(groupUid);

        if (materialGroupDO == null) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }

        List<MaterialDO> materialDOS = materialService.getMaterialByGroup(materialGroupDO.getId());

        // 检查素材列表是否为空
        if (materialDOS == null || materialDOS.isEmpty()) {
            throw exception(MATERIAL_PUBLISH_FAIL_EMPTY);
        }
        List<MaterialSaveReqVO>  collect= BeanUtil.copyToList(materialDOS, MaterialSaveReqVO.class);

        List<MaterialSaveReqVO> materialSaveReqVOS = collect.stream()
                .peek(t -> t.setId(null))
                .collect(Collectors.toList());
        // 检查当前分组是否已经分布过了
        MaterialGroupDO groupDO = validatePublish(groupUid);
        if (Objects.nonNull(groupDO)) {
            materialService.deleteMaterialByGroup(groupDO.getId());

            // 设置缩略图为素材的第一张
            groupDO.setThumbnail(materialDOS.get(0).getThumbnail())
                    .setName(materialDOS.get(0).getName())
                    .setStatus(Boolean.TRUE)
                    .setOvertStatus(Boolean.TRUE)
            ;
            materialGroupMapper.updateById(groupDO);

            // 设置分组编号
            List<MaterialSaveReqVO> newMaterialReqVO = materialSaveReqVOS.stream()
                    .peek(t ->{
                        t.setGroupId(groupDO.getId());
                        t.setId(null);
                    })
                    .collect(Collectors.toList());

            materialService.batchCreateMaterial(newMaterialReqVO);
        }else {

            // 1.0   复制分组
            MaterialGroupSaveReqVO publishGroupVO = BeanUtil.toBean(materialGroupDO, MaterialGroupSaveReqVO.class);
            publishGroupVO.setId(null);
            publishGroupVO.setAssociatedId(materialGroupDO.getId());
            publishGroupVO.setOvertStatus(Boolean.TRUE);
            publishGroupVO.setStatus(Boolean.TRUE);


            // 2.0   复制数据
            publishGroupVO.setMaterialSaveReqVOS(materialSaveReqVOS);

            this.createMaterialGroup(publishGroupVO);
        }


    }

    /**
     * 发布数据
     *
     * @param groupId 分组编号
     */
    @Override
    public void cancelPublish(String groupId) {

        MaterialGroupDO publishGroup = validatePublish(groupId);
        materialGroupMapper.updateById(publishGroup.setOvertStatus(Boolean.FALSE).setStatus(Boolean.FALSE));

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
     * @param uid 分组编号
     */
    @Override
    public void copyGroup(String uid) {
        MaterialGroupDO group = this.getMaterialGroupByUid(uid);
        if (group == null) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }
        List<MaterialDO> materialByGroup = materialService.getMaterialByGroup(group.getId());

        // 复制分组
        MaterialGroupSaveReqVO newGroup = BeanUtils.toBean(group, MaterialGroupSaveReqVO.class);
        newGroup.setId(null);
        // 复制数据
        materialByGroup.forEach(t -> t.setId(null));
        newGroup.setMaterialSaveReqVOS(BeanUtils.toBean(materialByGroup, MaterialSaveReqVO.class));
        this.createMaterialGroup(newGroup);

    }

    /**
     * @param sourceGroupUid 源分组编号
     * @param targetGroupUid 目标分组编号
     */
    @Override
    public void mergeGroup(String sourceGroupUid, String targetGroupUid) {
        MaterialGroupDO sourceGroup = this.getMaterialGroupByUid(sourceGroupUid);
        MaterialGroupDO targetGroup = this.getMaterialGroupByUid(targetGroupUid);
        if (sourceGroup == null || targetGroup == null) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }
        // 合并分组
        List<MaterialDO> sourceMaterial = materialService.getMaterialByGroup(sourceGroup.getId());
        List<MaterialDO> targetMaterial = materialService.getMaterialByGroup(targetGroup.getId());
        // 合并数据
        sourceMaterial.forEach(t -> t.setId(null));
        targetMaterial.addAll(sourceMaterial);
        materialService.updateMaterialByGroup(targetGroup.getId(), BeanUtils.toBean(targetMaterial, MaterialSaveReqVO.class));
        // 删除源分组
        materialService.deleteMaterialByGroup(sourceGroup.getId());
        // // 删除源分组
        // this.deleteMaterialGroup(sourceGroup.getId());

    }

    /**
     * 操作数据是否发布
     *
     * @param publishReqVO 发布数据VO
     */
    @Override
    public void handlePublish(MaterialGroupPublishReqVO publishReqVO) {
        MaterialGroupDO group = getMaterialGroupByUid(publishReqVO.getUid());

        if (group == null) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }

        if (publishReqVO.getOvertStatus()){
            this.publish(publishReqVO.getUid());
        }else {
            this.cancelPublish(publishReqVO.getUid());
        }
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
    private MaterialGroupDO validatePublish(String groupId) {

        MaterialGroupDO group = getMaterialGroupByUid(groupId);

        if (group == null) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }

        // 校验当前分组是否已经存在公开的数据
        return materialGroupMapper.selectOne(MaterialGroupDO::getAssociatedId, group.getId());
    }


}