package com.starcloud.ops.business.app.service.materiallibrary.impl;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.*;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibrarySliceDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibrarySliceMapper;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.*;

/**
 * 素材知识库数据 Service 实现类
 *
 * @author starcloudadmin
 */
@Service
@Validated
public class MaterialLibrarySliceServiceImpl implements MaterialLibrarySliceService {

    @Resource
    @Lazy
    private MaterialLibraryService materialLibraryService;

    @Resource
    private MaterialLibraryAppBindService materialLibraryAppBindService;

    @Resource
    private MaterialLibrarySliceMapper materialLibrarySliceMapper;

    @Override
    public Long createMaterialLibrarySlice(MaterialLibrarySliceSaveReqVO createReqVO) {
        // 插入
        MaterialLibrarySliceDO sliceDO = BeanUtils.toBean(createReqVO, MaterialLibrarySliceDO.class);

        long nextSequence = 1L;
        // 设置数据最新的序号
        MaterialLibrarySliceDO lastSequenceSliceDO = materialLibrarySliceMapper.selectLastSequence(sliceDO.getLibraryId());

        if (lastSequenceSliceDO != null) {
            nextSequence = nextSequence + 1;
        }
        sliceDO.setSequence(nextSequence);

        materialLibrarySliceMapper.insert(sliceDO);

        materialLibraryService.updateMaterialLibraryFileCount(sliceDO.getLibraryId());
        // 返回
        return sliceDO.getId();
    }

    /**
     * 批量创建素材知识库数据
     *
     * @param createReqVO 批量创建VO
     */
    @Override
    public void createBatchMaterialLibrarySlice(MaterialLibrarySliceBatchSaveReqVO createReqVO) {
        this.saveBatchData(BeanUtils.toBean(createReqVO.getSaveReqVOS(), MaterialLibrarySliceDO.class));
    }

    @Override
    public void updateMaterialLibrarySlice(MaterialLibrarySliceSaveReqVO updateReqVO) {
        // 校验存在
        validateMaterialLibrarySliceExists(updateReqVO.getId());
        // 更新
        MaterialLibrarySliceDO updateObj = BeanUtils.toBean(updateReqVO, MaterialLibrarySliceDO.class);
        materialLibrarySliceMapper.updateById(updateObj);
    }

    /**
     * 批量更新素材知识库数据
     *
     * @param updateReqVO 批量更新 VO
     */
    @Override
    public void updateBatchMaterialLibrarySlice(MaterialLibrarySliceBatchSaveReqVO updateReqVO) {
        materialLibrarySliceMapper.updateBatch(BeanUtils.toBean(updateReqVO.getSaveReqVOS(), MaterialLibrarySliceDO.class));
    }

    @Override
    public void deleteMaterialLibrarySlice(Long id) {
        // 校验存在
        validateMaterialLibrarySliceExists(id);

        MaterialLibrarySliceDO sliceDO = materialLibrarySliceMapper.selectById(id);
        if (sliceDO == null) {
            throw exception(MATERIAL_LIBRARY_SLICE_NOT_EXISTS);
        }
        // 删除
        materialLibrarySliceMapper.deleteById(id);

        materialLibraryService.updateMaterialLibraryFileCount(sliceDO.getLibraryId());

    }


    @Override
    public MaterialLibrarySliceDO getMaterialLibrarySlice(Long id) {
        return materialLibrarySliceMapper.selectById(id);
    }

    /**
     * 根据素材库编号 获得素材知识库数据
     *
     * @param libraryId 素材库编号
     * @return 素材知识库数据
     */
    @Override
    public List<MaterialLibrarySliceDO> getMaterialLibrarySliceByLibraryId(Long libraryId) {
        return materialLibrarySliceMapper.selectListByLibraryId(libraryId);
    }

    /**
     * 根据素材库编号 获得素材知识库数据
     *
     * @param libraryId 素材库编号
     * @param slices    素材编号
     * @return 素材知识库数据
     */
    @Override
    public List<MaterialLibrarySliceDO> getMaterialLibrarySlice(Long libraryId, List<Long> slices) {
        return materialLibrarySliceMapper.selectList(libraryId, slices);
    }

    @Override
    public PageResult<MaterialLibrarySliceDO> getMaterialLibrarySlicePage(MaterialLibrarySlicePageReqVO pageReqVO) {
        if (Objects.isNull(pageReqVO.getLibraryId())) {
            throw exception(MATERIAL_LIBRARY_ID_EMPTY);
        }
        materialLibraryService.validateMaterialLibraryExists(pageReqVO.getLibraryId());

        return materialLibrarySliceMapper.selectPage(pageReqVO);
    }

    /**
     * 批量设置数据为共享数据
     *
     * @param shareReqVO 设置数据分享状态
     */
    @Override
    public void updateSliceShareStatus(MaterialLibrarySliceShareReqVO shareReqVO) {
        // 校验数据是否存在
        shareReqVO.getId().forEach(this::validateMaterialLibrarySliceExists);
        // 校验数据共享状态
        shareReqVO.getId().forEach(slice -> validateSliceShareStatus(slice, shareReqVO.getIsShare()));
        // 更新数据
        LambdaUpdateWrapper<MaterialLibrarySliceDO> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(MaterialLibrarySliceDO::getLibraryId, shareReqVO.getLibraryId());
        wrapper.in(MaterialLibrarySliceDO::getLibraryId, shareReqVO.getId());
        wrapper.set(MaterialLibrarySliceDO::getIsShare, shareReqVO.getIsShare());

        materialLibrarySliceMapper.update(wrapper);
    }

