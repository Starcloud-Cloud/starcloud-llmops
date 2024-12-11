package com.starcloud.ops.business.app.service.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnBatchSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 素材知识库表格信息 Service 接口
 *
 * @author starcloudadmin
 */
public interface MaterialLibraryTableColumnService extends CommonExcelReadService {

    /**
     * 创建素材知识库表格信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMaterialLibraryTableColumn(@Valid MaterialLibraryTableColumnSaveReqVO createReqVO);

    /**
     * 更新素材知识库表格信息
     *
     * @param updateReqVO 更新信息
     */
    void updateMaterialLibraryTableColumn(@Valid MaterialLibraryTableColumnSaveReqVO updateReqVO);

    /**
     * 删除素材知识库表格信息
     *
     * @param id 编号
     */
    void deleteMaterialLibraryTableColumn(Long id);

    /**
     * 删除素材知识库表格信息
     *
     * @param libraryId 素材库编号
     */
    void deleteMaterialLibraryTableColumnByLibraryId(Long libraryId);

    /**
     * 获得素材知识库表格信息
     *
     * @param id 编号
     * @return 素材知识库表格信息
     */
    MaterialLibraryTableColumnDO getMaterialLibraryTableColumn(Long id);

    /**
     * 获得素材知识库表格信息分页
     *
     * @param pageReqVO 分页查询
     * @return 素材知识库表格信息分页
     */
    PageResult<MaterialLibraryTableColumnDO> getMaterialLibraryTableColumnPage(MaterialLibraryTableColumnPageReqVO pageReqVO);

    /**
     * 根据素材库编号获得素材知识库表格信息
     *
     * @param libraryId 编号
     * @return 素材知识库表格信息列表
     */
    List<MaterialLibraryTableColumnDO> getMaterialLibraryTableColumnByLibrary(Long libraryId);


    /**
     * 根据素材库编号获得素材知识库表格信息
     *
     * @param libraryId 编号
     * @return 素材知识库表格信息列表
     */
    List<MaterialLibraryTableColumnRespVO> getMaterialLibraryTableColumnByLibraryAndName(Long libraryId);


    /**
     * 批量更新表格字段
     *
     * @param batchSaveReqVO 批量更新 VO
     */
    void updateBatchByLibraryId(MaterialLibraryTableColumnBatchSaveReqVO batchSaveReqVO);


    /**
     * 仅仅复制一个新的素材库表头templateLibraryId -> libraryId
     *
     * @param templateLibraryId 模板素材库编号
     * @param libraryId         素材库编号
     */
    void materialLibraryCopy(Long templateLibraryId, Long libraryId);

    /**
     * 切换绑定校验
     *
     * @param saveReqVO 换绑 VO
     */
    Boolean validateSwitchBind(MaterialLibraryAppBindSaveReqVO saveReqVO);

    void updateColumn(String sourceUid, String targetUid);
}