package com.starcloud.ops.business.app.service.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.*;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibrarySliceDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 素材知识库数据 Service 接口
 *
 * @author starcloudadmin
 */
public interface MaterialLibrarySliceService extends CommonExcelReadService {

    /**
     * 创建素材知识库数据
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMaterialLibrarySlice(@Valid MaterialLibrarySliceSaveReqVO createReqVO);

    /**
     * 创建素材知识库数据
     *
     * @param createReqVO 创建信息
     */
    void createBatchMaterialLibrarySlice(@Valid MaterialLibrarySliceBatchSaveReqVO createReqVO);

    /**
     * 更新素材知识库数据
     *
     * @param updateReqVO 更新信息
     */
    void updateMaterialLibrarySlice(@Valid MaterialLibrarySliceSaveReqVO updateReqVO);

    void updateBatchMaterialLibrarySlice(@Valid MaterialLibrarySliceBatchSaveReqVO updateReqVO);

    /**
     * 删除素材知识库数据
     *
     * @param id 编号
     */
    void deleteMaterialLibrarySlice(Long id);

    /**
     * 获得素材知识库数据
     *
     * @param id 编号
     * @return 素材知识库数据
     */
    MaterialLibrarySliceDO getMaterialLibrarySlice(Long id);


    /**
     * 根据素材库编号 获得素材知识库数据
     *
     * @param libraryId 素材库编号
     * @return 素材知识库数据
     */
    List<MaterialLibrarySliceDO> getMaterialLibrarySliceByLibraryId(Long libraryId);

    /**
     * 根据素材库编号 获得素材知识库数据
     *
     * @param libraryId 素材库编号
     * @param slices    素材编号
     * @return 素材知识库数据
     */
    List<MaterialLibrarySliceDO> getMaterialLibrarySlice(Long libraryId, List<Long> slices);

    /**
     * 获得素材知识库数据分页
     *
     * @param pageReqVO 分页查询
     * @return 素材知识库数据分页
     */
    PageResult<MaterialLibrarySliceDO> getMaterialLibrarySlicePage(MaterialLibrarySlicePageReqVO pageReqVO);

    /**
     * 批量设置数据为共享数据
     *
     * @param shareReqVO 数据共享请求 VO
     */
    void updateSliceShareStatus(MaterialLibrarySliceShareReqVO shareReqVO);


    /**
     * 获取共享数据列表
     *
     * @param libraryId 素材库 编号
     * @return 共享数据列表
     */
    List<MaterialLibrarySliceDO> getSliceShareData(Long libraryId);

    /**
     * 获取共享数据列表
     *
     * @param libraryId 素材库 编号
     * @return 共享数据列表
     */
    Long getSliceDataCountByLibraryId(Long libraryId);

    /**
     * 根据素材库编号 删除素材库数据
     *
     * @param libraryId 素材库编号
     */
    void deleteMaterialLibrarySliceByLibraryId(Long libraryId);


    /**
     * 批量删除
     *
     * @param ids 编号列表
     */
    void deleteBatch(List<Long> ids);

    /**
     * @param libraryId         素材库编号
     * @param sliceIdList       选定的素材编号
     * @param removesliceIdList 需要移除的素材列表
     * @param sortingField      排序字段
     */
    List<MaterialLibrarySliceRespVO> selectSliceBySortingField(Long libraryId, List<Long> sliceIdList, List<Long> removesliceIdList, SortingField sortingField);

    /**
     * 通过素材库 UID 获取素材数据
     *
     * @param pageReqVO  pageVO
     * @return Page
     */
    PageResult<MaterialLibrarySliceDO> getMaterialLibrarySlicePageByLibraryUid(MaterialLibrarySlicePageReqVO pageReqVO);


    /**
     * 通过素材库 UID 获取素材数据
     *
     * @param appUid  应用 编号
     * @return Page
     */
    List<MaterialLibrarySliceRespVO> getMaterialLibrarySliceListByAppUid(String appUid);


    /**
     * 更新素材知识库数据
     *
     * @param libraryId 素材库编号
     * @param sliceId   素材编号
     * @param usedCount 使用次数
     */
    void updateSliceUsedCount(Long libraryId, Long sliceId, Integer usedCount);
}