    /**
     * 获取素材库下共享数据列表
     *
     * @param libraryId 素材库 编号
     * @return 共享数据列表
     */
    @Override
    public List<MaterialLibrarySliceDO> getSliceShareData(Long libraryId) {
        return materialLibrarySliceMapper.selectSliceShareData(libraryId);
    }

    /**
     * 获取共享数据列表
     *
     * @param libraryId 素材库 编号
     * @return 共享数据列表
     */
    @Override
    public Long getSliceDataCountByLibraryId(Long libraryId) {
        return materialLibrarySliceMapper.selectSliceDataCountByLibraryId(libraryId);
    }

    /**
     * 根据素材库编号 删除素材库数据
     *
     * @param libraryId 素材库编号
     */
    @Override
    public void deleteMaterialLibrarySliceByLibraryId(Long libraryId) {
        materialLibrarySliceMapper.deleteSliceByLibraryId(libraryId);
        materialLibraryService.updateMaterialLibraryFileCount(libraryId);
    }

    /**
     * 批量删除
     *
     * @param ids 素材编列表
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        materialLibrarySliceMapper.deleteBatchIds(ids);
    }

    /**
     * @param libraryId         素材库编号
     * @param sliceIdList       选定的素材编号
     * @param removeSliceIdList 需要移除的素材列表
     * @param sortingField      排序字段
     */
    @Override
    public List<MaterialLibrarySliceRespVO> selectSliceBySortingField(Long libraryId, List<Long> sliceIdList, List<Long> removeSliceIdList, SortingField sortingField) {
        List<MaterialLibrarySliceDO> sliceDOList = materialLibrarySliceMapper.selectSliceListByUserLibraryId(libraryId, sliceIdList, removeSliceIdList, sortingField);

        return BeanUtils.toBean(sliceDOList, MaterialLibrarySliceRespVO.class);
    }

    /**
     * 通过素材库 UID 获取素材数据
     *
     * @param pageReqVO 分页 VO
     * @return page
     */
    @Override
    public PageResult<MaterialLibrarySliceDO> getMaterialLibrarySlicePageByLibraryUid(MaterialLibrarySlicePageReqVO pageReqVO) {

        if (StrUtil.isBlank(pageReqVO.getLibraryUid())) {
            throw exception(MATERIAL_LIBRARY_ID_EMPTY);
        }
        MaterialLibraryDO materialLibraryDO = materialLibraryService.validateMaterialLibraryExists(pageReqVO.getLibraryUid());

        return materialLibrarySliceMapper.selectPage(pageReqVO.setLibraryId(materialLibraryDO.getId()));

    }

    /**
     * 通过素材库 UID 获取素材数据
     *
     * @param appUid 应用 编号
     * @return Page
     */
    @Override
    public List<MaterialLibrarySliceRespVO> getMaterialLibrarySliceListByAppUid(String appUid) {
        MaterialLibraryAppBindDO bind = materialLibraryAppBindService.getMaterialLibraryAppBind(appUid);

        if (Objects.isNull(bind)) {
            throw exception(MATERIAL_LIBRARY_NO_BIND_APP);
        }
        materialLibraryService.validateMaterialLibraryExists(bind.getId());


        List<MaterialLibrarySliceDO> sliceDOList = this.getMaterialLibrarySliceByLibraryId(bind.getLibraryId());

        return BeanUtils.toBean(sliceDOList, MaterialLibrarySliceRespVO.class);
    }

    /**
     * 更新素材知识库数据
     *
     * @param libraryId 素材库编号
     * @param sliceId   素材编号
     * @param usedCount 使用次数
     */
    @Override
    public void updateSliceUsedCount(Long libraryId, Long sliceId, Integer usedCount) {
        MaterialLibrarySliceDO slice = materialLibrarySliceMapper.selectById(sliceId);

        if (slice == null) {
            return;
        }
        materialLibrarySliceMapper.updateById(new MaterialLibrarySliceDO().setId(slice.getId()).setUsedCount(slice.getUsedCount() + usedCount));
    }


    /**
     * 校验数据是否存在
     *
     * @param id 数据编号
     */
    private void validateMaterialLibrarySliceExists(Long id) {
        if (materialLibrarySliceMapper.selectById(id) == null) {
            throw exception(MATERIAL_LIBRARY_SLICE_NOT_EXISTS);
        }
    }

    /**
     * 校验数据共享状态
     *
     * @param id          数据编号
     * @param shareStatus 数据共享状态
     */
    private void validateSliceShareStatus(Long id, Boolean shareStatus) {

        MaterialLibrarySliceDO sliceDO = materialLibrarySliceMapper.selectById(id);

    }

    /**
     * 批量保存数据
     *
     * @param list 要保存的数据
     * @return Integer 保存成功的条数
     */
    @Override
    public <T> Integer saveBatchData(List<T> list) {

        List<MaterialLibrarySliceDO> bean = BeanUtils.toBean(list, MaterialLibrarySliceDO.class);
        materialLibrarySliceMapper.insertBatch(bean);
        bean.stream().map(MaterialLibrarySliceDO::getLibraryId).distinct().forEach(materialLibraryService::updateMaterialLibraryFileCount);
        return list.size();

    }
}