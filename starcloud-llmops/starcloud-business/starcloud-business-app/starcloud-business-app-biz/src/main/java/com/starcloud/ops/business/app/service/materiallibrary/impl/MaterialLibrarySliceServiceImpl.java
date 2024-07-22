package com.starcloud.ops.business.app.service.materiallibrary.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.*;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibrarySliceDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibrarySliceMapper;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.*;

/**
 * 素材知识库数据 Service 实现类
 *
 * @author starcloudadmin
 */
@Slf4j
@Service
@Validated
public class MaterialLibrarySliceServiceImpl implements MaterialLibrarySliceService {

    @Resource
    @Lazy
    private MaterialLibraryService materialLibraryService;

    @Resource
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;

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

        if (createReqVO.getSaveReqVOS().isEmpty()) {
            return;
        }
        List<Long> libraryIds = createReqVO.getSaveReqVOS().stream()
                .map(MaterialLibrarySliceSaveReqVO::getLibraryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(libraryIds.get(0));

        // 批量添加时 对空数据做填充
        List<MaterialLibrarySliceSaveReqVO> saveReqVOS = createReqVO.getSaveReqVOS();

        saveReqVOS.forEach(saveReq -> {
            if (saveReq.getContent() == null || saveReq.getContent().size() != tableColumnDOList.size()) {
                throw exception(MATERIAL_LIBRARY_SLICE_DATA_MISSING);
            }
        });


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
     * 根据素材库 ID 获取素材数量
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
        materialLibraryService.validateMaterialLibraryExists(bind.getLibraryId());


        List<MaterialLibrarySliceDO> sliceDOList = this.getMaterialLibrarySliceByLibraryId(bind.getLibraryId());

        return BeanUtils.toBean(sliceDOList, MaterialLibrarySliceRespVO.class);
    }

    /**
     * 通过应用 UID 获取素材数据
     *
     * @param appPageReqVO 应用
     * @return Page
     */
    @Override
    public PageResult<MaterialLibrarySliceRespVO> getMaterialLibrarySlicePageByApp(MaterialLibrarySliceAppPageReqVO appPageReqVO) {

        MaterialLibraryAppBindDO bind = materialLibraryAppBindService.getMaterialLibraryAppBind(appPageReqVO.getAppUid());

        if (Objects.isNull(bind)) {
            throw exception(MATERIAL_LIBRARY_NO_BIND_APP);
        }
        materialLibraryService.validateMaterialLibraryExists(bind.getLibraryId());

        PageResult<MaterialLibrarySliceDO> pageResult = materialLibrarySliceMapper.selectPage2(bind.getLibraryId(), appPageReqVO);

        return BeanUtils.toBean(pageResult, MaterialLibrarySliceRespVO.class);
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
        log.info("updateSliceUsedCount : libraryId:{},sliceId:{},usedCount:{}",libraryId,sliceId,usedCount);
        MaterialLibrarySliceDO slice = materialLibrarySliceMapper.selectById(sliceId);

        if (slice == null) {
            return;
        }
        materialLibrarySliceMapper.updateById(new MaterialLibrarySliceDO().setId(slice.getId()).setUsedCount(slice.getUsedCount() + usedCount));
    }

    /**
     * 仅仅复制一个新的素材库数据（不做数据操作）templateApp -> appReqVO
     *
     * @param templateLibraryId 新应用
     * @param libraryId         老应用
     */
    @Override
    public void materialLibrarySliceCopy(Long templateLibraryId, Long libraryId) {


        List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(libraryId);
        if (tableColumnDOList.isEmpty()){
            return;
        }

        MaterialLibrarySlicePageReqVO pageReqVO = new MaterialLibrarySlicePageReqVO();
        pageReqVO.setPageNo(1);
        pageReqVO.setPageSize(100);
        pageReqVO.setLibraryId(templateLibraryId);


        AtomicReference<List<MaterialLibrarySliceDO> > templateSliceDOList  = new AtomicReference<>();

        // 关闭数据权限，避免因为没有数据权限，查询不到数据，进而导致唯一校验不正确
        DataPermissionUtils.executeIgnore(() -> {

            templateSliceDOList.set(getMaterialLibrarySlicePage(pageReqVO).getList());

        });


        if (templateSliceDOList.get().isEmpty()){
            return;
        }

        templateSliceDOList.get().forEach(sliceData -> {
            sliceData.setId(null);
            sliceData.setCreator(null);
            sliceData.setUpdater(null);
            sliceData.setLibraryId(libraryId);
            List<MaterialLibrarySliceDO.TableContent> datasList = sliceData.getContent();
            if (datasList != null) {
                datasList.forEach(datas -> {
                    if (datas != null && datas.getColumnCode() != null) {
                        MaterialLibraryTableColumnDO newColumnDO = findColumnDOByCode(tableColumnDOList, datas.getColumnCode());
                        if (newColumnDO != null) {
                            datas.setColumnId(newColumnDO.getId());
                        }
                    }
                });
            }
        });


       saveBatchData(templateSliceDOList.get());

    }



    private MaterialLibraryTableColumnDO findColumnDOByCode(List<MaterialLibraryTableColumnDO> tableColumnDOList, String columnCode) {
        // 为了优化性能，这里可以考虑使用更高效的数据结构进行搜索，比如HashMap
        // 由于示例中没有给出具体的ColumnDO实现，这里简单地使用循环遍历列表进行查找
        for (MaterialLibraryTableColumnDO tableColumnDO : tableColumnDOList) {
            if (tableColumnDO.getColumnCode().equals(columnCode)) {
                return tableColumnDO; // 找到匹配的ColumnDO，返回之
            }
        }
        return null; // 如果没有找到匹配的ColumnDO，返回null
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
     * 批量保存数据
     *
     * @param list 要保存的数据
     * @return Integer 保存成功的条数
     */
    @Override
    public <T> Integer saveBatchData(List<T> list) {

        if (CollUtil.isEmpty(list)) {
            return 0;
        }

        List<MaterialLibrarySliceDO> bean = BeanUtils.toBean(list, MaterialLibrarySliceDO.class);
        materialLibrarySliceMapper.insertBatch(bean);
        bean.stream().map(MaterialLibrarySliceDO::getLibraryId).distinct().forEach(materialLibraryService::updateMaterialLibraryFileCount);
        return list.size();

    }
}