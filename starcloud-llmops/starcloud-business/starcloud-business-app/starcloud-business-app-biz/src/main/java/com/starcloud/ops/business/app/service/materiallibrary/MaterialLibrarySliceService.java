package com.starcloud.ops.business.app.service.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySlicePageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceShareReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibrarySliceDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 素材知识库数据 Service 接口
 *
 * @author starcloudadmin
 */
public interface MaterialLibrarySliceService {

    /**
     * 创建素材知识库数据
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMaterialLibrarySlice(@Valid MaterialLibrarySliceSaveReqVO createReqVO);

    /**
     * 更新素材知识库数据
     *
     * @param updateReqVO 更新信息
     */
    void updateMaterialLibrarySlice(@Valid MaterialLibrarySliceSaveReqVO updateReqVO);

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
    MaterialLibrarySliceDO getMaterialLibrarySliceByLibraryId(Long libraryId);

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
     * @return 共享数据是否成功
     */
    void updateSliceShareStatus(MaterialLibrarySliceShareReqVO shareReqVO);


    /**
     * 获取共享数据列表
     *
     * @param libraryId 素材库 编号
     * @return 共共享数据列表
     */
    List<MaterialLibrarySliceDO> getSliceShareData(Long libraryId);

    /**
     * 根据素材库编号 删除素材库数据
     * @param libraryId 素材库编号
     */
    void deleteMaterialLibrarySliceByLibraryId(Long libraryId);
}