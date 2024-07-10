package com.starcloud.ops.business.app.service.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibrarySaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceAppReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceUseRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
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
     * 通过应用名称创建素材知识库
     *
     * @param appName 应用名称
     * @return 编号
     */
    String createMaterialLibraryByApp(String appName);


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
     * 通过素材库UID 获取 素材库详情
     *
     * @param uid 素材库UID
     * @return 素材库详情
     */
    MaterialLibraryRespVO getMaterialLibraryByUid(String uid);


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

    /**
     * 应用发布，直接复制一份新的素材库出来（版本管理）
     *
     * @param appReqVO 应用中绑定的数据
     * @return 素材库 UID
     */
    List<String> materialLibraryCopy(List<MaterialLibrarySliceAppReqVO> appReqVO);


    /**
     * 素材数据迁移
     *
     * @param appName               应用名称
     * @param tableColumnSaveReqVOS 表头存储 VO
     * @param materialList          素材数据
     * @return 素材库 UID
     */
    String materialLibraryDataMigration(String appName, List<MaterialLibraryTableColumnSaveReqVO> tableColumnSaveReqVOS, List<Map<String, Object>> materialList);

}