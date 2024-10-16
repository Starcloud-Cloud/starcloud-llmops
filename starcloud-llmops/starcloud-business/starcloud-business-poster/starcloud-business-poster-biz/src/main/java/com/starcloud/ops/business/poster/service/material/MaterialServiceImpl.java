package com.starcloud.ops.business.poster.service.material;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.model.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.model.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.service.xhs.manager.CreativeImageManager;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialSaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.MaterialDO;
import com.starcloud.ops.business.poster.dal.dataobject.materialgroup.MaterialGroupDO;
import com.starcloud.ops.business.poster.dal.mysql.material.MaterialMapper;
import com.starcloud.ops.business.poster.service.materialgroup.MaterialGroupService;
import com.starcloud.ops.business.user.util.UserUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.module.mp.enums.ErrorCodeConstants.MATERIAL_NOT_EXISTS;
import static com.starcloud.ops.business.poster.enums.ErrorCodeConstants.MATERIAL_GROUP_NOT_EXISTS;
import static java.util.Arrays.asList;

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


    @Resource
    @Lazy
    private MaterialGroupService materialGroupService;

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
            if (ObjectUtil.isEmpty(t.getUid())) {
                t.setUid(IdUtil.fastSimpleUUID());
            }
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

    /**
     * 获得海报素材
     *
     * @param uid 编号
     * @return 海报素材
     */
    @Override
    public MaterialDO getMaterialByUId(String uid) {
        // 设置查询条件
        LambdaQueryWrapper<MaterialDO> wrapper = Wrappers.lambdaQuery(MaterialDO.class);
        wrapper.eq(MaterialDO::getUid, uid);
        wrapper.eq(MaterialDO::getDeleted, Boolean.FALSE);
        wrapper.orderByDesc(MaterialDO::getUpdateTime);
        wrapper.last("Limit 1");
        // 获取数据
        return materialMapper.selectOne(wrapper);

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
                        (oldVal, newVal) -> ObjectUtil.equal(oldVal.getUid(), newVal.getUid()));

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(t -> {
                if (ObjectUtil.isEmpty(t.getUid()))
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

    /**
     * 根据海报模板UID获取海报详情
     *
     * @param uid 海报模板UID
     * @return 海报详情
     */
    @Override
    public PosterTemplateDTO posterTemplate(String uid) {

        MaterialDO material = this.getMaterialByUId(uid);

        MaterialGroupDO materialGroup = materialGroupService.getMaterialGroup(material.getGroupId());
        if (materialGroup == null) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }
        return transform(material, materialGroup.getName(), true);
    }

    /**
     * 根据分组获取海报列表
     *
     * @param uid 分组编号
     * @return 海报素材列表
     */
    @Override
    public List<PosterTemplateDTO> listPosterTemplateByGroup(String uid) {

        MaterialGroupDO materialGroup = materialGroupService.getMaterialGroupByUid(uid);

        if (materialGroup == null) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }

        // 构造查询条件
        LambdaQueryWrapper<MaterialDO> wrapper = Wrappers.lambdaQuery(MaterialDO.class);
        wrapper.select(
                MaterialDO::getId,
                MaterialDO::getGroupId,
                MaterialDO::getUid,
                MaterialDO::getName,
                MaterialDO::getThumbnail,
                MaterialDO::getType,
                MaterialDO::getMaterialTags,
                MaterialDO::getRequestParams,
                MaterialDO::getCategoryId,
                MaterialDO::getStatus,
                MaterialDO::getSort,
                MaterialDO::getUserType
        );
        wrapper.eq(MaterialDO::getGroupId, materialGroup.getId());
        wrapper.eq(MaterialDO::getDeleted, Boolean.FALSE);
        wrapper.orderByAsc(MaterialDO::getSort);

        // 查询列表
        List<MaterialDO> materialList = materialMapper.selectList(wrapper);
        return CollectionUtils.emptyIfNull(materialList).stream()
                .map(item -> transform(item,  materialGroup.getName(), false))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 转换为 PosterTemplateDTO
     *
     * @param material 素材
     * @return 海报模板
     */
    private PosterTemplateDTO transform(MaterialDO material, String groupName, boolean isNeedJson) {
        if (material == null) {
            return null;
        }
        List<PosterVariableDTO> variableList = CreativeImageManager.listVariable(material.getRequestParams());
        PosterTemplateDTO posterTemplate = new PosterTemplateDTO();
        posterTemplate.setCode(material.getUid());
        posterTemplate.setName(material.getName());
        posterTemplate.setGroup(material.getGroupId());
        posterTemplate.setGroupName(groupName);
        posterTemplate.setCategory(material.getCategoryId());
        posterTemplate.setExample(material.getThumbnail());
        posterTemplate.setSort(material.getSort());
        posterTemplate.setVariableList(variableList);
        if (isNeedJson) {
            posterTemplate.setJson(material.getMaterialData());
        }
        return posterTemplate;
    }


    public static List<List<MaterialDO>> diffList(Collection<MaterialDO> oldList, Collection<MaterialDO> newList, BiFunction<MaterialDO, MaterialDO, Boolean> sameFunc) {
        List<MaterialDO> createList = new LinkedList<>(newList); // 默认都认为是新增的，后续会进行移除
        List<MaterialDO> updateList = new ArrayList<>();
        List<MaterialDO> deleteList = new ArrayList<>();

        // 通过以 oldList 为主遍历，找出 updateList 和 deleteList
        for (MaterialDO oldObj : oldList) {
            // 1. 寻找是否有匹配的
            MaterialDO foundObj = null;
            for (Iterator<MaterialDO> iterator = createList.iterator(); iterator.hasNext(); ) {
                MaterialDO newObj = iterator.next();
                // 1.1 不匹配，则直接跳过
                if (!sameFunc.apply(oldObj, newObj)) {
                    continue;
                }
                // 1.2 匹配，则移除，并结束寻找
                iterator.remove();
                foundObj = newObj;
                break;
            }
            // 2. 匹配添加到 updateList；不匹配则添加到 deleteList 中
            if (foundObj != null) {
                foundObj.setId(oldObj.getId());
                updateList.add(foundObj);
            } else {
                deleteList.add(oldObj);
            }
        }
        return asList(createList, updateList, deleteList);
    }
}