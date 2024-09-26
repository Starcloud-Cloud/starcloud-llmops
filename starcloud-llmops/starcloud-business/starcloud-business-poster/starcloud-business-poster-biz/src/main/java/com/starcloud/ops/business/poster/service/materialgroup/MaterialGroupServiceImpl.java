package com.starcloud.ops.business.poster.service.materialgroup;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.util.MyBatisUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialSaveReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupPageReqVO;
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
        // TODO 异步任务  将base64 上传后同步到缩略图
        materialGroup
                // .setThumbnail(materialReqVO.get(0).getThumbnail())
                .setThumbnail("https://service-oss-juzhen.mofaai.com.cn/material/202409131730528538124/5ba2fc9a3e3046fabd3761d83410931b.jpeg")
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
        List<MaterialSaveReqVO> newMaterialReqVO = updateReqVO.getMaterialSaveReqVOS().stream().peek(t -> {
            t.setGroupId(updateObj.getId());
            t.setThumbnail("https://service-oss-juzhen.mofaai.com.cn/material/202409131730528538124/5ba2fc9a3e3046fabd3761d83410931b.jpeg");
        }).collect(Collectors.toList());

        // 更新素材
        materialService.updateMaterialByGroup(updateObj.getId(), newMaterialReqVO);
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
     * @param groupId 分组编号
     */
    @Override
    public void publish(String groupId) {

        MaterialGroupDO publishGroup = validatePublish(groupId);
        List<MaterialDO> materialDOS = materialService.getMaterialByGroup(publishGroup.getId());

        // 检查素材列表是否为空
        if (materialDOS == null || materialDOS.isEmpty()) {
            throw exception(MATERIAL_PUBLISH_FAIL_EMPTY);
        }

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
        // 校验分组是否存在
        // 校验存在
        MaterialGroupDO materialGroupDO = getMaterialGroupByUid(groupId);

        if (materialGroupDO == null) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }

        // 校验当前分组是否已经存在公开的数据

        return materialGroupMapper.selectOne(MaterialGroupDO::getAssociatedId, groupId, MaterialGroupDO::getOvertStatus, Boolean.TRUE);
    }


}