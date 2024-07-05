package com.starcloud.ops.business.app.service.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibrarySaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceAppReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceUseRespVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 素材知识库 Service 接口
 *
 * @author starcloudadmin
 */
public interface MaterialLibraryService {

    /**
     * 创建素材知识库
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMaterialLibrary(@Valid MaterialLibrarySaveReqVO createReqVO);

    /**
     * 更新素材知识库
     *
     * @param updateReqVO 更新信息
     */
    void updateMaterialLibrary(@Valid MaterialLibrarySaveReqVO updateReqVO);

    /**
     * 删除素材知识库
     *
     * @param id 编号
     */
    void deleteMaterialLibrary(Long id);

    /**
     * 获得素材知识库
     *
     * @param id 编号
     * @return 素材知识库
     */
    MaterialLibraryDO getMaterialLibrary(Long id);

    /**
     * 获得素材知识库分页
     *
     * @param pageReqVO 分页查询
     * @return 素材知识库分页
     */
    PageResult<MaterialLibraryDO> getMaterialLibraryPage(MaterialLibraryPageReqVO pageReqVO);


    /**
     * 素材库验证-仅验证 不返回数据
     *
     * @param id 素材库编号
     */
    MaterialLibraryDO validateMaterialLibraryExists(Long id);

    /**
     * 导入素材库数据
     *
     * @param importRespVO 导入数据的 VO
     */
    void importMaterialData(MaterialLibraryImportReqVO importRespVO);

    /**
     * 导出模板
     *
     * @param id       素材库编号
     * @param response response
     */
    void exportTemplate(Long id, HttpServletResponse response);


    /**
     * 获取应用执行的素材
     *
     * @param appReqVO 素材库查询
     */
    List<MaterialLibrarySliceUseRespVO> getMaterialLibrarySliceList(List<MaterialLibrarySliceAppReqVO> appReqVO);
}