package com.starcloud.ops.business.app.service.materiallibrary.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySlicePageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceShareReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibrarySliceDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibrarySliceMapper;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MATERIAL_LIBRARY_SLICE_NOT_EXISTS;

/**
 * 素材知识库数据 Service 实现类
 *
 * @author starcloudadmin
 */
@Service
@Validated
public class MaterialLibrarySliceServiceImpl implements MaterialLibrarySliceService {

    @Resource
    private MaterialLibrarySliceMapper materialLibrarySliceMapper;

    @Override
    public Long createMaterialLibrarySlice(MaterialLibrarySliceSaveReqVO createReqVO) {
        // 插入
        MaterialLibrarySliceDO materialLibrarySlice = BeanUtils.toBean(createReqVO, MaterialLibrarySliceDO.class);

        Long nextSequence = 1L;
        // 设置数据最新的序号
        MaterialLibrarySliceDO lastSequenceSliceDO = materialLibrarySliceMapper.selectLastSequence(materialLibrarySlice.getLibraryId());

        if (lastSequenceSliceDO != null) {
            nextSequence = nextSequence + 1;
        }
        materialLibrarySlice.setSequence(nextSequence);

        materialLibrarySliceMapper.insert(materialLibrarySlice);
        // 返回
        return materialLibrarySlice.getId();
    }

    @Override
    public void updateMaterialLibrarySlice(MaterialLibrarySliceSaveReqVO updateReqVO) {
        // 校验存在
        validateMaterialLibrarySliceExists(updateReqVO.getId());
        // 更新
        MaterialLibrarySliceDO updateObj = BeanUtils.toBean(updateReqVO, MaterialLibrarySliceDO.class);
        materialLibrarySliceMapper.updateById(updateObj);
    }

    @Override
    public void deleteMaterialLibrarySlice(Long id) {
        // 校验存在
        validateMaterialLibrarySliceExists(id);
        // 删除
        materialLibrarySliceMapper.deleteById(id);
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
    public MaterialLibrarySliceDO getMaterialLibrarySliceByLibraryId(Long libraryId) {
        return null;
    }

    @Override
    public PageResult<MaterialLibrarySliceDO> getMaterialLibrarySlicePage(MaterialLibrarySlicePageReqVO pageReqVO) {
        return materialLibrarySliceMapper.selectPage(pageReqVO);
    }

    /**
     * 批量设置数据为共享数据
     *
     * @param shareReqVO
     * @return 是否成功
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
     * 根据素材库编号 删除素材库数据
     *
     * @param libraryId 素材库编号
     */
    @Override
    public void deleteMaterialLibrarySliceByLibraryId(Long libraryId) {

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
        if (sliceDO.getIsShare() && shareStatus) {
            throw exception(MATERIAL_LIBRARY_SLICE_NOT_EXISTS);
        }
    }

}