package com.starcloud.ops.business.app.service.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.*;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceAppReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceUseRespVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

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
    Long createSystemMaterialLibrary(@Valid MaterialLibrarySaveReqVO createReqVO);


    /**
     * 通过应用名称创建素材知识库
     *
     * @param appName 应用名称
     * @return 编号
     */
    Long createSystemMaterialLibrary(String appName, Integer libraryType);


    /**
     * 通过应用创建素材知识库
     *
     * @param appReqVO 应用名称
     */
    void createMaterialLibraryByApp(MaterialLibraryAppReqVO appReqVO);

    /**
     * 通过应用获取绑定的素材知识库
     *
     * @param appReqVO 应用查询 VO
     * @return 编号
     */
    MaterialLibraryRespVO getMaterialLibraryByApp(MaterialLibraryAppReqVO appReqVO);

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
     * 通过应用删除素材知识库
     *
     * @param appReqVO 编号
     */
    void deleteMaterialLibraryByApp(MaterialLibraryAppReqVO appReqVO);

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
    PageResult<MaterialLibraryPageRespVO> getMaterialLibraryPage(MaterialLibraryPageReqVO pageReqVO);


    /**
     * 素材库验证
     *
     * @param id 素材库编号
     */
    MaterialLibraryDO validateMaterialLibraryExists(Long id);


    /**
     * 素材库验证-仅验证 不返回数据
     *
     * @param uid 素材库编号
     */
    MaterialLibraryDO validateMaterialLibraryExists(String uid);

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
     * 获取应用执行的素材
     *
     * @param appReqVO 素材库查询
     */
    MaterialLibrarySliceUseRespVO getMaterialLibrarySlice(MaterialLibrarySliceAppReqVO appReqVO);

    /**
     * 素材库-》素材库的复制（包含全部数据）
     *
     * @param copyReqVO 模板复制素材库VO
     * @return 素材库 UID
     */
    Long materialLibraryCopy(MaterialLibraryCopyReqVO copyReqVO);

    /**
     * 老应用的素材库-》新应用的素材库的复制（包含全部数据）
     *
     * @param newApp 新应用
     * @param oldApp 老应用
     */
    void materialLibraryCopy(MaterialLibraryAppReqVO newApp, MaterialLibraryAppReqVO oldApp);

    /**
     * 仅仅复制一个新的素材库（不做数据操作）将新应用的配置 复制到老的素材库上
     *
     * @param libraryId 素材库编号
     * @return 素材库编号
     */
    Long materialLibraryCopy(Long libraryId, MaterialLibraryAppReqVO appReqVO);


    /**
     * 素材数据迁移
     *
     * @param sliceMigrationReqVO 迁移 VO
     */
    void materialLibraryDataMigration(SliceMigrationReqVO sliceMigrationReqVO);

    /**
     * 更新素材库插件配置
     *
     * @param loginUserId       用户编号
     * @param plugInConfigReqVO 插件配置
     */
    void updatePluginConfig(Long loginUserId, MaterialLibrarySavePlugInConfigReqVO plugInConfigReqVO);

    /**
     * 素材数据使用计数
     *
     * @param sliceUsageCountReqVO 素材计算 VO
     */
    void materialLibrarySliceUsageCount(SliceUsageCountReqVO sliceUsageCountReqVO);


    // /**
    //  * 更新素材库文件数量
    //  *
    //  * @param libraryId 素材库编号
    //  */
    // void updateMaterialLibraryFileCount(Long libraryId);


    // /**
    //  * 更新素材库文件数量
    //  *
    //  * @param libraryId 素材库编号
    //  */
    // void e x(Long libraryId);
    void exportData(MaterialLibraryExportReqVO reqVO, HttpServletResponse response);


}